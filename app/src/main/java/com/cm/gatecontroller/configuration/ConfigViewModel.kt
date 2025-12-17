package com.cm.gatecontroller.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.configuration.model.ConfigPositionStatus
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.ConfigPositionState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.UsageState
import com.cm.gatecontroller.model.LedStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val serialRepository: SerialRepository
) : ViewModel() {

    private val _sideEffect = MutableSharedFlow<ConfigSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    val uiState = serialRepository.deviceStatus
        .map { it.toConfigurationUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConfigUiState()
        )

    init {
        refreshConfiguration()
    }

    fun handleIntent(intent: ConfigIntent) {
        when (intent) {
            is ConfigIntent.ShowRelayMap -> showRelayMap()
            else -> viewModelScope.launch {
                when (intent) {
                    is ConfigIntent.LoadInitialConfig -> serialRepository.refreshConfiguration()
                    is ConfigIntent.FactoryReset -> serialRepository.factoryReset()
                    is ConfigIntent.SaveConfig -> { /* TODO: Implement file saving */
                    }

                    is ConfigIntent.LoadConfig -> { /* TODO: Implement file loading */
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun refreshConfiguration() {
        viewModelScope.launch {
            serialRepository.refreshConfiguration()
        }
    }

    private fun showRelayMap() {
        viewModelScope.launch {
            _sideEffect.emit(ConfigSideEffect.ShowRelayMapDialog)
        }
    }

    private fun GateControllerState.toConfigurationUiState(): ConfigUiState {
        return ConfigUiState(
            version = this.version,
            levelOpen = this.levelOpen,
            levelClose = this.levelClose,
            lamp = when (this.lampUsage) {
                UsageState.USE -> UsageStatus.USE
                UsageState.UNUSE -> UsageStatus.UNUSE
            },
            buzzer = when (this.buzzerUsage) {
                UsageState.USE -> UsageStatus.USE
                UsageState.UNUSE -> UsageStatus.UNUSE
            },
            lampPosOn = when (this.lampOnPosition) {
                ConfigPositionState.OPENING -> ConfigPositionStatus.OPENING
                ConfigPositionState.OPENED -> ConfigPositionStatus.OPENED
                ConfigPositionState.CLOSING -> ConfigPositionStatus.CLOSING
                ConfigPositionState.CLOSED -> ConfigPositionStatus.CLOSED
            },
            lampPosOff = when (this.lampOffPosition) {
                ConfigPositionState.OPENING -> ConfigPositionStatus.OPENING
                ConfigPositionState.OPENED -> ConfigPositionStatus.OPENED
                ConfigPositionState.CLOSING -> ConfigPositionStatus.CLOSING
                ConfigPositionState.CLOSED -> ConfigPositionStatus.CLOSED
            },
            ledOpenColor = when (this.ledOpenColor) {
                LedColor.OFF -> LedStatus.OFF
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
            },
            ledOpenPos = when (this.ledOpenPosition) {
                ConfigPositionState.OPENING -> ConfigPositionStatus.OPENING
                ConfigPositionState.OPENED -> ConfigPositionStatus.OPENED
                ConfigPositionState.CLOSING -> ConfigPositionStatus.CLOSING
                ConfigPositionState.CLOSED -> ConfigPositionStatus.CLOSED
            },
            ledClose = when (this.ledCloseColor) {
                LedColor.OFF -> LedStatus.OFF
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
            },
            ledClosePos = when (this.ledClosePosition) {
                ConfigPositionState.OPENING -> ConfigPositionStatus.OPENING
                ConfigPositionState.OPENED -> ConfigPositionStatus.OPENED
                ConfigPositionState.CLOSING -> ConfigPositionStatus.CLOSING
                ConfigPositionState.CLOSED -> ConfigPositionStatus.CLOSED
            },
            loopA = when (this.loopA_conf) {
                UsageState.USE -> UsageStatus.USE
                UsageState.UNUSE -> UsageStatus.UNUSE
            },
            loopB = when (this.loopB_conf) {
                UsageState.USE -> UsageStatus.USE
                UsageState.UNUSE -> UsageStatus.UNUSE
            },
            delayTime = this.delayTime_conf,
            relay1 = if (this.relay1Mode != 0) UsageStatus.USE else UsageStatus.UNUSE,
            relay2 = if (this.relay2Mode != 0) UsageStatus.USE else UsageStatus.UNUSE,
            isLoading = false
        )
    }
}