package com.cm.gatecontroller.core.serial.parser

import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.LampPosition
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.OnOff
import com.cm.gatecontroller.core.serial.model.UseState
import com.cm.gatecontroller.core.serial.util.safeValueOf
import javax.inject.Inject

class AtCommandParser @Inject constructor() {

    fun parse(response: String, currentState: GateControllerState): GateControllerState {
        if (!response.contains("=")) return currentState

        val parts = response.split("=", limit = 2)
        val key = parts[0]
        val value = parts[1]

        return when (key) {
            // Common
            "curr_version" -> currentState.copy(version = value)

            // Monitoring
            "AT+STGATE" -> currentState.copy(
                gateState = safeValueOf<GateState>(value) ?: currentState.gateState
            )

            "AT+STLAMP" -> currentState.copy(
                lampState = safeValueOf<OnOff>(value) ?: currentState.lampState
            )

            "AT+STLED" -> currentState.copy(
                ledColor = safeValueOf<LedColor>(value) ?: currentState.ledColor
            )

            "AT+STRELAY1" -> currentState.copy(
                relay1 = safeValueOf<OnOff>(value) ?: currentState.relay1
            )

            "AT+STRELAY2" -> currentState.copy(
                relay2 = safeValueOf<OnOff>(value) ?: currentState.relay2
            )

            "AT+STPHOTO1" -> currentState.copy(
                photo1 = safeValueOf<OnOff>(value) ?: currentState.photo1
            )

            "AT+STPHOTO2" -> currentState.copy(
                photo2 = safeValueOf<OnOff>(value) ?: currentState.photo2
            )

            "AT+STOPEN1" -> currentState.copy(
                open1 = safeValueOf<OnOff>(value) ?: currentState.open1
            )

            "AT+STCLOSE1", "AT+STCLOSEE1" -> currentState.copy(
                close1 = safeValueOf<OnOff>(value) ?: currentState.close1
            )

            "AT+STOPEN2" -> currentState.copy(
                open2 = safeValueOf<OnOff>(value) ?: currentState.open2
            )

            "AT+STCLOSE2" -> currentState.copy(
                close2 = safeValueOf<OnOff>(value) ?: currentState.close2
            )

            "AT+STOPEN3" -> currentState.copy(
                open3 = safeValueOf<OnOff>(value) ?: currentState.open3
            )

            "AT+STCLOSE3", "AT+STCLOSE" -> currentState.copy(
                close3 = safeValueOf<OnOff>(value) ?: currentState.close3
            )

            "AT+STLOOPA" -> currentState.copy(
                loopA_mon = safeValueOf<OnOff>(value) ?: currentState.loopA_mon
            )

            "AT+STLOOPB" -> currentState.copy(
                loopB_mon = safeValueOf<OnOff>(value) ?: currentState.loopB_mon
            )

            "AT+STMPWR" -> currentState.copy(mainPower = "${value}V")
            "curr_testcount" -> currentState.copy(testCount = value)
            "AT+STDELATTIME" -> currentState.copy(delayTime_mon = "${value}sec")
            "AT+TESTSTART" -> currentState.copy(
                isTestRunning = value.equals(
                    "START",
                    ignoreCase = true
                )
            )

            // Configuration
            "curr_levelOpen" -> currentState.copy(
                openLevel = value.toIntOrNull() ?: currentState.openLevel
            )

            "curr_levelClose" -> currentState.copy(
                closeLevel = value.toIntOrNull() ?: currentState.closeLevel
            )

            "curr_lamp" -> currentState.copy(
                lampUsage = safeValueOf<UseState>(value) ?: currentState.lampUsage
            )

            "curr_buzzer" -> currentState.copy(
                buzzerUsage = safeValueOf<UseState>(value) ?: currentState.buzzerUsage
            )

            "curr_lampPosOn" -> currentState.copy(
                lampOnPosition = safeValueOf<LampPosition>(value) ?: currentState.lampOnPosition
            )

            "curr_lampPosOff" -> currentState.copy(
                lampOffPosition = safeValueOf<LampPosition>(value) ?: currentState.lampOffPosition
            )

            "curr_ledOpen" -> currentState.copy(
                ledOpenColor = safeValueOf<LedColor>(value) ?: currentState.ledOpenColor
            )

            "curr_ledOpenPos" -> currentState.copy(
                ledOpenPosition = safeValueOf<LampPosition>(value) ?: currentState.ledOpenPosition
            )

            "curr_ledClose" -> currentState.copy(
                ledCloseColor = safeValueOf<LedColor>(value) ?: currentState.ledCloseColor
            )

            "curr_ledClosePos" -> currentState.copy(
                ledClosePosition = safeValueOf<LampPosition>(
                    value
                ) ?: currentState.ledClosePosition
            )

            "curr_loopa" -> currentState.copy(
                loopA_conf = safeValueOf<UseState>(value) ?: currentState.loopA_conf
            )

            "curr_loopb" -> currentState.copy(
                loopB_conf = safeValueOf<UseState>(value) ?: currentState.loopB_conf
            )

            "curr_delayTime" -> currentState.copy(
                delayTime_conf = value.toIntOrNull() ?: currentState.delayTime_conf
            )

            "curr_relay1" -> currentState.copy(
                relay1Mode = value.toIntOrNull() ?: currentState.relay1Mode
            )

            "curr_relay2" -> currentState.copy(
                relay2Mode = value.toIntOrNull() ?: currentState.relay2Mode
            )

            "AT+FACTORY" -> {
                // This is a command confirmation, not a state update.
                // It should be handled as a side effect.
                currentState
            }

            else -> currentState
        }
    }
}