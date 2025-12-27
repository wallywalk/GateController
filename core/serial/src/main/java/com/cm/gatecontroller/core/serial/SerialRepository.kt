package com.cm.gatecontroller.core.serial

import com.cm.gatecontroller.core.serial.model.GateControllerState
import kotlinx.coroutines.flow.StateFlow

interface SerialRepository {

    val deviceStatus: StateFlow<GateControllerState>

    suspend fun requestVersion()
    suspend fun refreshMonitoring()
    suspend fun startTest()
    suspend fun stopTest()
    suspend fun refreshConfiguration()
    suspend fun setOpenLevel(level: Int)
    suspend fun setCloseLevel(level: Int)
    suspend fun setLampUsage(use: Boolean)
    suspend fun setBuzzerUsage(use: Boolean)
    suspend fun setLampOnPosition(on: Boolean)
    suspend fun setLampOffPosition(on: Boolean)
    suspend fun setLedOpenColor(color: String)
    suspend fun setLedOpenPosition(on: Boolean)
    suspend fun setLedCloseColor(color: String)
    suspend fun setLedClosePosition(on: Boolean)
    suspend fun setLoopAUsage(use: Boolean)
    suspend fun setLoopBUsage(use: Boolean)
    suspend fun setDelayTime(time: Int)
    suspend fun setRelay1Mode(mode: Int)
    suspend fun setRelay2Mode(mode: Int)
    suspend fun factoryReset()
    suspend fun setControlLamp(on: Boolean)
    suspend fun setControlRelay1(on: Boolean)
    suspend fun setControlRelay2(on: Boolean)
    suspend fun setControlLed(color: String)
    suspend fun requestPosition()
    suspend fun openGateTest()
    suspend fun closeGateTest()
}