package com.cm.gatecontroller.core.serial.model

enum class SwitchState { ON, OFF }
enum class LedColor { OFF, BLUE, GREEN, RED, WHITE }

// Monitoring States
enum class AccessMode { OPEN, CLOSE }

// Configuration States
enum class UsageState { USE, UNUSE }
enum class FactoryResponse { OK, ERROR }

// Board Test States
enum class PositionState { LEFT, RIGHT }
enum class GateState { OPENING, OPENED, CLOSING, CLOSED, STOP }

data class GateControllerState(
    // Common
    val version: String = "N/A",

    // Monitoring State
    val accessMode: AccessMode? = null,
    val lampState: SwitchState? = null,
    val ledColor: LedColor? = null,
    val stRelay1: SwitchState? = null,
    val stRelay2: SwitchState? = null,
    val stPhoto1: SwitchState? = null,
    val stPhoto2: SwitchState? = null,
    val stOpen1: SwitchState? = null,
    val stClose1: SwitchState? = null,
    val stOpen2: SwitchState? = null,
    val stClose2: SwitchState? = null,
    val stOpen3: SwitchState? = null,
    val stClose3: SwitchState? = null,
    val stLoopA: SwitchState? = null,
    val stLoopB: SwitchState? = null,
    val mainPower: String = "0.0V",
    val testCount: String = "0",
    val stDelayTime: String = "0sec",
    val isTestRunning: Boolean = false,

    // Configuration State
    val levelOpen: Int = 1,
    val levelClose: Int = 1,
    val lampUsage: UsageState = UsageState.UNUSE,
    val buzzerUsage: UsageState = UsageState.UNUSE,
    val lampPositionOn: GateState = GateState.OPENING,
    val lampPositionOff: GateState = GateState.CLOSING,
    val ledOpenColor: LedColor = LedColor.OFF,
    val ledOpenPosition: GateState = GateState.OPENING,
    val ledCloseColor: LedColor = LedColor.OFF,
    val ledClosePosition: GateState = GateState.CLOSING,
    val setLoopA: UsageState = UsageState.UNUSE,
    val setLoopB: UsageState = UsageState.UNUSE,
    val configDelayTime: Int = 30,
    val setRelay1: Int = 4,
    val setRelay2: Int = 10,
    val factory: FactoryResponse? = FactoryResponse.ERROR,

    // Board Test State
    val controlLamp: SwitchState = SwitchState.OFF,
    val controlRelay1: SwitchState = SwitchState.OFF,
    val controlRelay2: SwitchState = SwitchState.OFF,
    val controlLed: LedColor = LedColor.OFF,
    val controlPosition: PositionState = PositionState.LEFT,
    val inPhoto1: SwitchState = SwitchState.OFF,
    val inPhoto2: SwitchState = SwitchState.OFF,
    val inLoopA: SwitchState? = SwitchState.OFF,
    val inLoopB: SwitchState? = SwitchState.OFF,
    val inOpen1: SwitchState = SwitchState.OFF,
    val inOpen2: SwitchState = SwitchState.OFF,
    val inOpen3: SwitchState = SwitchState.OFF,
    val inClose1: SwitchState = SwitchState.OFF,
    val inClose2: SwitchState = SwitchState.OFF,
    val inClose3: SwitchState = SwitchState.OFF,
    val swOpen: SwitchState? = SwitchState.OFF,
    val swClose: SwitchState? = SwitchState.OFF,
    val gateState: GateState? = GateState.CLOSING
)
