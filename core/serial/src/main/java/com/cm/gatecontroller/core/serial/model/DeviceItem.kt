package com.cm.gatecontroller.core.serial.model

import android.hardware.usb.UsbDevice
import com.hoho.android.usbserial.driver.UsbSerialPort

data class DeviceItem(val device: UsbDevice, val port: UsbSerialPort?) {
    override fun toString(): String {
        return device.productName ?: device.deviceName
    }
}