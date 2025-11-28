package com.cm.gatecontroller.core.serial.parser

import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.GateStage
import com.cm.gatecontroller.core.serial.model.LampPosition
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.OnOff
import com.cm.gatecontroller.core.serial.model.PositionState
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
            "AT+STGATE" -> currentState.copy(gateState = safeValueOf<GateState>(value) ?: currentState.gateState)
            "AT+STLAMP" -> currentState.copy(lampState = safeValueOf<OnOff>(value) ?: currentState.lampState)
            "AT+STLED" -> currentState.copy(ledColor = safeValueOf<LedColor>(value) ?: currentState.ledColor)
            "AT+STRELAY1" -> currentState.copy(relay1 = safeValueOf<OnOff>(value) ?: currentState.relay1)
            "AT+STRELAY2" -> currentState.copy(relay2 = safeValueOf<OnOff>(value) ?: currentState.relay2)
            "AT+STPHOTO1" -> currentState.copy(photo1 = safeValueOf<OnOff>(value) ?: currentState.photo1)
            "AT+STPHOTO2" -> currentState.copy(photo2 = safeValueOf<OnOff>(value) ?: currentState.photo2)
            "AT+STOPEN1" -> currentState.copy(open1 = safeValueOf<OnOff>(value) ?: currentState.open1)
            "AT+STCLOSE1", "AT+STCLOSEE1" -> currentState.copy(close1 = safeValueOf<OnOff>(value) ?: currentState.close1)
            "AT+STOPEN2" -> currentState.copy(open2 = safeValueOf<OnOff>(value) ?: currentState.open2)
            "AT+STCLOSE2" -> currentState.copy(close2 = safeValueOf<OnOff>(value) ?: currentState.close2)
            "AT+STOPEN3" -> currentState.copy(open3 = safeValueOf<OnOff>(value) ?: currentState.open3)
            "AT+STCLOSE3", "AT+STCLOSE" -> currentState.copy(close3 = safeValueOf<OnOff>(value) ?: currentState.close3)
            "AT+STLOOPA" -> currentState.copy(loopA_mon = safeValueOf<OnOff>(value) ?: currentState.loopA_mon)
            "AT+STLOOPB" -> currentState.copy(loopB_mon = safeValueOf<OnOff>(value) ?: currentState.loopB_mon)
            "AT+STMPWR" -> currentState.copy(mainPower = "${value}V")
            "curr_testcount" -> currentState.copy(testCount = value)
            "AT+STDELATTIME" -> currentState.copy(delayTime_mon = "${value}sec")
            "AT+TESTSTART" -> currentState.copy(isTestRunning = value.equals("START", ignoreCase = true))

            // Configuration
            "curr_levelOpen" -> currentState.copy(openLevel = value.toIntOrNull() ?: currentState.openLevel)
            "curr_levelClose" -> currentState.copy(closeLevel = value.toIntOrNull() ?: currentState.closeLevel)
            "curr_lamp" -> currentState.copy(lampUsage = safeValueOf<UseState>(value) ?: currentState.lampUsage)
            "curr_buzzer" -> currentState.copy(buzzerUsage = safeValueOf<UseState>(value) ?: currentState.buzzerUsage)
            "curr_lampPosOn" -> currentState.copy(lampOnPosition = safeValueOf<LampPosition>(value) ?: currentState.lampOnPosition)
            "curr_lampPosOff" -> currentState.copy(lampOffPosition = safeValueOf<LampPosition>(value) ?: currentState.lampOffPosition)
            "curr_ledOpen" -> currentState.copy(ledOpenColor = safeValueOf<LedColor>(value) ?: currentState.ledOpenColor)
            "curr_ledOpenPos" -> currentState.copy(ledOpenPosition = safeValueOf<LampPosition>(value) ?: currentState.ledOpenPosition)
            "curr_ledClose" -> currentState.copy(ledCloseColor = safeValueOf<LedColor>(value) ?: currentState.ledCloseColor)
            "curr_ledClosePos" -> currentState.copy(ledClosePosition = safeValueOf<LampPosition>(value) ?: currentState.ledClosePosition)
            "curr_loopa" -> currentState.copy(loopA_conf = safeValueOf<UseState>(value) ?: currentState.loopA_conf)
            "curr_loopb" -> currentState.copy(loopB_conf = safeValueOf<UseState>(value) ?: currentState.loopB_conf)
            "curr_delayTime" -> currentState.copy(delayTime_conf = value.toIntOrNull() ?: currentState.delayTime_conf)
            "curr_relay1" -> currentState.copy(relay1Mode = value.toIntOrNull() ?: currentState.relay1Mode)
            "curr_relay2" -> currentState.copy(relay2Mode = value.toIntOrNull() ?: currentState.relay2Mode)

            // Board Test - Output Test Responses (AT+CTRL...)
            "AT+CTRLLAMP" -> currentState.copy(controlLamp = safeValueOf<OnOff>(value) ?: currentState.controlLamp)
            "AT+CTRLRELAY1" -> currentState.copy(controlRelay1 = safeValueOf<OnOff>(value) ?: currentState.controlRelay1)
            "AT+CTRLRELAY2" -> currentState.copy(controlRelay2 = safeValueOf<OnOff>(value) ?: currentState.controlRelay2)
            "AT+CTRLLED" -> currentState.copy(controlLed = safeValueOf<LedColor>(value) ?: currentState.controlLed)
            "AT+STPOS" -> currentState.copy(controlPosition = safeValueOf<PositionState>(value) ?: currentState.controlPosition)

            // Board Test - Input Test Responses (AT+IN...)
            "AT+INPHOTO1" -> currentState.copy(inPhoto1 = safeValueOf<OnOff>(value) ?: currentState.inPhoto1)
            "AT+INPHOTO2" -> currentState.copy(inPhoto2 = safeValueOf<OnOff>(value) ?: currentState.inPhoto2)
            "AT+INLOOPA" -> currentState.copy(inLoopA = safeValueOf<OnOff>(value) ?: currentState.inLoopA)
            "AT+INLOOPB" -> currentState.copy(inLoopB = safeValueOf<OnOff>(value) ?: currentState.inLoopB)
            "AT+INOPEN1" -> currentState.copy(inOpen1 = safeValueOf<OnOff>(value) ?: currentState.inOpen1)
            "AT+INOPEN2" -> currentState.copy(inOpen2 = safeValueOf<OnOff>(value) ?: currentState.inOpen2)
            "AT+INOPEN3" -> currentState.copy(inOpen3 = safeValueOf<OnOff>(value) ?: currentState.inOpen3)
            "AT+INCLOSE1" -> currentState.copy(inClose1 = safeValueOf<OnOff>(value) ?: currentState.inClose1)
            "AT+INCLOSE2" -> currentState.copy(inClose2 = safeValueOf<OnOff>(value) ?: currentState.inClose2)
            "AT+INCLOSE3" -> currentState.copy(inClose3 = safeValueOf<OnOff>(value) ?: currentState.inClose3)
            "AT+SWOPEN" -> currentState.copy(swOpen = safeValueOf<OnOff>(value) ?: currentState.swOpen)
            "AT+SWCLOSE" -> currentState.copy(swClose = safeValueOf<OnOff>(value) ?: currentState.swClose)

            // Board Test - Operation Test Responses (AT+STAGE...)
            "AT+STAGE" -> currentState.copy(gateStage = safeValueOf<GateStage>(value) ?: currentState.gateStage)

            "AT+FACTORY" -> {
                // This is a command confirmation, not a state update.
                // It should be handled as a side effect.
                currentState
            }

            else -> currentState
        }
    }
}