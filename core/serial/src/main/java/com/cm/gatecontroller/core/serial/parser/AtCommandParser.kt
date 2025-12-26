package com.cm.gatecontroller.core.serial.parser

import com.cm.gatecontroller.core.serial.ResponseKey
import com.cm.gatecontroller.core.serial.model.AccessMode
import com.cm.gatecontroller.core.serial.model.FactoryResponse
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
            ResponseKey.CurrVersion -> currentState.copy(version = value)

            ResponseKey.Message -> currentState.copy(
                message = value
            )

            ResponseKey.StGate -> currentState.copy(
                accessMode = currentState.accessMode.orParsed<AccessMode>(value)
            )

            ResponseKey.StLamp -> currentState.copy(
                lampState = currentState.lampState.orParsed<SwitchState>(value)
            )

            ResponseKey.StLed -> currentState.copy(
                ledColor = currentState.ledColor.orParsed<LedColor>(value)
            )

            ResponseKey.StRelay1 -> currentState.copy(
                stRelay1 = currentState.stRelay1.orParsed<SwitchState>(value)
            )

            ResponseKey.StRelay2 -> currentState.copy(
                stRelay2 = currentState.stRelay2.orParsed<SwitchState>(value)
            )

            ResponseKey.StPhoto1 -> currentState.copy(
                stPhoto1 = currentState.stPhoto1.orParsed<SwitchState>(value)
            )

            ResponseKey.StPhoto2 -> currentState.copy(
                stPhoto2 = currentState.stPhoto2.orParsed<SwitchState>(value)
            )

            ResponseKey.StOpen1 -> currentState.copy(
                stOpen1 = currentState.stOpen1.orParsed<SwitchState>(value)
            )

            ResponseKey.StClose1 -> currentState.copy(
                stClose1 = currentState.stClose1.orParsed<SwitchState>(value)
            )

            ResponseKey.StOpen2 -> currentState.copy(
                stOpen2 = currentState.stOpen2.orParsed<SwitchState>(value)
            )

            ResponseKey.StClose2 -> currentState.copy(
                stClose2 = currentState.stClose2.orParsed<SwitchState>(value)
            )

            ResponseKey.StOpen3 -> currentState.copy(
                stOpen3 = currentState.stOpen3.orParsed<SwitchState>(value)
            )

            ResponseKey.StClose3 -> currentState.copy(
                stClose3 = currentState.stClose3.orParsed<SwitchState>(value)
            )

            ResponseKey.StClose -> currentState.copy(
                stClose3 = currentState.stClose3.orParsed<SwitchState>(value)
            )

            ResponseKey.StLoopA -> currentState.copy(
                stLoopA = currentState.stLoopA.orParsed<SwitchState>(value)
            )

            ResponseKey.StLoopB -> currentState.copy(
                stLoopB = currentState.stLoopB.orParsed<SwitchState>(value)
            )

            ResponseKey.StMPwr -> currentState.copy(mainPower = "${value}V")

            ResponseKey.CurrTestCount -> currentState.copy(testCount = value)

            ResponseKey.StDelayTime -> currentState.copy(stDelayTime = "${value}sec")

            ResponseKey.TestStart -> currentState.copy(
                isTestRunning = value.equals("START", ignoreCase = true)
            )

            ResponseKey.CurrLevelOpen -> currentState.copy(
                levelOpen = value.toIntOrNull() ?: currentState.levelOpen
            )

            ResponseKey.CurrLevelClose -> currentState.copy(
                levelClose = value.toIntOrNull() ?: currentState.levelClose
            )

            ResponseKey.CurrLamp -> currentState.copy(
                lampUsage = currentState.lampUsage.orParsed<UsageState>(value)
            )

            ResponseKey.CurrBuzzer -> currentState.copy(
                buzzerUsage = currentState.buzzerUsage.orParsed<UsageState>(value)
            )

            ResponseKey.CurrLampPosOn -> currentState.copy(
                lampPositionOn = currentState.lampPositionOn.orParsed<GateState>(value)
            )

            ResponseKey.CurrLampPosOff -> currentState.copy(
                lampPositionOff = currentState.lampPositionOff.orParsed<GateState>(value)
            )

            ResponseKey.CurrLedOpen -> currentState.copy(
                ledOpenColor = currentState.ledOpenColor.orParsed<LedColor>(value)
            )

            ResponseKey.CurrLedOpenPos -> currentState.copy(
                ledOpenPosition = currentState.ledOpenPosition.orParsed<GateState>(value)
            )

            ResponseKey.CurrLedClose -> currentState.copy(
                ledCloseColor = currentState.ledCloseColor.orParsed<LedColor>(value)
            )

            ResponseKey.CurrLedClosePos -> currentState.copy(
                ledClosePosition = currentState.ledClosePosition.orParsed<GateState>(value)
            )

            ResponseKey.CurrLoopA -> currentState.copy(
                setLoopA = currentState.setLoopA.orParsed<UsageState>(value)
            )

            ResponseKey.CurrLoopB -> currentState.copy(
                setLoopB = currentState.setLoopB.orParsed<UsageState>(value)
            )

            ResponseKey.CurrDelayTime -> currentState.copy(
                configDelayTime = value.toIntOrNull() ?: currentState.configDelayTime
            )

            ResponseKey.CurrRelay1 -> currentState.copy(
                setRelay1 = value.toIntOrNull() ?: currentState.setRelay1
            )

            ResponseKey.CurrRelay2 -> currentState.copy(
                setRelay2 = value.toIntOrNull() ?: currentState.setRelay2
            )

            ResponseKey.Factory -> {
                currentState.copy(
                    factory = currentState.factory.orParsed<FactoryResponse>(value)
                )
            }

            ResponseKey.CtrlLamp -> currentState.copy(
                controlLamp = currentState.controlLamp.orParsed<SwitchState>(value)
            )

            ResponseKey.CtrlRelay1 -> currentState.copy(
                controlRelay1 = currentState.controlRelay1.orParsed<SwitchState>(value)
            )

            ResponseKey.CtrlRelay2 -> currentState.copy(
                controlRelay2 = currentState.controlRelay2.orParsed<SwitchState>(value)
            )

            ResponseKey.CtrlLed -> currentState.copy(
                controlLed = currentState.controlLed.orParsed<LedColor>(value)
            )

            ResponseKey.StPos -> currentState.copy(
                controlPosition = currentState.controlPosition.orParsed<PositionState>(value)
            )

            ResponseKey.InPhoto1 -> currentState.copy(
                inPhoto1 = currentState.inPhoto1.orParsed<SwitchState>(value)
            )

            ResponseKey.InPhoto2 -> currentState.copy(
                inPhoto2 = currentState.inPhoto2.orParsed<SwitchState>(value)
            )

            ResponseKey.InLoopA -> currentState.copy(
                inLoopA = currentState.inLoopA.orParsed<SwitchState>(value)
            )

            ResponseKey.InLoopB -> currentState.copy(
                inLoopB = currentState.inLoopB.orParsed<SwitchState>(value)
            )

            ResponseKey.InOpen1 -> currentState.copy(
                stOpen1 = currentState.stOpen1.orParsed<SwitchState>(value)
            )

            ResponseKey.InOpen2 -> currentState.copy(
                stOpen2 = currentState.stOpen2.orParsed<SwitchState>(value)
            )

            ResponseKey.InOpen3 -> currentState.copy(
                stOpen3 = currentState.stOpen3.orParsed<SwitchState>(value)
            )

            ResponseKey.InClose1 -> currentState.copy(
                stClose1 = currentState.stClose1.orParsed<SwitchState>(value)
            )

            ResponseKey.InClose2 -> currentState.copy(
                stClose2 = currentState.stClose2.orParsed<SwitchState>(value)
            )

            ResponseKey.InClose3 -> currentState.copy(
                stClose3 = currentState.stClose3.orParsed<SwitchState>(value)
            )

            ResponseKey.SwOpen -> currentState.copy(
                swOpen = currentState.swOpen.orParsed<SwitchState>(value)
            )

            ResponseKey.SwClose -> currentState.copy(
                swClose = currentState.swClose.orParsed<SwitchState>(value)
            )

            ResponseKey.Stage -> currentState.copy(
                gateState = currentState.gateState.orParsed<GateState>(value)
            )
        }
    }
}