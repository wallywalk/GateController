package com.cm.gatecontroller.configuration.model

import com.cm.gatecontroller.model.LedStatus

data class DeviceConfig(
    val version: String = "",
    val levelOpen: Int = 1,
    val levelClose: Int = 1,
    val lamp: UsageStatus = UsageStatus.USE,
    val buzzer: UsageStatus = UsageStatus.USE,
    val lampPosOn: LampStatus = LampStatus.OPENED,
    val lampPosOff: LampStatus = LampStatus.CLOSED,
    val ledOpen: LedStatus = LedStatus.OFF,
    val ledOpenPos: Int = 1,
    val ledClose: LedStatus = LedStatus.OFF,
    val ledClosePos: Int = 1,
    val loopA: UsageStatus = UsageStatus.USE,
    val loopB: UsageStatus = UsageStatus.USE,
    val delayTime: Int = 0,
    val relay1: UsageStatus = UsageStatus.USE,
    val relay2: UsageStatus = UsageStatus.USE
)
