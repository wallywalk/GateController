package com.cm.gatecontroller.configuration

import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.LampPosition
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.UseState

sealed interface ConfigurationIntent {
    data object RefreshConfig : ConfigurationIntent
    data class SetOpenLevel(val level: Int) : ConfigurationIntent
    data class SetCloseLevel(val level: Int) : ConfigurationIntent
    data class SetLampUsage(val use: UseState) : ConfigurationIntent
    data class SetBuzzerUsage(val use: UseState) : ConfigurationIntent
    data class SetLampOnPosition(val position: LampPosition) : ConfigurationIntent
    data class SetLampOffPosition(val position: LampPosition) : ConfigurationIntent
    data class SetLedOpenColor(val color: LedColor) : ConfigurationIntent
    data class SetLedOpenPosition(val position: LampPosition) : ConfigurationIntent
    data class SetLedCloseColor(val color: LedColor) : ConfigurationIntent
    data class SetLedClosePosition(val position: LampPosition) : ConfigurationIntent
    data class SetLoopAUsage(val use: UseState) : ConfigurationIntent
    data class SetLoopBUsage(val use: UseState) : ConfigurationIntent
    data class SetDelayTime(val time: Int) : ConfigurationIntent
    data class SetRelay1Mode(val mode: Int) : ConfigurationIntent
    data class SetRelay2Mode(val mode: Int) : ConfigurationIntent
    data object LoadConfig : ConfigurationIntent
    data object SaveConfig : ConfigurationIntent
    data object FactoryReset : ConfigurationIntent
    data object ShowRelayMap : ConfigurationIntent
}

data class ConfigurationUiState(
    val configState: GateControllerState = GateControllerState(),
    val isLoading: Boolean = true,
    val showConfirmDialog: Boolean = false,
    val confirmDialogAction: ConfigurationIntent? = null,
    val errorMessage: String? = null
)

sealed interface ConfigurationSideEffect {
    data class ShowSnackbar(val message: String) : ConfigurationSideEffect
    data class ShowConfirmDialog(val message: String, val action: ConfigurationIntent) : ConfigurationSideEffect
    data object LaunchFilePicker : ConfigurationSideEffect
    data object LaunchFileSaver : ConfigurationSideEffect
    data object ShowRelayMapDialog : ConfigurationSideEffect
}
