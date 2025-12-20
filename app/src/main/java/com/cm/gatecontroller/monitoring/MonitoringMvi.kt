package com.cm.gatecontroller.monitoring

import androidx.compose.runtime.Immutable
import com.cm.gatecontroller.monitoring.model.ChannelMode
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus

@Immutable
data class MonitoringUiState(
    val version: String = "N/A",
    val channelMode: ChannelMode = ChannelMode.CLOSE,
    val lamp: SwitchStatus = SwitchStatus.OFF,
    val led: LedStatus = LedStatus.OFF,
    val relay1: SwitchStatus = SwitchStatus.OFF,
    val relay2: SwitchStatus = SwitchStatus.OFF,
    val photo1: SwitchStatus = SwitchStatus.OFF,
    val photo2: SwitchStatus = SwitchStatus.OFF,
    val open1: SwitchStatus = SwitchStatus.OFF,
    val open2: SwitchStatus = SwitchStatus.OFF,
    val open3: SwitchStatus = SwitchStatus.OFF,
    val close1: SwitchStatus = SwitchStatus.OFF,
    val close2: SwitchStatus = SwitchStatus.OFF,
    val close3: SwitchStatus = SwitchStatus.OFF,
    val loopA: SwitchStatus = SwitchStatus.OFF,
    val loopB: SwitchStatus = SwitchStatus.OFF,
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