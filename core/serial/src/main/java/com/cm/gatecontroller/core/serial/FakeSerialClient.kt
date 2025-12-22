package com.cm.gatecontroller.core.serial

import android.hardware.usb.UsbDevice
import com.cm.gatecontroller.core.serial.model.DeviceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class FakeSerialClient : SerialClient {

    private val _responses = MutableSharedFlow<String>()
    override val responses = _responses.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun getAvailableDevices(): List<DeviceItem> {
        return emptyList()
    }

    override fun requestPermission(device: UsbDevice) {
        // No-op for fake client
    }

    override suspend fun connect(deviceItem: DeviceItem): Result<Unit> {
        return Result.success(Unit)
    }

    override fun disconnect() {
        // No-op for fake client
    }

    override suspend fun sendCommand(command: String) {
        scope.launch {
            delay(200)
            val response = when (command) {
                "AT+CONFIG=VERSION" -> "curr_version=1.00JAN24"
                "AT+STGATE" -> "curr_gate_status=${if (Random.nextBoolean()) "OPEN" else "CLOSED"}"
                "AT+STLAMP" -> "curr_lamp=${if (Random.nextBoolean()) "ON" else "OFF"}"
                "AT+STLED" -> "curr_led=${listOf("RED", "GREEN", "BLUE").random()}"
                "AT+STMPWR" -> "curr_mpwr=${Random.nextFloat() * 10 + 20}"
                "AT+TESTCNT" -> "curr_test_cnt=${Random.nextInt(100)}"
                "AT+OPEN" -> "OK"
                "AT+CLOSE" -> "OK"
                else -> "OK"
            }
            _responses.emit(response)
        }
    }
}
