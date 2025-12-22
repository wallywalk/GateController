package com.cm.gatecontroller.configuration

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus

@Immutable
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
    val isLoading: Boolean = true,
    val progressMessage: String? = null
)

sealed interface ConfigIntent {
    data object Initialize : ConfigIntent
    data class SetLevelOpen(val level: Int) : ConfigIntent
    data class SetLevelClose(val level: Int) : ConfigIntent
    data class SetLamp(val use: Boolean) : ConfigIntent
    data class SetBuzzer(val use: Boolean) : ConfigIntent
    data class SetLampPosOn(val on: Boolean) : ConfigIntent
    data class SetLampPosOff(val on: Boolean) : ConfigIntent
    data class SetLedOpen(val color: LedStatus) : ConfigIntent
    data class SetLedOpenPos(val on: Boolean) : ConfigIntent
    data class SetLedClose(val color: LedStatus) : ConfigIntent
    data class SetLedClosePos(val on: Boolean) : ConfigIntent
    data class SetLoopA(val use: Boolean) : ConfigIntent
    data class SetLoopB(val use: Boolean) : ConfigIntent
    data class SetDelayTime(val time: Int) : ConfigIntent
    data class SetRelay1(val value: Int) : ConfigIntent
    data class SetRelay2(val value: Int) : ConfigIntent
    data object FactoryReset : ConfigIntent
    data object SaveConfig : ConfigIntent
    data class SaveConfigToUri(val uri: Uri) : ConfigIntent
    data object LoadConfig : ConfigIntent
    data class LoadConfigFromUri(val uri: Uri) : ConfigIntent
    data object ShowRelayMap : ConfigIntent
}

sealed interface ConfigSideEffect {
    data class ShowSnackbar(val message: String) : ConfigSideEffect
    data object ShowRelayMapDialog : ConfigSideEffect
    data object OpenFileForLoad : ConfigSideEffect
    data class CreateFileForSave(val fileName: String) : ConfigSideEffect
}