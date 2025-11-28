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

    override suspend fun refreshStatus() {
        serialClient.sendCommand("AT+CONFIG=VERSION")
        serialClient.sendCommand("AT+STGATE")
        serialClient.sendCommand("AT+STLAMP")
        serialClient.sendCommand("AT+STLED")
        serialClient.sendCommand("AT+STMPWR")
        serialClient.sendCommand("AT+TESTCNT")
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
        serialClient.sendCommand("AT+STLAMP=TOGGLE") // TODO: Example command
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
}
