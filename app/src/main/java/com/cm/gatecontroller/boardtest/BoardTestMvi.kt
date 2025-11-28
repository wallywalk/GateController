package com.cm.gatecontroller.boardtest

import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.PositionState

sealed interface BoardTestIntent {
    data object RefreshStatus : BoardTestIntent

    // Output Test
    data class ToggleControlLamp(val on: Boolean) : BoardTestIntent
    data class ToggleControlRelay1(val on: Boolean) : BoardTestIntent
    data class ToggleControlRelay2(val on: Boolean) : BoardTestIntent
    data class SetControlLed(val color: LedColor) : BoardTestIntent
    data class SetControlPosition(val position: PositionState) : BoardTestIntent

    // Operation Test
    data object OpenGate : BoardTestIntent
    data object CloseGate : BoardTestIntent
    data object StopGate : BoardTestIntent
}

data class BoardTestUiState(
    val testState: GateControllerState = GateControllerState(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface BoardTestSideEffect {
    data class ShowSnackbar(val message: String) : BoardTestSideEffect
}
