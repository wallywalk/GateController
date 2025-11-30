package com.cm.gatecontroller.core.serial.model

// Monitoring States
enum class GateState { OPEN, CLOSE }
enum class OnOff { ON, OFF }
enum class LedColor { OFF, BLUE, GREEN, RED, WHITE }

// Configuration States
enum class UseState { USE, UNUSE }
enum class LampPosition { OPENING_START, OPENED_STOP, CLOSING_START, CLOSED_STOP }
typealias LedOpenPosition = LampPosition
typealias LedClosePosition = LampPosition

// Board Test States
enum class PositionState { LEFT, RIGHT }
enum class GateStage { OPENING, OPENED, CLOSING, CLOSED, STOP }

data class GateControllerState(
    // Common
    val version: String = "N/A",

    // Monitoring State
    val gateState: GateState? = null,
    val lampState: OnOff? = null,
    val ledColor: LedColor? = null,
    val relay1: OnOff? = null,
    val relay2: OnOff? = null,
    val photo1: OnOff? = null,
    val photo2: OnOff? = null,
    val open1: OnOff? = null,
    val close1: OnOff? = null,
    val open2: OnOff? = null,
    val close2: OnOff? = null,
    val open3: OnOff? = null,
    val close3: OnOff? = null,
    val loopA_mon: OnOff? = null,
    val loopB_mon: OnOff? = null,
    val mainPower: String = "0.0V",
    val testCount: String = "0",
    val delayTime_mon: String = "0sec",
    val isTestRunning: Boolean = false,

    // Configuration State
    val openLevel: Int = 1,
    val closeLevel: Int = 1,
    val lampUsage: UseState = UseState.UNUSE,
    val buzzerUsage: UseState = UseState.UNUSE,
    val lampOnPosition: LampPosition = LampPosition.OPENING_START,
    val lampOffPosition: LampPosition = LampPosition.CLOSING_START,
    val ledOpenColor: LedColor = LedColor.OFF,
    val ledOpenPosition: LedOpenPosition = LedOpenPosition.OPENING_START,
    val ledCloseColor: LedColor = LedColor.OFF,
    val ledClosePosition: LedClosePosition = LedClosePosition.CLOSING_START,
    val loopA_conf: UseState = UseState.UNUSE,
    val loopB_conf: UseState = UseState.UNUSE,
    val delayTime_conf: Int = 30,
    val relay1Mode: Int = 4,
    val relay2Mode: Int = 10,

    // Board Test State
    val controlLamp: OnOff = OnOff.OFF,
    val controlRelay1: OnOff = OnOff.OFF,
    val controlRelay2: OnOff = OnOff.OFF,
    val controlLed: LedColor = LedColor.OFF,
    val controlPosition: PositionState = PositionState.LEFT,
    val inPhoto1: OnOff = OnOff.OFF,
    val inPhoto2: OnOff = OnOff.OFF,
    val inLoopA: OnOff = OnOff.OFF,
    val inLoopB: OnOff = OnOff.OFF,
    val inOpen1: OnOff = OnOff.OFF,
    val inOpen2: OnOff = OnOff.OFF,
    val inOpen3: OnOff = OnOff.OFF,
    val inClose1: OnOff = OnOff.OFF,
    val inClose2: OnOff = OnOff.OFF,
    val inClose3: OnOff = OnOff.OFF,
    val swOpen: OnOff = OnOff.OFF,
    val swClose: OnOff = OnOff.OFF,
    val gateStage: GateStage = GateStage.STOP
)
