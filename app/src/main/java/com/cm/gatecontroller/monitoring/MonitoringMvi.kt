package com.cm.gatecontroller.monitoring

import androidx.compose.runtime.Immutable
import com.cm.gatecontroller.monitoring.model.AccessStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus

@Immutable
data class MonitoringUiState(
    val version: String = "N/A",
    val gateState: AccessStatus = AccessStatus.CLOSE,
    val lampState: SwitchStatus = SwitchStatus.OFF,
    val ledState: LedStatus = LedStatus.OFF,
    val relay1State: SwitchStatus = SwitchStatus.OFF,
    val relay2State: SwitchStatus = SwitchStatus.OFF,
    val photo1State: SwitchStatus = SwitchStatus.OFF,
    val photo2State: SwitchStatus = SwitchStatus.OFF,
    val open1State: SwitchStatus = SwitchStatus.OFF,
    val open2State: SwitchStatus = SwitchStatus.OFF,
    val open3State: SwitchStatus = SwitchStatus.OFF,
    val close1State: SwitchStatus = SwitchStatus.OFF,
    val close2State: SwitchStatus = SwitchStatus.OFF,
    val close3State: SwitchStatus = SwitchStatus.OFF,
    val loopAState: SwitchStatus = SwitchStatus.OFF,
    val loopBState: SwitchStatus = SwitchStatus.OFF,
    val mainPower: String = "0.0V",
    val testCount: Int = 0,
    val delayTime: Int = 0,
    val isTestRunning: Boolean = false
)

sealed interface MonitoringIntent {
    data object ToggleTest : MonitoringIntent
}

sealed interface MonitoringSideEffect {
    data class ShowToast(val message: String) : MonitoringSideEffect
}