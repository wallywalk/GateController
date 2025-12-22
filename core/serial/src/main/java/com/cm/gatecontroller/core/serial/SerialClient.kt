package com.cm.gatecontroller.core.serial

import android.hardware.usb.UsbDevice
import com.cm.gatecontroller.core.serial.model.DeviceItem
import kotlinx.coroutines.flow.Flow

interface SerialClient {
    val responses: Flow<String>
    fun getAvailableDevices(): List<DeviceItem>
    fun requestPermission(device: UsbDevice)
    suspend fun connect(deviceItem: DeviceItem): Result<Unit>
    fun disconnect()
    suspend fun sendCommand(command: String)
}
