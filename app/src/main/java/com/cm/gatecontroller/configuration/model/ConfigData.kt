package com.cm.gatecontroller.configuration.model

import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus

data class ConfigData(
    val version: String,
    val levelOpen: Int,
    val levelClose: Int,
    val lamp: UsageStatus,
    val buzzer: UsageStatus,
    val lampPosOn: GateStatus,
    val lampPosOff: GateStatus,
    val ledOpenColor: LedStatus,
    val ledOpenPos: GateStatus,
    val ledCloseColor: LedStatus,
    val ledClosePos: GateStatus,
    val loopA: UsageStatus,
    val loopB: UsageStatus,
    val delayTime: Int,
    val relay1: Int,
    val relay2: Int
)
