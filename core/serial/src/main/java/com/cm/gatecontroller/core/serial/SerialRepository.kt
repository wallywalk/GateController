package com.cm.gatecontroller.core.serial

import com.cm.gatecontroller.core.serial.model.GateControllerState
import kotlinx.coroutines.flow.Flow

interface SerialRepository {
    val deviceStatus: Flow<GateControllerState>
    suspend fun refreshStatus()
    suspend fun openGate()
    suspend fun closeGate()
    suspend fun stopGate()
    suspend fun toggleLamp()
    suspend fun setLedColor(color: String)
    suspend fun startTest()
    suspend fun stopTest()

    // Configuration Commands
    suspend fun refreshConfiguration()
    suspend fun setOpenLevel(level: Int)
    suspend fun setCloseLevel(level: Int)
    suspend fun setLampUsage(use: Boolean)
    suspend fun setBuzzerUsage(use: Boolean)
    suspend fun setLampOnPosition(position: String)
    suspend fun setLampOffPosition(position: String)
    suspend fun setLedOpenColor(color: String)
    suspend fun setLedOpenPosition(position: String)
    suspend fun setLedCloseColor(color: String)
    suspend fun setLedClosePosition(position: String)
    suspend fun setLoopAUsage(use: Boolean)
    suspend fun setLoopBUsage(use: Boolean)
    suspend fun setDelayTime(time: Int)
    suspend fun setRelay1Mode(mode: Int)
    suspend fun setRelay2Mode(mode: Int)
    suspend fun factoryReset(): Result<Unit>

    // Board Test Commands
    suspend fun setControlLamp(on: Boolean)
    suspend fun setControlRelay1(on: Boolean)
    suspend fun setControlRelay2(on: Boolean)
    suspend fun setControlLed(color: String)
    suspend fun setControlPosition(position: String) // AT+STPOS
    suspend fun openGateTest() // AT+OPEN
    suspend fun closeGateTest() // AT+CLOSE
    suspend fun stopGateTest() // AT+STOP
}
