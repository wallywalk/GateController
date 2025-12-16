package com.cm.gatecontroller.monitoring

import androidx.compose.runtime.Immutable

sealed interface MonitoringIntent {
    data object ToggleTest : MonitoringIntent
}

@Immutable
data class MonitoringUiState(
    val version: String = "N/A",
    val gateState: GateStatus = GateStatus.CLOSE,
    val lampState: OnOffStatus = OnOffStatus.OFF,
    val ledState: LedStatus = LedStatus.OFF,
    val relay1State: OnOffStatus = OnOffStatus.OFF,
    val relay2State: OnOffStatus = OnOffStatus.OFF,
    val photo1State: OnOffStatus = OnOffStatus.OFF,
    val photo2State: OnOffStatus = OnOffStatus.OFF,
    val open1State: OnOffStatus = OnOffStatus.OFF,
    val open2State: OnOffStatus = OnOffStatus.OFF,
    val open3State: OnOffStatus = OnOffStatus.OFF,
    val close1State: OnOffStatus = OnOffStatus.OFF,
    val close2State: OnOffStatus = OnOffStatus.OFF,
    val close3State: OnOffStatus = OnOffStatus.OFF,
    val loopAState: OnOffStatus = OnOffStatus.OFF,
    val loopBState: OnOffStatus = OnOffStatus.OFF,
    val mainPower: String = "0.0V",
    val testCount: Int = 0,
    val delayTime: Int = 0,
    val isTestRunning: Boolean = false
)

enum class GateStatus { OPEN, CLOSE }
enum class OnOffStatus { ON, OFF }
enum class LedStatus { OFF, BLUE, GREEN, RED, WHITE }

sealed interface MonitoringSideEffect {
    data class ShowToast(val message: String) : MonitoringSideEffect
}