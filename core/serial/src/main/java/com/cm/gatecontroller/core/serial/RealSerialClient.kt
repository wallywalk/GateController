package com.cm.gatecontroller.core.serial

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.core.content.ContextCompat
import com.cm.gatecontroller.core.logger.Logger
import com.cm.gatecontroller.core.serial.model.DeviceItem
import com.hoho.android.usbserial.driver.Ch34xSerialDriver
import com.hoho.android.usbserial.driver.ProbeTable
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealSerialClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val debugLogger: Logger
) : SerialClient, SerialInputOutputManager.Listener {

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var serialPort: UsbSerialPort? = null
    private var ioManager: SerialInputOutputManager? = null

    private val scope = CoroutineScope(Dispatchers.IO)
    private val lineBuffer = StringBuilder()

    private val _responses = MutableSharedFlow<String>()
    override val responses = _responses.asSharedFlow()

    private val _permissionEvents = MutableSharedFlow<Boolean>()
    override val permissionEvents = _permissionEvents.asSharedFlow()

    private val permissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_USB_PERMISSION) {
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                debugLogger.d(TAG, "Permission broadcast received. Granted: $granted")
                scope.launch {
                    _permissionEvents.emit(granted)
                }
            }
        }
    }

    init {
        debugLogger.d(TAG, "init - registering permission receiver.")
        ContextCompat.registerReceiver(
            context,
            permissionReceiver,
            IntentFilter(ACTION_USB_PERMISSION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun getAvailableDevices(): List<DeviceItem> {
        debugLogger.d(TAG, "getAvailableDevices() called")
        val defaultProber = UsbSerialProber.getDefaultProber()
        val customTable = ProbeTable()
        customTable.addProduct(0x1A86, 0x7522, Ch34xSerialDriver::class.java)
        val customProber = UsbSerialProber(customTable)

        debugLogger.d(TAG, "Enumerating connected USB devices...")
        val deviceItems = mutableListOf<DeviceItem>()
        for (device in usbManager.deviceList.values) {
            val vid = device.vendorId
            val pid = device.productId
            debugLogger.d(
                TAG,
                "Checking device: ${device.deviceName} (VID: ${"0x%04X".format(vid)}, PID: ${
                    "0x%04X".format(pid)
                })"
            )

            var driver = defaultProber.probeDevice(device)
            if (driver != null) {
                debugLogger.d(
                    TAG,
                    "  -> Found driver with DefaultProber: ${driver.javaClass.simpleName}"
                )
            } else {
                driver = customProber.probeDevice(device)
                if (driver != null) {
                    debugLogger.d(
                        TAG,
                        "  -> Found driver with CustomProber: ${driver.javaClass.simpleName}"
                    )
                }
            }

            if (driver != null) {
                for (portNumber in 0 until driver.ports.size) {
                    val deviceItem = DeviceItem(driver.device, driver.ports[portNumber])
                    deviceItems.add(deviceItem)
                    debugLogger.d(
                        TAG,
                        "  -> Added Device: ${deviceItem.device.deviceName}, Port: $portNumber"
                    )
                }
            } else {
                debugLogger.d(TAG, "  -> No compatible driver found for this device.")
            }
        }

        if (deviceItems.isEmpty()) {
            debugLogger.d(TAG, "No devices with compatible drivers found in total.")
        }
        return deviceItems
    }

    override fun requestPermission(device: UsbDevice) {
        debugLogger.d(TAG, "requestPermission() for ${device.deviceName}")
        val intent = Intent(ACTION_USB_PERMISSION)
        intent.setPackage(context.packageName)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        usbManager.requestPermission(device, pendingIntent)
        debugLogger.d(TAG, "usbManager.requestPermission() called.")
    }

    override suspend fun connect(deviceItem: DeviceItem): Result<Unit> {
        debugLogger.d(TAG, "connect() called for device: ${deviceItem.device.deviceName}")
        val device = deviceItem.device
        val port = deviceItem.port ?: run {
            debugLogger.d(TAG, "Connection failed: No port found for device.")
            return Result.failure(IOException("No port found"))
        }

        debugLogger.d(TAG, "Checking USB permission.")
        if (!usbManager.hasPermission(device)) {
            debugLogger.d(TAG, "Permission not granted. Failing connect().")
            return Result.failure(SecurityException("USB permission not granted."))
        }
        debugLogger.d(TAG, "Permission already granted.")

        debugLogger.d(TAG, "Opening device...")
        val connection = usbManager.openDevice(device)
            ?: run {
                debugLogger.d(TAG, "Connection failed: Could not open device.")
                return Result.failure(IOException("Failed to open device"))
            }

        return try {
            debugLogger.d(TAG, "Opening port...")
            serialPort = port.apply {
                open(connection)
                debugLogger.d(TAG, "Port opened. Setting parameters...")
                setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                debugLogger.d(TAG, "Parameters set.")
            }
            ioManager = SerialInputOutputManager(serialPort, this).also {
                debugLogger.d(TAG, "Starting IOManager.")
                it.start()
            }
            debugLogger.d(TAG, "Connection successful.")
            Result.success(Unit)
        } catch (e: IOException) {
            debugLogger.d(TAG, "Connection failed during port setup: ${e.message}")
            disconnect()
            Result.failure(e)
        }
    }

    override fun disconnect() {
        debugLogger.d(TAG, "disconnect() called")
        try {
            ioManager?.stop()
            serialPort?.close()
        } catch (e: IOException) {
            debugLogger.d(TAG, "Error during disconnect: ${e.message}")
            e.printStackTrace()
        }
        ioManager = null
        serialPort = null
        debugLogger.d(TAG, "Disconnected.")
    }

    override suspend fun sendCommand(command: String) {
        debugLogger.d(TAG, "sendCommand: $command")
        try {
            val data = (command + "\r\n").toByteArray()
            serialPort?.write(data, 2000)
        } catch (e: IOException) {
            debugLogger.d(TAG, "Error sending command: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onNewData(data: ByteArray) {
        val receivedStr = String(data)
        debugLogger.d(
            TAG,
            "onNewData: ${data.size} bytes\n - STR: $receivedStr - HEX: ${
                data.joinToString(" ") {
                    "%02X".format(it)
                }
            }"
        )
        lineBuffer.append(receivedStr)

        var newlineIndex = lineBuffer.indexOf('\n')
        while (newlineIndex != -1) {
            val line = lineBuffer.substring(0, newlineIndex).trim()
            if (line.isNotEmpty()) {
                scope.launch {
                    _responses.emit(line)
                }
            }
            lineBuffer.delete(0, newlineIndex + 1)
            newlineIndex = lineBuffer.indexOf('\n')
        }
    }

    override fun onRunError(e: Exception) {
        debugLogger.d(TAG, "onRunError: ${e.message}")
        disconnect()
    }

    companion object {
        private const val ACTION_USB_PERMISSION = "com.cm.gatecontroller.USB_PERMISSION"
        private const val TAG = "RealSerialClient"
    }
}