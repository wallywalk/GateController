package com.cm.gatecontroller.core.serial.model

enum class SwitchState { ON, OFF }
enum class LedColor { OFF, BLUE, GREEN, RED, WHITE }

// Monitoring States
enum class MonitoringGateState { OPEN, CLOSE }

// Configuration States
enum class UsageState { USE, UNUSE }
enum class ConfigPositionState { OPENING, OPENED, CLOSING, CLOSED }

// Board Test States
enum class BoardPositionState { LEFT, RIGHT }
enum class BoardGateState { OPENING, OPENED, CLOSING, CLOSED, STOP }

data class GateControllerState(
    // Common
    val version: String = "N/A",

    // Monitoring State
    val mGateState: MonitoringGateState? = null,
    val lampState: SwitchState? = null,
    val ledColor: LedColor? = null,
    val relay1: SwitchState? = null,
    val relay2: SwitchState? = null,
    val photo1: SwitchState? = null,
    val photo2: SwitchState? = null,
    val open1: SwitchState? = null,
    val close1: SwitchState? = null,
    val open2: SwitchState? = null,
    val close2: SwitchState? = null,
    val open3: SwitchState? = null,
    val close3: SwitchState? = null,
    val loopA_mon: SwitchState? = null,
    val loopB_mon: SwitchState? = null,
    val mainPower: String = "0.0V",
    val testCount: String = "0",
    val delayTime_mon: String = "0sec",
    val isTestRunning: Boolean = false,

    // Configuration State
    val levelOpen: Int = 1,
    val levelClose: Int = 1,
    val lampUsage: UsageState = UsageState.UNUSE,
    val buzzerUsage: UsageState = UsageState.UNUSE,
    val lampOnPosition: ConfigPositionState = ConfigPositionState.OPENING,
    val lampOffPosition: ConfigPositionState = ConfigPositionState.CLOSING,
    val ledOpenColor: LedColor = LedColor.OFF,
    val ledOpenPosition: ConfigPositionState = ConfigPositionState.OPENING,
    val ledCloseColor: LedColor = LedColor.OFF,
    val ledClosePosition: ConfigPositionState = ConfigPositionState.CLOSING,
    val loopA_conf: UsageState = UsageState.UNUSE,
    val loopB_conf: UsageState = UsageState.UNUSE,
    val delayTime_conf: Int = 30,
    val relay1Mode: Int = 4,
    val relay2Mode: Int = 10,

    // Board Test State
    val controlLamp: SwitchState = SwitchState.OFF,
    val controlRelay1: SwitchState = SwitchState.OFF,
    val controlRelay2: SwitchState = SwitchState.OFF,
    val controlLed: LedColor = LedColor.OFF,
    val controlPosition: BoardPositionState = BoardPositionState.LEFT,
    val inPhoto1: SwitchState = SwitchState.OFF,
    val inPhoto2: SwitchState = SwitchState.OFF,
    val inLoopA: SwitchState = SwitchState.OFF,
    val inLoopB: SwitchState = SwitchState.OFF,
    val inOpen1: SwitchState = SwitchState.OFF,
    val inOpen2: SwitchState = SwitchState.OFF,
    val inOpen3: SwitchState = SwitchState.OFF,
    val inClose1: SwitchState = SwitchState.OFF,
    val inClose2: SwitchState = SwitchState.OFF,
    val inClose3: SwitchState = SwitchState.OFF,
    val swOpen: SwitchState = SwitchState.OFF,
    val swClose: SwitchState = SwitchState.OFF,
    val boardGateState: BoardGateState = BoardGateState.STOP
)
