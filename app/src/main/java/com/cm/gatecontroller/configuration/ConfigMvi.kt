package com.cm.gatecontroller.configuration

import com.cm.gatecontroller.configuration.model.FactoryResult
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
    val factory: FactoryResult = FactoryResult.ERROR,
    val isLoading: Boolean = true
)

sealed interface ConfigIntent {
    data object Initialize : ConfigIntent
    data class SetLevelOpen(val level: Int) : ConfigIntent
    data class SetLevelClose(val level: Int) : ConfigIntent
    data class SetLamp(val status: UsageStatus) : ConfigIntent
    data class SetBuzzer(val status: UsageStatus) : ConfigIntent
    data class SetLampPosOn(val state: GateStatus) : ConfigIntent
    data class SetLampPosOff(val state: GateStatus) : ConfigIntent
    data class SetLedOpen(val color: LedStatus) : ConfigIntent
    data class SetLedOpenPos(val position: Int) : ConfigIntent
    data class SetLedClose(val color: LedStatus) : ConfigIntent
    data class SetLedClosePos(val position: Int) : ConfigIntent
    data class SetLoopA(val status: UsageStatus) : ConfigIntent
    data class SetLoopB(val status: UsageStatus) : ConfigIntent
    data class SetDelayTime(val time: Int) : ConfigIntent
    data class SetRelay1(val status: UsageStatus) : ConfigIntent
    data class SetRelay2(val status: UsageStatus) : ConfigIntent
    data object SaveConfig : ConfigIntent
    data object LoadConfig : ConfigIntent
    data object FactoryReset : ConfigIntent
    data object ShowRelayMap : ConfigIntent
}

sealed interface ConfigSideEffect {
    data class ShowToast(val message: String) : ConfigSideEffect
    data object ShowRelayMapDialog : ConfigSideEffect
}