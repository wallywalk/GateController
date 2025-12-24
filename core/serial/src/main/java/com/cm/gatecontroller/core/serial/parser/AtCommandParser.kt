package com.cm.gatecontroller.core.serial.parser

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

        val atCommand = AtCommand.fromKey(keyString) ?: return currentState

        return when (atCommand) {
            AtCommand.CurrVersion -> currentState.copy(version = value)

            AtCommand.StGate -> currentState.copy(
                accessMode = currentState.accessMode.orParsed<AccessMode>(value)
            )

            AtCommand.StLamp -> currentState.copy(
                lampState = currentState.lampState.orParsed<SwitchState>(value)
            )

            AtCommand.StLed -> currentState.copy(
                ledColor = currentState.ledColor.orParsed<LedColor>(value)
            )

            AtCommand.StRelay1 -> currentState.copy(
                stRelay1 = currentState.stRelay1.orParsed<SwitchState>(value)
            )

            AtCommand.StRelay2 -> currentState.copy(
                stRelay2 = currentState.stRelay2.orParsed<SwitchState>(value)
            )

            AtCommand.StPhoto1 -> currentState.copy(
                stPhoto1 = currentState.stPhoto1.orParsed<SwitchState>(value)
            )

            AtCommand.StPhoto2 -> currentState.copy(
                stPhoto2 = currentState.stPhoto2.orParsed<SwitchState>(value)
            )

            AtCommand.StOpen1 -> currentState.copy(
                stOpen1 = currentState.stOpen1.orParsed<SwitchState>(value)
            )

            AtCommand.StClose1 -> currentState.copy(
                stClose1 = currentState.stClose1.orParsed<SwitchState>(value)
            )

            AtCommand.StOpen2 -> currentState.copy(
                stOpen2 = currentState.stOpen2.orParsed<SwitchState>(value)
            )

            AtCommand.StClose2 -> currentState.copy(
                stClose2 = currentState.stClose2.orParsed<SwitchState>(value)
            )

            AtCommand.StOpen3 -> currentState.copy(
                stOpen3 = currentState.stOpen3.orParsed<SwitchState>(value)
            )

            AtCommand.StClose3 -> currentState.copy(
                stClose3 = currentState.stClose3.orParsed<SwitchState>(value)
            )

            AtCommand.StClose -> currentState.copy(
                stClose3 = currentState.stClose3.orParsed<SwitchState>(value)
            )

            AtCommand.StLoopA -> currentState.copy(
                stLoopA = currentState.stLoopA.orParsed<SwitchState>(value)
            )

            AtCommand.StLoopB -> currentState.copy(
                stLoopB = currentState.stLoopB.orParsed<SwitchState>(value)
            )

            AtCommand.StMPwr -> currentState.copy(mainPower = "${value}V")

            AtCommand.CurrTestCount -> currentState.copy(testCount = value)

            AtCommand.StDelayTime -> currentState.copy(stDelayTime = "${value}sec")

            AtCommand.TestStart -> currentState.copy(
                isTestRunning = value.equals("START", ignoreCase = true)
            )

            AtCommand.CurrLevelOpen -> currentState.copy(
                levelOpen = value.toIntOrNull() ?: currentState.levelOpen
            )

            AtCommand.CurrLevelClose -> currentState.copy(
                levelClose = value.toIntOrNull() ?: currentState.levelClose
            )

            AtCommand.CurrLamp -> currentState.copy(
                lampUsage = currentState.lampUsage.orParsed<UsageState>(value)
            )

            AtCommand.CurrBuzzer -> currentState.copy(
                buzzerUsage = currentState.buzzerUsage.orParsed<UsageState>(value)
            )

            AtCommand.CurrLampPosOn -> currentState.copy(
                lampPositionOn = currentState.lampPositionOn.orParsed<GateState>(value)
            )

            AtCommand.CurrLampPosOff -> currentState.copy(
                lampPositionOff = currentState.lampPositionOff.orParsed<GateState>(value)
            )

            AtCommand.CurrLedOpen -> currentState.copy(
                ledOpenColor = currentState.ledOpenColor.orParsed<LedColor>(value)
            )

            AtCommand.CurrLedOpenPos -> currentState.copy(
                ledOpenPosition = currentState.ledOpenPosition.orParsed<GateState>(value)
            )

            AtCommand.CurrLedClose -> currentState.copy(
                ledCloseColor = currentState.ledCloseColor.orParsed<LedColor>(value)
            )

            AtCommand.CurrLedClosePos -> currentState.copy(
                ledClosePosition = currentState.ledClosePosition.orParsed<GateState>(value)
            )

            AtCommand.CurrLoopA -> currentState.copy(
                setLoopA = currentState.setLoopA.orParsed<UsageState>(value)
            )

            AtCommand.CurrLoopB -> currentState.copy(
                setLoopB = currentState.setLoopB.orParsed<UsageState>(value)
            )

            AtCommand.CurrDelayTime -> currentState.copy(
                configDelayTime = value.toIntOrNull() ?: currentState.configDelayTime
            )

            AtCommand.CurrRelay1 -> currentState.copy(
                setRelay1 = value.toIntOrNull() ?: currentState.setRelay1
            )

            AtCommand.CurrRelay2 -> currentState.copy(
                setRelay2 = value.toIntOrNull() ?: currentState.setRelay2
            )

            AtCommand.Factory -> {
                currentState.copy(
                    factory = currentState.factory.orParsed<FactoryResponse>(value)
                )
            }

            AtCommand.CtrlLamp -> currentState.copy(
                controlLamp = currentState.controlLamp.orParsed<SwitchState>(value)
            )

            AtCommand.CtrlRelay1 -> currentState.copy(
                controlRelay1 = currentState.controlRelay1.orParsed<SwitchState>(value)
            )

            AtCommand.CtrlRelay2 -> currentState.copy(
                controlRelay2 = currentState.controlRelay2.orParsed<SwitchState>(value)
            )

            AtCommand.CtrlLed -> currentState.copy(
                controlLed = currentState.controlLed.orParsed<LedColor>(value)
            )

            AtCommand.StPos -> currentState.copy(
                controlPosition = currentState.controlPosition.orParsed<PositionState>(value)
            )

            AtCommand.InPhoto1 -> currentState.copy(
                inPhoto1 = currentState.inPhoto1.orParsed<SwitchState>(value)
            )

            AtCommand.InPhoto2 -> currentState.copy(
                inPhoto2 = currentState.inPhoto2.orParsed<SwitchState>(value)
            )

            AtCommand.InLoopA -> currentState.copy(
                inLoopA = currentState.inLoopA.orParsed<SwitchState>(value)
            )

            AtCommand.InLoopB -> currentState.copy(
                inLoopB = currentState.inLoopB.orParsed<SwitchState>(value)
            )

            AtCommand.InOpen1 -> currentState.copy(
                stOpen1 = currentState.stOpen1.orParsed<SwitchState>(value)
            )

            AtCommand.InOpen2 -> currentState.copy(
                stOpen2 = currentState.stOpen2.orParsed<SwitchState>(value)
            )

            AtCommand.InOpen3 -> currentState.copy(
                stOpen3 = currentState.stOpen3.orParsed<SwitchState>(value)
            )

            AtCommand.InClose1 -> currentState.copy(
                stClose1 = currentState.stClose1.orParsed<SwitchState>(value)
            )

            AtCommand.InClose2 -> currentState.copy(
                stClose2 = currentState.stClose2.orParsed<SwitchState>(value)
            )

            AtCommand.InClose3 -> currentState.copy(
                stClose3 = currentState.stClose3.orParsed<SwitchState>(value)
            )

            AtCommand.SwOpen -> currentState.copy(
                swOpen = currentState.swOpen.orParsed<SwitchState>(value)
            )

            AtCommand.SwClose -> currentState.copy(
                swClose = currentState.swClose.orParsed<SwitchState>(value)
            )

            AtCommand.StAge -> currentState.copy(
                gateState = currentState.gateState.orParsed<GateState>(value)
            )
        }
    }
}