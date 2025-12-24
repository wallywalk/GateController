package com.cm.gatecontroller.core.serial.parser

sealed class AtCommand(val key: String) {
    object CurrVersion : AtCommand("curr_version")
    object StGate : AtCommand("AT+STGATE")
    object StLamp : AtCommand("AT+STLAMP")
    object StLed : AtCommand("AT+STLED")
    object StRelay1 : AtCommand("AT+STRELAY1")
    object StRelay2 : AtCommand("AT+STRELAY2")
    object StPhoto1 : AtCommand("AT+STPHOTO1")
    object StPhoto2 : AtCommand("AT+STPHOTO2")
    object StOpen1 : AtCommand("AT+STOPEN1")
    object StClose1 : AtCommand("AT+STCLOSE1")
    object StOpen2 : AtCommand("AT+STOPEN2")
    object StClose2 : AtCommand("AT+STCLOSE2")
    object StOpen3 : AtCommand("AT+STOPEN3")
    object StClose3 : AtCommand("AT+STCLOSE3")
    object StClose : AtCommand("AT+STCLOSE")
    object StLoopA : AtCommand("AT+STLOOPA")
    object StLoopB : AtCommand("AT+STLOOPB")
    object StMPwr : AtCommand("AT+STMPWR")
    object CurrTestCount : AtCommand("curr_testcount")
    object StDelayTime : AtCommand("AT+DELAYTIME")
    object TestStart : AtCommand("AT+TESTSTART")
    object CurrLevelOpen : AtCommand("curr_levelOpen")
    object CurrLevelClose : AtCommand("curr_levelClose")
    object CurrLamp : AtCommand("curr_lamp")
    object CurrBuzzer : AtCommand("curr_buzzer")
    object CurrLampPosOn : AtCommand("curr_lampPosOn")
    object CurrLampPosOff : AtCommand("curr_lampPosOff")
    object CurrLedOpen : AtCommand("curr_ledOpen")
    object CurrLedOpenPos : AtCommand("curr_ledOpenPos")
    object CurrLedClose : AtCommand("curr_ledClose")
    object CurrLedClosePos : AtCommand("curr_ledClosePos")
    object CurrLoopA : AtCommand("curr_loopa")
    object CurrLoopB : AtCommand("curr_loopb")
    object CurrDelayTime : AtCommand("curr_delayTime")
    object CurrRelay1 : AtCommand("curr_relay1")
    object CurrRelay2 : AtCommand("curr_relay2")
    object Factory : AtCommand("AT+FACTORY")
    object CtrlLamp : AtCommand("AT+CTRLLAMP")
    object CtrlRelay1 : AtCommand("AT+CTRLRELAY1")
    object CtrlRelay2 : AtCommand("AT+CTRLRELAY2")
    object CtrlLed : AtCommand("AT+CTRLLED")
    object StPos : AtCommand("AT+STPOS")
    object InPhoto1 : AtCommand("AT+INPHOTO1")
    object InPhoto2 : AtCommand("AT+INPHOTO2")
    object InLoopA : AtCommand("AT+INLOOPA")
    object InLoopB : AtCommand("AT+INLOOPB")
    object InOpen1 : AtCommand("AT+INOPEN1")
    object InOpen2 : AtCommand("AT+INOPEN2")
    object InOpen3 : AtCommand("AT+INOPEN3")
    object InClose1 : AtCommand("AT+INCLOSE1")
    object InClose2 : AtCommand("AT+INCLOSE2")
    object InClose3 : AtCommand("AT+INCLOSE3")
    object SwOpen : AtCommand("AT+SWOPEN")
    object SwClose : AtCommand("AT+SWCLOSE")
    object StAge : AtCommand("AT+STAGE")

    companion object {
        fun fromKey(key: String): AtCommand? {
            return when (key) {
                CurrVersion.key -> CurrVersion
                StGate.key -> StGate
                StLamp.key -> StLamp
                StLed.key -> StLed
                StRelay1.key -> StRelay1
                StRelay2.key -> StRelay2
                StPhoto1.key -> StPhoto1
                StPhoto2.key -> StPhoto2
                StOpen1.key -> StOpen1
                StClose1.key -> StClose1
                StOpen2.key -> StOpen2
                StClose2.key -> StClose2
                StOpen3.key -> StOpen3
                StClose3.key -> StClose3
                StClose.key -> StClose
                StLoopA.key -> StLoopA
                StLoopB.key -> StLoopB
                StMPwr.key -> StMPwr
                CurrTestCount.key -> CurrTestCount
                StDelayTime.key -> StDelayTime
                TestStart.key -> TestStart
                CurrLevelOpen.key -> CurrLevelOpen
                CurrLevelClose.key -> CurrLevelClose
                CurrLamp.key -> CurrLamp
                CurrBuzzer.key -> CurrBuzzer
                CurrLampPosOn.key -> CurrLampPosOn
                CurrLampPosOff.key -> CurrLampPosOff
                CurrLedOpen.key -> CurrLedOpen
                CurrLedOpenPos.key -> CurrLedOpenPos
                CurrLedClose.key -> CurrLedClose
                CurrLedClosePos.key -> CurrLedClosePos
                CurrLoopA.key -> CurrLoopA
                CurrLoopB.key -> CurrLoopB
                CurrDelayTime.key -> CurrDelayTime
                CurrRelay1.key -> CurrRelay1
                CurrRelay2.key -> CurrRelay2
                Factory.key -> Factory
                CtrlLamp.key -> CtrlLamp
                CtrlRelay1.key -> CtrlRelay1
                CtrlRelay2.key -> CtrlRelay2
                CtrlLed.key -> CtrlLed
                StPos.key -> StPos
                InPhoto1.key -> InPhoto1
                InPhoto2.key -> InPhoto2
                InLoopA.key -> InLoopA
                InLoopB.key -> InLoopB
                InOpen1.key -> InOpen1
                InOpen2.key -> InOpen2
                InOpen3.key -> InOpen3
                InClose1.key -> InClose1
                InClose2.key -> InClose2
                InClose3.key -> InClose3
                SwOpen.key -> SwOpen
                SwClose.key -> SwClose
                StAge.key -> StAge
                else -> null
            }
        }
    }
}
