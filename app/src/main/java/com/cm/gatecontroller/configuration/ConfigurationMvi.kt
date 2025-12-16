package com.cm.gatecontroller.configuration

import com.cm.gatecontroller.configuration.model.LampStatus
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.model.LedStatus

data class ConfigurationUiState(
    val version: String = "",
    val levelOpen: Int = 1,
    val levelClose: Int = 1,
    val lamp: UsageStatus = UsageStatus.USE,
    val buzzer: UsageStatus = UsageStatus.USE,
    val lampPosOn: LampStatus = LampStatus.OPENED,
    val lampPosOff: LampStatus = LampStatus.CLOSED,
    val ledOpenColor: LedStatus = LedStatus.OFF,
    val ledOpenPos: Int = 1,
    val ledClose: LedStatus = LedStatus.OFF,
    val ledClosePos: Int = 1,
    val loopA: UsageStatus = UsageStatus.USE,
    val loopB: UsageStatus = UsageStatus.USE,
    val delayTime: Int = 0,
    val relay1: UsageStatus = UsageStatus.USE,
    val relay2: UsageStatus = UsageStatus.USE,
    val isLoading: Boolean = true
)

sealed interface ConfigurationIntent {
    data object LoadInitialConfig : ConfigurationIntent
    data class SetLevelOpen(val level: Int) : ConfigurationIntent
    data class SetLevelClose(val level: Int) : ConfigurationIntent
    data class SetLamp(val status: UsageStatus) : ConfigurationIntent
    data class SetBuzzer(val status: UsageStatus) : ConfigurationIntent
    data class SetLampPosOn(val state: LampStatus) : ConfigurationIntent
    data class SetLampPosOff(val state: LampStatus) : ConfigurationIntent
    data class SetLedOpen(val color: LedStatus) : ConfigurationIntent
    data class SetLedOpenPos(val position: Int) : ConfigurationIntent
    data class SetLedClose(val color: LedStatus) : ConfigurationIntent
    data class SetLedClosePos(val position: Int) : ConfigurationIntent
    data class SetLoopA(val status: UsageStatus) : ConfigurationIntent
    data class SetLoopB(val status: UsageStatus) : ConfigurationIntent
    data class SetDelayTime(val time: Int) : ConfigurationIntent
    data class SetRelay1(val status: UsageStatus) : ConfigurationIntent
    data class SetRelay2(val status: UsageStatus) : ConfigurationIntent
    data object SaveConfig : ConfigurationIntent
    data object LoadConfig : ConfigurationIntent
    data object FactoryReset : ConfigurationIntent
    data object ShowRelayMap : ConfigurationIntent
}

sealed interface ConfigurationSideEffect {
    data class ShowToast(val message: String) : ConfigurationSideEffect
    data object ShowRelayMapDialog : ConfigurationSideEffect
}