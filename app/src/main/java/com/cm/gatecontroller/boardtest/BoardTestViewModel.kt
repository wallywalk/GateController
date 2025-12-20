package com.cm.gatecontroller.boardtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.boardtest.model.PositionStatus
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.PositionState
import com.cm.gatecontroller.core.serial.model.SwitchState
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardTestViewModel @Inject constructor(
    private val serialRepository: SerialRepository
) : ViewModel() {

    private val _sideEffect = Channel<BoardTestSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    val uiState: StateFlow<BoardTestUiState> = serialRepository.deviceStatus
        .map { it.toBoardTestUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BoardTestUiState(isLoading = true)
        )

    init {
        handleIntent(BoardTestIntent.Initialize)
    }

    fun handleIntent(intent: BoardTestIntent) {
        viewModelScope.launch {
            when (intent) {
                is BoardTestIntent.Initialize -> serialRepository.requestVersion()
                is BoardTestIntent.ToggleLamp -> {
                    val isOn = uiState.value.lamp == SwitchStatus.ON
                    serialRepository.setControlLamp(!isOn)
                }

                is BoardTestIntent.ToggleRelay1 -> {
                    val isOn = uiState.value.relay1 == SwitchStatus.ON
                    serialRepository.setControlRelay1(!isOn)
                }

                is BoardTestIntent.ToggleRelay2 -> {
                    val isOn = uiState.value.relay2 == SwitchStatus.ON
                    serialRepository.setControlRelay2(!isOn)
                }

                is BoardTestIntent.SelectLed -> serialRepository.setControlLed(intent.color.name)
                is BoardTestIntent.RequestPosition -> serialRepository.requestPosition()
                is BoardTestIntent.ToggleGateOpen -> serialRepository.openGateTest()
                is BoardTestIntent.ToggleGateClose -> serialRepository.closeGateTest()
                is BoardTestIntent.ToggleGateStop -> serialRepository.stopGateTest()
            }
        }
    }

    private fun GateControllerState.toBoardTestUiState(): BoardTestUiState {
        return BoardTestUiState(
            version = this.version,
            lamp = if (this.controlLamp == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            relay1 = if (this.controlRelay1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            relay2 = if (this.controlRelay2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            led = when (this.controlLed) {
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
                else -> LedStatus.OFF
            },
            position = when (this.controlPosition) {
                PositionState.LEFT -> PositionStatus.LEFT
                else -> PositionStatus.RIGHT
            },
            photo1 = if (this.stPhoto1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            photo2 = if (this.stPhoto2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            loopA = if (this.inLoopA == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            loopB = if (this.inLoopB == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open1 = if (this.stOpen1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open2 = if (this.stOpen2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open3 = if (this.stOpen3 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            openSwitch = if (this.swOpen == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close1 = if (this.stClose1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close2 = if (this.stClose2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close3 = if (this.stClose3 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            closeSwitch = if (this.swClose == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            gateStatus = when (this.gateState) {
                GateState.OPENING -> GateStatus.OPENING
                GateState.OPENED -> GateStatus.OPENED
                GateState.CLOSING -> GateStatus.CLOSING
                GateState.CLOSED -> GateStatus.CLOSED
                GateState.STOP -> GateStatus.STOP
                null -> GateStatus.STOP
            },
            isLoading = false
        )
    }
}
