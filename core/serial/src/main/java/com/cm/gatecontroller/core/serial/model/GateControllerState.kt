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
    val levelOpen: Int = 0,
    val levelClose: Int = 0,
    val lampUsage: UsageState? = null,
    val buzzerUsage: UsageState? = null,
    val lampPositionOn: GateState? = null,
    val lampPositionOff: GateState? = null,
    val ledOpenColor: LedColor? = null,
    val ledOpenPosition: GateState? = null,
    val ledCloseColor: LedColor? = null,
    val ledClosePosition: GateState? = null,
    val setLoopA: UsageState? = null,
    val setLoopB: UsageState? = null,
    val configDelayTime: Int = 0,
    val setRelay1: Int = 0,
    val setRelay2: Int = 0,
    val factory: FactoryResponse? = null,

    // Board Test State
    val controlLamp: SwitchState? = null,
    val controlRelay1: SwitchState? = null,
    val controlRelay2: SwitchState? = null,
    val controlLed: LedColor? = null,
    val controlPosition: PositionState? = null,
    val inPhoto1: SwitchState? = null,
    val inPhoto2: SwitchState? = null,
    val inLoopA: SwitchState? = null,
    val inLoopB: SwitchState? = null,
    val inOpen1: SwitchState? = null,
    val inOpen2: SwitchState? = null,
    val inOpen3: SwitchState? = null,
    val inClose1: SwitchState? = null,
    val inClose2: SwitchState? = null,
    val inClose3: SwitchState? = null,
    val swOpen: SwitchState? = null,
    val swClose: SwitchState? = null,
    val gateState: GateState? = null
)
