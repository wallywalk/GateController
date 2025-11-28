package com.cm.gatecontroller.configuration

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.UseState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val serialRepository: SerialRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfigurationUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<ConfigurationSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        serialRepository.deviceStatus
            .onEach { state ->
                _uiState.update { it.copy(configState = state, isLoading = false) }
            }
            .catch { e ->
                _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Error: ${e.message}"))
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)

        refreshConfig()
    }

    fun handleIntent(intent: ConfigurationIntent) {
        viewModelScope.launch {
            when (intent) {
                ConfigurationIntent.RefreshConfig -> refreshConfig()
                is ConfigurationIntent.SetOpenLevel -> serialRepository.setOpenLevel(intent.level)
                is ConfigurationIntent.SetCloseLevel -> serialRepository.setCloseLevel(intent.level)
                is ConfigurationIntent.SetLampUsage -> serialRepository.setLampUsage(intent.use == UseState.USE)
                is ConfigurationIntent.SetBuzzerUsage -> serialRepository.setBuzzerUsage(intent.use == UseState.USE)
                is ConfigurationIntent.SetLampOnPosition -> serialRepository.setLampOnPosition(intent.position.name)
                is ConfigurationIntent.SetLampOffPosition -> serialRepository.setLampOffPosition(intent.position.name)
                is ConfigurationIntent.SetLedOpenColor -> serialRepository.setLedOpenColor(intent.color.name)
                is ConfigurationIntent.SetLedOpenPosition -> serialRepository.setLedOpenPosition(intent.position.name)
                is ConfigurationIntent.SetLedCloseColor -> serialRepository.setLedCloseColor(intent.color.name)
                is ConfigurationIntent.SetLedClosePosition -> serialRepository.setLedClosePosition(intent.position.name)
                is ConfigurationIntent.SetLoopAUsage -> serialRepository.setLoopAUsage(intent.use == UseState.USE)
                is ConfigurationIntent.SetLoopBUsage -> serialRepository.setLoopBUsage(intent.use == UseState.USE)
                is ConfigurationIntent.SetDelayTime -> serialRepository.setDelayTime(intent.time)
                is ConfigurationIntent.SetRelay1Mode -> serialRepository.setRelay1Mode(intent.mode)
                is ConfigurationIntent.SetRelay2Mode -> serialRepository.setRelay2Mode(intent.mode)
                ConfigurationIntent.LoadConfig -> _sideEffect.send(ConfigurationSideEffect.LaunchFilePicker)
                ConfigurationIntent.SaveConfig -> _sideEffect.send(ConfigurationSideEffect.LaunchFileSaver)
                ConfigurationIntent.FactoryReset -> _sideEffect.send(ConfigurationSideEffect.ShowConfirmDialog("Are you sure you want to factory reset?", intent))
                ConfigurationIntent.ShowRelayMap -> _sideEffect.send(ConfigurationSideEffect.ShowRelayMapDialog)
                is ConfigurationIntent.FileSelectedForLoad -> loadConfigFromFile(intent.uri)
                is ConfigurationIntent.FileSelectedForSave -> saveConfigToFile(intent.uri)
            }
        }
    }

    fun confirmFactoryReset() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = serialRepository.factoryReset()
            if (result.isSuccess) {
                _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Factory reset successful!"))
            } else {
                _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Factory reset failed: ${result.exceptionOrNull()?.message}"))
            }
            _uiState.update { it.copy(isLoading = false) }
            refreshConfig()
        }
    }

    private fun refreshConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                serialRepository.refreshConfiguration()
            } catch (e: Exception) {
                _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Failed to refresh config: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadConfigFromFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val jsonString = reader.readText()
                    val loadedState = Json.decodeFromString<GateControllerState>(jsonString)

                    // Apply loaded state to device
                    applyConfigToDevice(loadedState)
                    _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Configuration loaded and applied from file."))
                } ?: _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Failed to open file for loading."))
            } catch (e: Exception) {
                _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Error loading config: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun saveConfigToFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val jsonString = Json.encodeToString(uiState.value.configState)
                    outputStream.write(jsonString.toByteArray())
                    _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Configuration saved to file."))
                } ?: _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Failed to open file for saving."))
            } catch (e: Exception) {
                _sideEffect.send(ConfigurationSideEffect.ShowSnackbar("Error saving config: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun applyConfigToDevice(state: GateControllerState) {
        serialRepository.setOpenLevel(state.openLevel)
        serialRepository.setCloseLevel(state.closeLevel)
        serialRepository.setLampUsage(state.lampUsage == UseState.USE)
        serialRepository.setBuzzerUsage(state.buzzerUsage == UseState.USE)
        serialRepository.setLampOnPosition(state.lampOnPosition.name)
        serialRepository.setLampOffPosition(state.lampOffPosition.name)
        serialRepository.setLedOpenColor(state.ledOpenColor.name)
        serialRepository.setLedOpenPosition(state.ledOpenPosition.name)
        serialRepository.setLedCloseColor(state.ledCloseColor.name)
        serialRepository.setLedClosePosition(state.ledClosePosition.name)
        serialRepository.setLoopAUsage(state.loopA_conf == UseState.USE)
        serialRepository.setLoopBUsage(state.loopB_conf == UseState.USE)
        serialRepository.setDelayTime(state.delayTime_conf)
        serialRepository.setRelay1Mode(state.relay1Mode)
        serialRepository.setRelay2Mode(state.relay2Mode)
        serialRepository.refreshConfiguration()
    }
}