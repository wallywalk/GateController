package com.cm.gatecontroller.core.serial.parser

import com.cm.gatecontroller.core.serial.ResponseKey
import com.cm.gatecontroller.core.serial.model.AccessMode
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.PositionState
import com.cm.gatecontroller.core.serial.model.SwitchState
import com.cm.gatecontroller.core.serial.model.UsageState
import com.cm.gatecontroller.core.serial.util.orParsed
import javax.inject.Inject

class AtCommandParser @Inject constructor() {

    fun parse(response: String, currentState: GateControllerState): GateControllerState {
        if (!response.contains("=")) return currentState

        val parts = response.split("=", limit = 2)
        val keyString = parts[0]
        val value = parts[1]

        val responseKey = ResponseKey.fromKey(keyString) ?: return currentState

        return when (responseKey) {
            ResponseKey.CURR_VERSION -> currentState.copy(version = value)

            ResponseKey.MESSAGE -> currentState.copy(message = value)

            ResponseKey.ST_GATE -> currentState.copy(
                accessMode = currentState.accessMode.orParsed<AccessMode>(
                    value
                )
            )

            ResponseKey.ST_LAMP ->
                currentState.copy(
                    lampState = currentState.lampState.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_LED ->
                currentState.copy(
                    ledColor = currentState.ledColor.orParsed<LedColor>(value)
                )

            ResponseKey.ST_RELAY1 ->
                currentState.copy(
                    stRelay1 = currentState.stRelay1.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_RELAY2 ->
                currentState.copy(
                    stRelay2 = currentState.stRelay2.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_PHOTO1 ->
                currentState.copy(
                    stPhoto1 = currentState.stPhoto1.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_PHOTO2 ->
                currentState.copy(
                    stPhoto2 = currentState.stPhoto2.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_OPEN1 ->
                currentState.copy(
                    stOpen1 = currentState.stOpen1.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_OPEN2 ->
                currentState.copy(
                    stOpen2 = currentState.stOpen2.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_OPEN3 ->
                currentState.copy(
                    stOpen3 = currentState.stOpen3.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_CLOSE1 ->
                currentState.copy(
                    stClose1 = currentState.stClose1.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_CLOSE2 ->
                currentState.copy(
                    stClose2 = currentState.stClose2.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_CLOSE3 ->
                currentState.copy(
                    stClose3 = currentState.stClose3.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_LOOP_A ->
                currentState.copy(
                    stLoopA = currentState.stLoopA.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_LOOP_B ->
                currentState.copy(
                    stLoopB = currentState.stLoopB.orParsed<SwitchState>(value)
                )

            ResponseKey.ST_M_PWR -> currentState.copy(mainPower = "${value}V")

            ResponseKey.CURR_TEST_COUNT -> currentState.copy(testCount = value)

            ResponseKey.ST_DELAY_TIME -> currentState.copy(stDelayTime = "${value}sec")

            ResponseKey.TEST_START ->
                currentState.copy(
                    isTestRunning = value.equals("START", ignoreCase = true)
                )

            ResponseKey.CURR_LEVEL_OPEN ->
                currentState.copy(
                    levelOpen = value.toIntOrNull() ?: currentState.levelOpen
                )

            ResponseKey.CURR_LEVEL_CLOSE ->
                currentState.copy(
                    levelClose = value.toIntOrNull() ?: currentState.levelClose
                )

            ResponseKey.CURR_LAMP ->
                currentState.copy(
                    lampUsage = currentState.lampUsage.orParsed<UsageState>(value)
                )

            ResponseKey.CURR_BUZZER ->
                currentState.copy(
                    buzzerUsage = currentState.buzzerUsage.orParsed<UsageState>(value)
                )

            ResponseKey.CURR_LAMP_POS_ON ->
                currentState.copy(
                    lampPositionOn = currentState.lampPositionOn.orParsed<GateState>(value)
                )

            ResponseKey.CURR_LAMP_POS_OFF ->
                currentState.copy(
                    lampPositionOff = currentState.lampPositionOff.orParsed<GateState>(value)
                )

            ResponseKey.CURR_LED_OPEN ->
                currentState.copy(
                    ledOpenColor = currentState.ledOpenColor.orParsed<LedColor>(value)
                )

            ResponseKey.CURR_LED_OPEN_POS ->
                currentState.copy(
                    ledOpenPosition = currentState.ledOpenPosition.orParsed<GateState>(value)
                )

            ResponseKey.CURR_LED_CLOSE ->
                currentState.copy(
                    ledCloseColor = currentState.ledCloseColor.orParsed<LedColor>(value)
                )

            ResponseKey.CURR_LED_CLOSE_POS ->
                currentState.copy(
                    ledClosePosition = currentState.ledClosePosition.orParsed<GateState>(value)
                )

            ResponseKey.CURR_LOOP_A ->
                currentState.copy(
                    setLoopA = currentState.setLoopA.orParsed<UsageState>(value)
                )

            ResponseKey.CURR_LOOP_B ->
                currentState.copy(
                    setLoopB = currentState.setLoopB.orParsed<UsageState>(value)
                )

            ResponseKey.CURR_DELAY_TIME ->
                currentState.copy(
                    configDelayTime = value.toIntOrNull() ?: currentState.configDelayTime
                )

            ResponseKey.CURR_RELAY1 ->
                currentState.copy(
                    setRelay1 = value.toIntOrNull() ?: currentState.setRelay1
                )

            ResponseKey.CURR_RELAY2 ->
                currentState.copy(
                    setRelay2 = value.toIntOrNull() ?: currentState.setRelay2
                )

            ResponseKey.ST_POS ->
                currentState.copy(
                    stPosition = currentState.stPosition.orParsed<PositionState>(value)
                )

            ResponseKey.CTRL_LAMP ->
                currentState.copy(
                    controlLamp = currentState.controlLamp.orParsed<SwitchState>(value)
                )

            ResponseKey.CTRL_RELAY1 ->
                currentState.copy(
                    controlRelay1 = currentState.controlRelay1.orParsed<SwitchState>(value)
                )

            ResponseKey.CTRL_RELAY2 ->
                currentState.copy(
                    controlRelay2 = currentState.controlRelay2.orParsed<SwitchState>(value)
                )

            ResponseKey.CTRL_LED ->
                currentState.copy(
                    controlLed = currentState.controlLed.orParsed<LedColor>(value)
                )

            ResponseKey.IN_POS ->
                currentState.copy(
                    inPosition = currentState.inPosition.orParsed<PositionState>(value)
                )

            ResponseKey.IN_PHOTO1 ->
                currentState.copy(
                    inPhoto1 = currentState.inPhoto1.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_PHOTO2 ->
                currentState.copy(
                    inPhoto2 = currentState.inPhoto2.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_LOOP_A ->
                currentState.copy(
                    inLoopA = currentState.inLoopA.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_LOOP_B ->
                currentState.copy(
                    inLoopB = currentState.inLoopB.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_OPEN1 ->
                currentState.copy(
                    stOpen1 = currentState.stOpen1.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_OPEN2 ->
                currentState.copy(
                    stOpen2 = currentState.stOpen2.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_OPEN3 ->
                currentState.copy(
                    stOpen3 = currentState.stOpen3.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_CLOSE1 ->
                currentState.copy(
                    stClose1 = currentState.stClose1.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_CLOSE2 ->
                currentState.copy(
                    stClose2 = currentState.stClose2.orParsed<SwitchState>(value)
                )

            ResponseKey.IN_CLOSE3 ->
                currentState.copy(
                    stClose3 = currentState.stClose3.orParsed<SwitchState>(value)
                )

            ResponseKey.SW_OPEN ->
                currentState.copy(
                    swOpen = currentState.swOpen.orParsed<SwitchState>(value)
                )

            ResponseKey.SW_CLOSE ->
                currentState.copy(
                    swClose = currentState.swClose.orParsed<SwitchState>(value)
                )

            ResponseKey.STAGE ->
                currentState.copy(
                    gateState = currentState.gateState.orParsed<GateState>(value)
                )
        }
    }
}