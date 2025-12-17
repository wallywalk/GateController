package com.cm.gatecontroller.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.configuration.model.LampStatus
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.LampPosition
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.UseState
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
                    is ConfigIntent.SetLevelOpen -> serialRepository.setOpenLevel(intent.level)
                    is ConfigIntent.SetLevelClose -> serialRepository.setCloseLevel(intent.level)
                    is ConfigIntent.SetLamp -> serialRepository.setLampUsage(intent.status == UsageStatus.USE)
                    is ConfigIntent.SetBuzzer -> serialRepository.setBuzzerUsage(intent.status == UsageStatus.USE)
                    is ConfigIntent.SetLampPosOn -> serialRepository.setLampOnPosition(intent.state.name)
                    is ConfigIntent.SetLampPosOff -> serialRepository.setLampOffPosition(
                        intent.state.name // fixme: Int 값이 아님
                    )

                    is ConfigIntent.SetLedOpen -> serialRepository.setLedOpenColor(intent.color.name)
                    is ConfigIntent.SetLedOpenPos -> serialRepository.setLedOpenPosition(
                        intent.position.toString() // fixme: Int 값이 아님
                    )

                    is ConfigIntent.SetLedClose -> serialRepository.setLedCloseColor(intent.color.name)
                    is ConfigIntent.SetLedClosePos -> serialRepository.setLedClosePosition(
                        intent.position.toString() // fixme: Int 값이 아님
                    )

                    is ConfigIntent.SetLoopA -> serialRepository.setLoopAUsage(intent.status == UsageStatus.USE)
                    is ConfigIntent.SetLoopB -> serialRepository.setLoopBUsage(intent.status == UsageStatus.USE)
                    is ConfigIntent.SetDelayTime -> serialRepository.setDelayTime(intent.time)
                    is ConfigIntent.SetRelay1 -> serialRepository.setRelay1Mode(if (intent.status == UsageStatus.USE) 1 else 0)
                    is ConfigIntent.SetRelay2 -> serialRepository.setRelay2Mode(if (intent.status == UsageStatus.USE) 1 else 0)
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
                UseState.USE -> UsageStatus.USE
                UseState.UNUSE -> UsageStatus.UNUSE
            },
            buzzer = when (this.buzzerUsage) {
                UseState.USE -> UsageStatus.USE
                UseState.UNUSE -> UsageStatus.UNUSE
            },
            lampPosOn = when (this.lampOnPosition) {
                LampPosition.OPENING -> LampStatus.OPENING
                LampPosition.OPENED -> LampStatus.OPENED
                LampPosition.CLOSING -> LampStatus.CLOSING
                LampPosition.CLOSED -> LampStatus.CLOSED
            },
            lampPosOff = when (this.lampOffPosition) {
                LampPosition.OPENING -> LampStatus.OPENING
                LampPosition.OPENED -> LampStatus.OPENED
                LampPosition.CLOSING -> LampStatus.CLOSING
                LampPosition.CLOSED -> LampStatus.CLOSED
            },
            ledOpenColor = when (this.ledOpenColor) {
                LedColor.OFF -> LedStatus.OFF
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
            },
            ledOpenPos = this.ledOpenPosition.ordinal + 1,
            ledClose = when (this.ledCloseColor) {
                LedColor.OFF -> LedStatus.OFF
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
            },
            ledClosePos = this.ledClosePosition.ordinal + 1,
            loopA = when (this.loopA_conf) {
                UseState.USE -> UsageStatus.USE
                UseState.UNUSE -> UsageStatus.UNUSE
            },
            loopB = when (this.loopB_conf) {
                UseState.USE -> UsageStatus.USE
                UseState.UNUSE -> UsageStatus.UNUSE
            },
            delayTime = this.delayTime_conf,
            relay1 = if (this.relay1Mode != 0) UsageStatus.USE else UsageStatus.UNUSE,
            relay2 = if (this.relay2Mode != 0) UsageStatus.USE else UsageStatus.UNUSE,
            isLoading = false
        )
    }
}