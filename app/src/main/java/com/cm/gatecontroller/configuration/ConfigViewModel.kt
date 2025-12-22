package com.cm.gatecontroller.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.FactoryResponse
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.UsageState
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
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
        observeFactoryReset()
    }

    fun handleIntent(intent: ConfigIntent) {
        viewModelScope.launch {
            when (intent) {
                is ConfigIntent.Initialize -> serialRepository.refreshConfiguration()
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
                is ConfigIntent.FactoryReset -> serialRepository.factoryReset()
                is ConfigIntent.SaveConfig, is ConfigIntent.LoadConfig -> {
                    viewModelScope.launch {
                        _sideEffect.emit(ConfigSideEffect.ShowToast("Not implemented yet"))
                    }
                }

                is ConfigIntent.ShowRelayMap -> showRelayMap()
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
                    _sideEffect.emit(ConfigSideEffect.ShowToast(message))
                }
        }
    }

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
}