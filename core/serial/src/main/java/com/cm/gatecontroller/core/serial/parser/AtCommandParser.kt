package com.cm.gatecontroller.core.serial.parser

import com.cm.gatecontroller.core.serial.model.FactoryResponse
import com.cm.gatecontroller.core.serial.model.PositionState
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.AccessMode
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.SwitchState
import com.cm.gatecontroller.core.serial.model.UsageState
import com.cm.gatecontroller.core.serial.util.safeValueOf
import javax.inject.Inject

class AtCommandParser @Inject constructor() {

    fun parse(response: String, currentState: GateControllerState): GateControllerState {
        if (!response.contains("=")) return currentState

        val parts = response.split("=", limit = 1)
        val key = parts[0]
        val value = parts[1]

        return when (key) {
            // Common
            "curr_version" -> currentState.copy(version = value)

            // Monitoring
            "AT+STGATE" -> currentState.copy(
                accessMode = safeValueOf<AccessMode>(value) ?: currentState.accessMode
            )

            "AT+STLAMP" -> currentState.copy(
                lampState = safeValueOf<SwitchState>(value) ?: currentState.lampState
            )

            "AT+STLED" -> currentState.copy(
                ledColor = safeValueOf<LedColor>(value) ?: currentState.ledColor
            )

            "AT+STRELAY1" -> currentState.copy(
                stRelay1 = safeValueOf<SwitchState>(value) ?: currentState.stRelay1
            )

            "AT+STRELAY2" -> currentState.copy(
                stRelay2 = safeValueOf<SwitchState>(value) ?: currentState.stRelay2
            )

            "AT+STPHOTO1" -> currentState.copy(
                stPhoto1 = safeValueOf<SwitchState>(value) ?: currentState.stPhoto1
            )

            "AT+STPHOTO2" -> currentState.copy(
                stPhoto2 = safeValueOf<SwitchState>(value) ?: currentState.stPhoto2
            )

            "AT+STOPEN1" -> currentState.copy(
                stOpen1 = safeValueOf<SwitchState>(value) ?: currentState.stOpen1
            )

            "AT+STCLOSE1" -> currentState.copy(
                stClose1 = safeValueOf<SwitchState>(value) ?: currentState.stClose1
            )

            "AT+STOPEN2" -> currentState.copy(
                stOpen2 = safeValueOf<SwitchState>(value) ?: currentState.stOpen2
            )

            "AT+STCLOSE2" -> currentState.copy(
                stClose2 = safeValueOf<SwitchState>(value) ?: currentState.stClose2
            )

            "AT+STOPEN3" -> currentState.copy(
                stOpen3 = safeValueOf<SwitchState>(value) ?: currentState.stOpen3
            )

            "AT+STCLOSE3", "AT+STCLOSE" -> currentState.copy(
                stClose3 = safeValueOf<SwitchState>(value) ?: currentState.stClose3
            )

            "AT+STLOOPA" -> currentState.copy(
                stLoopA = safeValueOf<SwitchState>(value) ?: currentState.stLoopA
            )

            "AT+STLOOPB" -> currentState.copy(
                stLoopB = safeValueOf<SwitchState>(value) ?: currentState.stLoopB
            )

            "AT+STMPWR" -> currentState.copy(mainPower = "${value}V")

            "curr_testcount" -> currentState.copy(testCount = value)

            "AT+STDELATTIME" -> currentState.copy(stDelayTime = "${value}sec")

            "AT+TESTSTART" -> currentState.copy(
                isTestRunning = value.equals(
                    "START",
                    ignoreCase = true
                )
            )

            // Configuration
            "curr_levelOpen" -> currentState.copy(
                levelOpen = value.toIntOrNull() ?: currentState.levelOpen
            )

            "curr_levelClose" -> currentState.copy(
                levelClose = value.toIntOrNull() ?: currentState.levelClose
            )

            "curr_lamp" -> currentState.copy(
                lampUsage = safeValueOf<UsageState>(value) ?: currentState.lampUsage
            )

            "curr_buzzer" -> currentState.copy(
                buzzerUsage = safeValueOf<UsageState>(value) ?: currentState.buzzerUsage
            )

            "curr_lampPosOn" -> currentState.copy(
                lampPositionOn = safeValueOf<GateState>(value)
                    ?: currentState.lampPositionOn
            )

            "curr_lampPosOff" -> currentState.copy(
                lampPositionOff = safeValueOf<GateState>(value)
                    ?: currentState.lampPositionOff
            )

            "curr_ledOpen" -> currentState.copy(
                ledOpenColor = safeValueOf<LedColor>(value) ?: currentState.ledOpenColor
            )

            "curr_ledOpenPos" -> currentState.copy(
                ledOpenPosition = safeValueOf<GateState>(value)
                    ?: currentState.ledOpenPosition
            )

            "curr_ledClose" -> currentState.copy(
                ledCloseColor = safeValueOf<LedColor>(value) ?: currentState.ledCloseColor
            )

            "curr_ledClosePos" -> currentState.copy(
                ledClosePosition = safeValueOf<GateState>(
                    value
                ) ?: currentState.ledClosePosition
            )

            "curr_loopa" -> currentState.copy(
                setLoopA = safeValueOf<UsageState>(value) ?: currentState.setLoopA
            )

            "curr_loopb" -> currentState.copy(
                setLoopB = safeValueOf<UsageState>(value) ?: currentState.setLoopB
            )

            "curr_delayTime" -> currentState.copy(
                configDelayTime = value.toIntOrNull() ?: currentState.configDelayTime
            )

            "curr_relay1" -> currentState.copy(
                setRelay1 = value.toIntOrNull() ?: currentState.setRelay1
            )

            "curr_relay2" -> currentState.copy(
                setRelay2 = value.toIntOrNull() ?: currentState.setRelay2
            )

            "AT+FACTORY" -> {
                currentState.copy(
                    factory = safeValueOf<FactoryResponse>(value)
                )
            }

            // Board Test - Output Test Responses (AT+CTRL...)
            "AT+CTRLLAMP" -> currentState.copy(
                controlLamp = safeValueOf<SwitchState>(value) ?: currentState.controlLamp
            )

            "AT+CTRLRELAY1" -> currentState.copy(
                controlRelay1 = safeValueOf<SwitchState>(value) ?: currentState.controlRelay1
            )

            "AT+CTRLRELAY2" -> currentState.copy(
                controlRelay2 = safeValueOf<SwitchState>(value) ?: currentState.controlRelay2
            )

            "AT+CTRLLED" -> currentState.copy(
                controlLed = safeValueOf<LedColor>(value) ?: currentState.controlLed
            )

            "AT+STPOS" -> currentState.copy(
                controlPosition = safeValueOf<PositionState>(value)
                    ?: currentState.controlPosition
            )

            // Board Test - Input Test Responses (AT+IN...)
            "AT+INPHOTO1" -> currentState.copy(stPhoto1 = safeValueOf<SwitchState>(value))

            "AT+INPHOTO2" -> currentState.copy(stPhoto2 = safeValueOf<SwitchState>(value))

            "AT+INLOOPB" -> currentState.copy(inLoopB = safeValueOf<SwitchState>(value))

            "AT+INLOOPA" -> currentState.copy(inLoopA = safeValueOf<SwitchState>(value))

            "AT+INOPEN1" -> currentState.copy(stOpen1 = safeValueOf<SwitchState>(value))

            "AT+INOPEN2" -> currentState.copy(stOpen2 = safeValueOf<SwitchState>(value))

            "AT+INOPEN3" -> currentState.copy(stOpen3 = safeValueOf<SwitchState>(value))

            "AT+INCLOSE1" -> currentState.copy(stClose1 = safeValueOf<SwitchState>(value))

            "AT+INCLOSE2" -> currentState.copy(stClose2 = safeValueOf<SwitchState>(value))

            "AT+INCLOSE3" -> currentState.copy(stClose3 = safeValueOf<SwitchState>(value))

            "AT+SWOPEN" -> currentState.copy(swOpen = safeValueOf<SwitchState>(value))

            "AT+SWCLOSE" -> currentState.copy(swClose = safeValueOf<SwitchState>(value))

            "AT+STAGE" -> currentState.copy(gateState = safeValueOf<GateState>(value))

            else -> currentState
        }
    }
}