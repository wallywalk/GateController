package com.cm.gatecontroller.boardtest

import androidx.compose.runtime.Immutable
import com.cm.gatecontroller.boardtest.model.PositionStatus
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus

@Immutable
data class BoardTestUiState(
    val version: String = "N/A",
    val lamp: SwitchStatus = SwitchStatus.OFF,
    val relay1: SwitchStatus = SwitchStatus.OFF,
    val relay2: SwitchStatus = SwitchStatus.OFF,
    val led: LedStatus = LedStatus.OFF,
    val position: PositionStatus = PositionStatus.LEFT,
    val photo1: SwitchStatus = SwitchStatus.OFF,
    val photo2: SwitchStatus = SwitchStatus.OFF,
    val loopA: SwitchStatus = SwitchStatus.OFF,
    val loopB: SwitchStatus = SwitchStatus.OFF,
    val open1: SwitchStatus = SwitchStatus.OFF,
    val open2: SwitchStatus = SwitchStatus.OFF,
    val open3: SwitchStatus = SwitchStatus.OFF,
    val openSwitch: SwitchStatus = SwitchStatus.OFF,
    val close1: SwitchStatus = SwitchStatus.OFF,
    val close2: SwitchStatus = SwitchStatus.OFF,
    val close3: SwitchStatus = SwitchStatus.OFF,
    val closeSwitch: SwitchStatus = SwitchStatus.OFF,
    val gateStatus: GateStatus = GateStatus.STOP,
    val isGateOpen: Boolean = true,
    val isLoading: Boolean = false,
)

sealed interface BoardTestIntent {
    data object Initialize : BoardTestIntent
    data object ToggleLamp : BoardTestIntent
    data object ToggleRelay1 : BoardTestIntent
    data object ToggleRelay2 : BoardTestIntent
    data class SelectLed(val color: LedStatus) : BoardTestIntent
    data object ToggleGate : BoardTestIntent
    data object ToggleGateStop : BoardTestIntent
    data object RequestPosition : BoardTestIntent
}

sealed interface BoardTestSideEffect {
    data class ShowSnackbar(val message: String) : BoardTestSideEffect
}
