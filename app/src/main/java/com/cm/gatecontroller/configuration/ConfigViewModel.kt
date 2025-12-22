package com.cm.gatecontroller.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.configuration.model.ConfigData
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.core.logger.Logger
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.FactoryResponse
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.UsageState
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import android.net.Uri

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val serialRepository: SerialRepository,
    private val configFileRepository: ConfigFileRepository,
    private val logger: Logger,
) : ViewModel() {

    private val _sideEffect = MutableSharedFlow<ConfigSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val _progressMessage = MutableStateFlow<String?>(null)

    val uiState = combine(
        serialRepository.deviceStatus.map { it.toConfigurationUiState() },
        _progressMessage
    ) { state, progress ->
        state.copy(progressMessage = progress)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConfigUiState()
    )

    init {
        logger.d(TAG, "ViewModel initialized")
        refreshConfiguration()
        observeFactoryReset()
    }

    fun handleIntent(intent: ConfigIntent) {
        viewModelScope.launch {
            when (intent) {
                is ConfigIntent.Initialize -> {
                    logger.d(TAG, "Intent: Initialize")
                    serialRepository.refreshConfiguration()
                }

                is ConfigIntent.SetLevelOpen -> serialRepository.setOpenLevel(intent.level)
                is ConfigIntent.SetLevelClose -> serialRepository.setCloseLevel(intent.level)
                is ConfigIntent.SetLamp -> serialRepository.setLampUsage(intent.use)
                is ConfigIntent.SetBuzzer -> serialRepository.setBuzzerUsage(intent.use)
                is ConfigIntent.SetLampPosOn -> serialRepository.setLampOnPosition(intent.on)
                is ConfigIntent.SetLampPosOff -> serialRepository.setLampOffPosition(intent.on)
                is ConfigIntent.SetLedOpen -> serialRepository.setLedOpenColor(intent.color.name)
                is ConfigIntent.SetLedOpenPos -> serialRepository.setLedOpenPosition(intent.on)
                is ConfigIntent.SetLedClose -> serialRepository.setLedCloseColor(intent.color.name)
                is ConfigIntent.SetLedClosePos -> serialRepository.setLedClosePosition(intent.on)
                is ConfigIntent.SetLoopA -> serialRepository.setLoopAUsage(intent.use)
                is ConfigIntent.SetLoopB -> serialRepository.setLoopBUsage(intent.use)
                is ConfigIntent.SetDelayTime -> serialRepository.setDelayTime(intent.time)
                is ConfigIntent.SetRelay1 -> serialRepository.setRelay1Mode(intent.value)
                is ConfigIntent.SetRelay2 -> serialRepository.setRelay2Mode(intent.value)
                is ConfigIntent.FactoryReset -> {
                    logger.d(TAG, "Intent: FactoryReset")
                    serialRepository.factoryReset()
                }

                is ConfigIntent.LoadConfig -> {
                    logger.d(TAG, "Intent: LoadConfig")
                    viewModelScope.launch {
                        _sideEffect.emit(ConfigSideEffect.OpenFileForLoad)
                    }
                }

                is ConfigIntent.LoadConfigFromUri -> {
                    logger.d(TAG, "Intent: LoadConfigFromUri, uri=${intent.uri}")
                    loadConfigurationFromFile(intent.uri)
                }

                is ConfigIntent.SaveConfig -> {
                    logger.d(TAG, "Intent: SaveConfig")
                    viewModelScope.launch {
                        val timestamp =
                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val fileName = "pgc2a_$timestamp.cfg"
                        _sideEffect.emit(ConfigSideEffect.CreateFileForSave(fileName))
                    }
                }

                is ConfigIntent.SaveConfigToUri -> {
                    logger.d(TAG, "Intent: SaveConfigToUri, uri=${intent.uri}")
                    saveConfigurationToFile(intent.uri)
                }

                is ConfigIntent.ShowRelayMap -> {
                    logger.d(TAG, "Intent: ShowRelayMap")
                    showRelayMap()
                }
            }
        }
    }

    private suspend fun saveConfigurationToFile(uri: Uri) {
        logger.d(TAG, "Saving configuration to: $uri")
        _progressMessage.value = "Saving config..."
        
        configFileRepository.saveConfiguration(uri, uiState.value.toConfigData())
            .onSuccess {
                logger.d(TAG, "Configuration save process completed.")
                _sideEffect.emit(ConfigSideEffect.ShowSnackbar("Configuration saved successfully."))
            }
            .onFailure { e ->
                logger.e(TAG, "Error saving file", e)
                _sideEffect.emit(ConfigSideEffect.ShowSnackbar("Error saving file: ${e.message}"))
            }

        _progressMessage.value = null
    }

    private suspend fun loadConfigurationFromFile(uri: Uri) {
        logger.d(TAG, "Loading configuration from: $uri")
        _progressMessage.value = "Reading config file..."

        configFileRepository.loadConfiguration(uri)
            .onSuccess { configMap ->
                logger.d(TAG, "Parsed config: $configMap")
                val validationError = validateConfigMap(configMap)
                if (validationError != null) {
                    logger.e(TAG, "Validation error: $validationError")
                    _sideEffect.emit(ConfigSideEffect.ShowSnackbar(validationError))
                    _progressMessage.value = null
                    return@onSuccess
                }
                logger.d(TAG, "Config validation successful.")
                applyConfigToDevice(configMap)
            }
            .onFailure { e ->
                logger.e(TAG, "Error loading or applying file", e)
                _sideEffect.emit(ConfigSideEffect.ShowSnackbar("Error loading file: ${e.message}"))
                _progressMessage.value = null
            }
    }

    private fun validateConfigMap(map: Map<String, String>): String? {
        val requiredKeys = listOf(
            "LEVELOPEN", "LEVELCLOSE", "LAMP", "BUZZER", "LAMPON", "LAMPOFF",
            "LEDOPEN", "LEDOPENPOS", "LEDCLOSE", "LEDCLOSEPOS", "LOOPA", "LOOPB",
            "DELAY", "RELAY1", "RELAY2"
        )
        for (key in requiredKeys) {
            if (!map.containsKey(key)) return "Validation failed: Missing key '$key'"
        }
        return null
    }

    private suspend fun applyConfigToDevice(configMap: Map<String, String>) {
        logger.d(TAG, "Applying config to device...")
        val commands = mutableListOf<Pair<String, suspend () -> Unit>>()

        configMap["LEVELOPEN"]?.toIntOrNull()
            ?.let { v -> commands.add("SetLevelOpen($v)" to { serialRepository.setOpenLevel(v) }) }
        configMap["LEVELCLOSE"]?.toIntOrNull()
            ?.let { v -> commands.add("SetLevelClose($v)" to { serialRepository.setCloseLevel(v) }) }
        configMap["LAMP"]?.let { v ->
            commands.add("SetLamp($v)" to {
                serialRepository.setLampUsage(
                    v == "USE"
                )
            })
        }
        configMap["BUZZER"]?.let { v ->
            commands.add("SetBuzzer($v)" to {
                serialRepository.setBuzzerUsage(
                    v == "USE"
                )
            })
        }
        configMap["LAMPON"]?.let { v ->
            commands.add("SetLampPosOn($v)" to {
                serialRepository.setLampOnPosition(
                    v == "OPENING"
                )
            })
        }
        configMap["LAMPOFF"]?.let { v ->
            commands.add("SetLampPosOff($v)" to {
                serialRepository.setLampOffPosition(
                    v == "CLOSING"
                )
            })
        }
        configMap["LEDOPEN"]?.let { v ->
            commands.add("SetLedOpen($v)" to {
                serialRepository.setLedOpenColor(
                    v
                )
            })
        }
        configMap["LEDOPENPOS"]?.let { v ->
            commands.add("SetLedOpenPos($v)" to {
                serialRepository.setLedOpenPosition(
                    v == "OPENING"
                )
            })
        }
        configMap["LEDCLOSE"]?.let { v ->
            commands.add("SetLedClose($v)" to {
                serialRepository.setLedCloseColor(
                    v
                )
            })
        }
        configMap["LEDCLOSEPOS"]?.let { v ->
            commands.add("SetLedClosePos($v)" to {
                serialRepository.setLedClosePosition(
                    v == "CLOSING"
                )
            })
        }
        configMap["LOOPA"]?.let { v ->
            commands.add("SetLoopA($v)" to {
                serialRepository.setLoopAUsage(
                    v == "USE"
                )
            })
        }
        configMap["LOOPB"]?.let { v ->
            commands.add("SetLoopB($v)" to {
                serialRepository.setLoopBUsage(
                    v == "USE"
                )
            })
        }
        configMap["DELAY"]?.toIntOrNull()
            ?.let { v -> commands.add("SetDelayTime($v)" to { serialRepository.setDelayTime(v) }) }
        configMap["RELAY1"]?.toIntOrNull()
            ?.let { v -> commands.add("SetRelay1($v)" to { serialRepository.setRelay1Mode(v) }) }
        configMap["RELAY2"]?.toIntOrNull()
            ?.let { v -> commands.add("SetRelay2($v)" to { serialRepository.setRelay2Mode(v) }) }

        try {
            for ((index, command) in commands.withIndex()) {
                val (desc, action) = command
                _progressMessage.value = "Applying config ${index + 1}/${commands.size}"
                logger.d(TAG, "Applying [${index + 1}/${commands.size}]: $desc")
                action.invoke()
                delay(150) // Small delay to prevent flooding the serial port
            }
            _progressMessage.value = "Configuration applied."
            _sideEffect.emit(ConfigSideEffect.ShowSnackbar("Configuration loaded and applied successfully."))
            logger.d(TAG, "Finished applying config to device.")
            delay(2000)
        } catch (e: Exception) {
            logger.e(TAG, "An error occurred during apply", e)
            _sideEffect.emit(ConfigSideEffect.ShowSnackbar("An error occurred during apply: ${e.message}"))
        } finally {
            _progressMessage.value = null
            refreshConfiguration() // Refresh state from device
        }
    }

    private fun refreshConfiguration() {
        viewModelScope.launch {
            logger.d(TAG, "Refreshing configuration...")
            serialRepository.refreshConfiguration()
        }
    }

    private fun showRelayMap() {
        viewModelScope.launch {
            _sideEffect.emit(ConfigSideEffect.ShowRelayMapDialog)
        }
    }

    private fun observeFactoryReset() {
        viewModelScope.launch {
            serialRepository.deviceStatus
                .map { it.factory }
                .distinctUntilChanged()
                .filterNotNull()
                .collect { factoryResult ->
                    val message = when (factoryResult) {
                        FactoryResponse.OK -> "Factory reset success."
                        FactoryResponse.ERROR -> "Factory reset failed."
                    }
                    logger.d(TAG, "FactoryReset result: $message")
                    _sideEffect.emit(ConfigSideEffect.ShowSnackbar(message))
                }
        }
    }

    private fun ConfigUiState.toConfigData() = ConfigData(
        version = this.version,
        levelOpen = this.levelOpen,
        levelClose = this.levelClose,
        lamp = this.lamp,
        buzzer = this.buzzer,
        lampPosOn = this.lampPosOn,
        lampPosOff = this.lampPosOff,
        ledOpenColor = this.ledOpenColor,
        ledOpenPos = this.ledOpenPos,
        ledCloseColor = this.ledCloseColor,
        ledClosePos = this.ledClosePos,
        loopA = this.loopA,
        loopB = this.loopB,
        delayTime = this.delayTime,
        relay1 = this.relay1,
        relay2 = this.relay2
    )

    private fun GateControllerState.toConfigurationUiState(): ConfigUiState {
        return ConfigUiState(
            version = this.version,
            levelOpen = this.levelOpen,
            levelClose = this.levelClose,
            lamp = when (this.lampUsage) {
                UsageState.USE -> UsageStatus.USE
                else -> UsageStatus.UNUSE
            },
            buzzer = when (this.buzzerUsage) {
                UsageState.USE -> UsageStatus.USE
                else -> UsageStatus.UNUSE
            },
            lampPosOn = when (this.lampPositionOn) {
                GateState.OPENING -> GateStatus.OPENING
                GateState.OPENED -> GateStatus.OPENED
                GateState.CLOSING -> GateStatus.CLOSING
                GateState.CLOSED -> GateStatus.CLOSED
                else -> GateStatus.STOP
            },
            lampPosOff = when (this.lampPositionOff) {
                GateState.OPENING -> GateStatus.OPENING
                GateState.OPENED -> GateStatus.OPENED
                GateState.CLOSING -> GateStatus.CLOSING
                GateState.CLOSED -> GateStatus.CLOSED
                else -> GateStatus.STOP
            },
            ledOpenColor = when (this.ledOpenColor) {
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
                else -> LedStatus.OFF
            },
            ledOpenPos = when (this.ledOpenPosition) {
                GateState.OPENING -> GateStatus.OPENING
                GateState.OPENED -> GateStatus.OPENED
                GateState.CLOSING -> GateStatus.CLOSING
                GateState.CLOSED -> GateStatus.CLOSED
                else -> GateStatus.STOP
            },
            ledCloseColor = when (this.ledCloseColor) {
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
                else -> LedStatus.OFF
            },
            ledClosePos = when (this.ledClosePosition) {
                GateState.OPENING -> GateStatus.OPENING
                GateState.OPENED -> GateStatus.OPENED
                GateState.CLOSING -> GateStatus.CLOSING
                GateState.CLOSED -> GateStatus.CLOSED
                else -> GateStatus.STOP
            },
            loopA = when (this.setLoopA) {
                UsageState.USE -> UsageStatus.USE
                else -> UsageStatus.UNUSE
            },
            loopB = when (this.setLoopB) {
                UsageState.USE -> UsageStatus.USE
                else -> UsageStatus.UNUSE
            },
            delayTime = this.configDelayTime,
            relay1 = this.setRelay1,
            relay2 = this.setRelay2,
            isLoading = false
        )
    }

    companion object {
        private const val TAG = "ConfigViewModel"
    }
}