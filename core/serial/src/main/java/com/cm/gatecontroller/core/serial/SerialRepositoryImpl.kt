package com.cm.gatecontroller.core.serial

import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.parser.AtCommandParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SerialRepositoryImpl @Inject constructor(
    private val serialClient: SerialClient,
    private val parser: AtCommandParser
) : SerialRepository {

    private val _deviceStatus = MutableStateFlow(GateControllerState())
    override val deviceStatus: StateFlow<GateControllerState> = _deviceStatus.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            serialClient.responses.collect { response ->
                val newState = parser.parse(response, _deviceStatus.value)
                _deviceStatus.value = newState
            }
        }
    }

    // Monitoring Commands
    override suspend fun refreshStatus() {
        serialClient.sendCommand("AT+CONFIG=VERSION")
        serialClient.sendCommand("AT+STGATE")
        serialClient.sendCommand("AT+STLAMP")
        serialClient.sendCommand("AT+STLED")
        serialClient.sendCommand("AT+STRELAY1")
        serialClient.sendCommand("AT+STRELAY2")
        serialClient.sendCommand("AT+STPHOTO1")
        serialClient.sendCommand("AT+STPHOTO2")
        serialClient.sendCommand("AT+STOPEN1")
        serialClient.sendCommand("AT+STCLOSE1")
        serialClient.sendCommand("AT+STOPEN2")
        serialClient.sendCommand("AT+STCLOSE2")
        serialClient.sendCommand("AT+STOPEN3")
        serialClient.sendCommand("AT+STCLOSE3")
        serialClient.sendCommand("AT+STLOOPA")
        serialClient.sendCommand("AT+STLOOPB")
        serialClient.sendCommand("AT+STMPWR")
        serialClient.sendCommand("AT+TESTCNT")
        serialClient.sendCommand("AT+STDELATTIME")
    }

    override suspend fun openGate() {
        serialClient.sendCommand("AT+OPEN")
    }

    override suspend fun closeGate() {
        serialClient.sendCommand("AT+CLOSE")
    }

    override suspend fun stopGate() {
        serialClient.sendCommand("AT+STOP")
    }

    override suspend fun toggleLamp() {
//        serialClient.sendCommand("AT+STLAMP=TOGGLE") // TODO: Example command
    }

    override suspend fun setLedColor(color: String) {
        serialClient.sendCommand("AT+STLED=$color")
    }

    override suspend fun startTest() {
        serialClient.sendCommand("AT+TESTSTART")
    }

    override suspend fun stopTest() {
        serialClient.sendCommand("AT+TESTSTOP")
    }

    // Configuration Commands
    override suspend fun refreshConfiguration() {
        serialClient.sendCommand("AT+CONFIG=VERSION")
        serialClient.sendCommand("AT+CONFIG=LEVELOPEN")
        serialClient.sendCommand("AT+CONFIG=LEVELCLOSE")
        serialClient.sendCommand("AT+CONFIG=LAMP")
        serialClient.sendCommand("AT+CONFIG=BUZZER")
        serialClient.sendCommand("AT+CONFIG=LAMPPOSON")
        serialClient.sendCommand("AT+CONFIG=LAMPPOSOFF")
        serialClient.sendCommand("AT+CONFIG=LEDOPEN")
        serialClient.sendCommand("AT+CONFIG=LEDOPENPOS")
        serialClient.sendCommand("AT+CONFIG=LEDCLOSE")
        serialClient.sendCommand("AT+CONFIG=LEDCLOSEPOS")
        serialClient.sendCommand("AT+CONFIG=LOOPA")
        serialClient.sendCommand("AT+CONFIG=LOOPB")
        serialClient.sendCommand("AT+CONFIG=DELAYTIME")
        serialClient.sendCommand("AT+CONFIG=RELAY1")
        serialClient.sendCommand("AT+CONFIG=RELAY2")
    }

    override suspend fun setOpenLevel(level: Int) {
        serialClient.sendCommand("AT+SETLEVELOPEN=$level")
    }

    override suspend fun setCloseLevel(level: Int) {
        serialClient.sendCommand("AT+SETLEVELCLOSE=$level")
    }

    override suspend fun setLampUsage(use: Boolean) {
        val value = if (use) "USE" else "UNUSE"
        serialClient.sendCommand("AT+SETLAMP=$value")
    }

    override suspend fun setBuzzerUsage(use: Boolean) {
        val value = if (use) "USE" else "UNUSE"
        serialClient.sendCommand("AT+SETBUZZER=$value")
    }

    override suspend fun setLampOnPosition(position: String) {
        serialClient.sendCommand("AT+SETLAMPON=$position")
    }

    override suspend fun setLampOffPosition(position: String) {
        serialClient.sendCommand("AT+SETLAMPOFF=$position")
    }

    override suspend fun setLedOpenColor(color: String) {
        serialClient.sendCommand("AT+SETLEDOPEN=$color")
    }

    override suspend fun setLedOpenPosition(position: String) {
        serialClient.sendCommand("AT+SETLEDOPENPOS=$position")
    }

    override suspend fun setLedCloseColor(color: String) {
        serialClient.sendCommand("AT+SETLEDCLOSE=$color")
    }

    override suspend fun setLedClosePosition(position: String) {
        serialClient.sendCommand("AT+SETLEDCLOSEPOS=$position")
    }

    override suspend fun setLoopAUsage(use: Boolean) {
        val value = if (use) "USE" else "UNUSE"
        serialClient.sendCommand("AT+SETLOOPA=$value")
    }

    override suspend fun setLoopBUsage(use: Boolean) {
        val value = if (use) "USE" else "UNUSE"
        serialClient.sendCommand("AT+SETLOOPB=$value")
    }

    override suspend fun setDelayTime(time: Int) {
        serialClient.sendCommand("AT+SETDELAY=$time")
    }

    override suspend fun setRelay1Mode(mode: Int) {
        serialClient.sendCommand("AT+SETRELAY1=$mode")
    }

    override suspend fun setRelay2Mode(mode: Int) {
        serialClient.sendCommand("AT+SETRELAY2=$mode")
    }

    override suspend fun factoryReset(): Result<Unit> {
        return try {
            serialClient.sendCommand("AT+FACTORY")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Board Test Commands
    override suspend fun setControlLamp(on: Boolean) {
        val value = if (on) "ON" else "OFF"
        serialClient.sendCommand("AT+CTRLLAMP=$value")
    }

    override suspend fun setControlRelay1(on: Boolean) {
        val value = if (on) "ON" else "OFF"
        serialClient.sendCommand("AT+CTRLRELAY1=$value")
    }

    override suspend fun setControlRelay2(on: Boolean) {
        val value = if (on) "ON" else "OFF"
        serialClient.sendCommand("AT+CTRLRELAY2=$value")
    }

    override suspend fun setControlLed(color: String) {
        serialClient.sendCommand("AT+CTRLLED=$color")
    }

    override suspend fun setControlPosition(position: String) {
        serialClient.sendCommand("AT+STPOS=$position")
    }

    override suspend fun openGateTest() {
        serialClient.sendCommand("AT+OPEN")
    }

    override suspend fun closeGateTest() {
        serialClient.sendCommand("AT+CLOSE")
    }

    override suspend fun stopGateTest() {
        serialClient.sendCommand("AT+STOP")
    }
}