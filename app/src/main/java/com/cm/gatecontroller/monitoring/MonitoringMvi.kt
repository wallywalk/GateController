package com.cm.gatecontroller.monitoring

import com.cm.gatecontroller.core.serial.model.GateControllerState

sealed interface MonitoringIntent {
    data object RefreshStatus : MonitoringIntent
    data object ToggleTest : MonitoringIntent
}

data class MonitoringUiState(
    val deviceStatus: GateControllerState = GateControllerState(),
    val isLoading: Boolean = true
)

sealed interface MonitoringSideEffect {
    data class ShowSnackbar(val message: String) : MonitoringSideEffect
}
