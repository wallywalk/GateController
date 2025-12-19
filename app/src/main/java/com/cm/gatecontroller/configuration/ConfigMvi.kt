package com.cm.gatecontroller.configuration

import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.model.LedStatus

data class ConfigUiState(
    val version: String = "",
    val levelOpen: Int = 1,
    val levelClose: Int = 1,
    val lamp: UsageStatus = UsageStatus.USE,
    val buzzer: UsageStatus = UsageStatus.USE,
    val lampPosOn: GateStatus = GateStatus.OPENED,
    val lampPosOff: GateStatus = GateStatus.CLOSED,
    val ledOpenColor: LedStatus = LedStatus.OFF,
    val ledOpenPos: GateStatus = GateStatus.OPENED,
    val ledClose: LedStatus = LedStatus.OFF,
    val ledClosePos: GateStatus = GateStatus.OPENED,
    val loopA: UsageStatus = UsageStatus.USE,
    val loopB: UsageStatus = UsageStatus.USE,
    val delayTime: Int = 0,
    val relay1: Int = 1,
    val relay2: Int = 1,
    val isLoading: Boolean = true
)

sealed interface ConfigIntent {
    data object LoadInitialConfig : ConfigIntent
    data class SetLevelOpen(val level: Int) : ConfigIntent
    data class SetLevelClose(val level: Int) : ConfigIntent
    data object SaveConfig : ConfigIntent
    data object LoadConfig : ConfigIntent
    data object FactoryReset : ConfigIntent
    data object ShowRelayMap : ConfigIntent
}

sealed interface ConfigSideEffect {
    data class ShowToast(val message: String) : ConfigSideEffect
    data object ShowRelayMapDialog : ConfigSideEffect
}