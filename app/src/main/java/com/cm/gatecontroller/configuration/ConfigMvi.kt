package com.cm.gatecontroller.configuration

import com.cm.gatecontroller.configuration.model.FactoryResult
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.model.LedStatus

data class ConfigUiState(
    val version: String = "N/A",
    val levelOpen: Int = 0,
    val levelClose: Int = 0,
    val lamp: UsageStatus = UsageStatus.UNUSE,
    val buzzer: UsageStatus = UsageStatus.UNUSE,
    val lampPosOn: GateStatus = GateStatus.STOP,
    val lampPosOff: GateStatus = GateStatus.STOP,
    val ledOpenColor: LedStatus = LedStatus.OFF,
    val ledOpenPos: GateStatus = GateStatus.STOP,
    val ledCloseColor: LedStatus = LedStatus.OFF,
    val ledClosePos: GateStatus = GateStatus.STOP,
    val loopA: UsageStatus = UsageStatus.UNUSE,
    val loopB: UsageStatus = UsageStatus.UNUSE,
    val delayTime: Int = 0,
    val relay1: Int = 0,
    val relay2: Int = 0,
    val factory: FactoryResult? = null,
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