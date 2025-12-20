package com.cm.gatecontroller.core.serial

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.core.content.ContextCompat
import com.cm.gatecontroller.core.serial.model.DeviceItem
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val ACTION_USB_PERMISSION = "com.cm.gatecontroller.USB_PERMISSION"

@Singleton
class RealSerialClient @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SerialClient, SerialInputOutputManager.Listener {

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var serialPort: UsbSerialPort? = null
    private var ioManager: SerialInputOutputManager? = null

    private val _responses = MutableSharedFlow<String>()
    override val responses = _responses.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val lineBuffer = StringBuilder()

    override fun getAvailableDevices(): List<DeviceItem> {
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        return availableDrivers.map { driver ->
            DeviceItem(driver.device, driver.ports.firstOrNull())
        }
    }

    override suspend fun connect(deviceItem: DeviceItem): Result<Unit> {
        val device = deviceItem.device
        val port = deviceItem.port ?: return Result.failure(IOException("No port found"))

        if (!usbManager.hasPermission(device)) {
            val permissionGranted = requestUsbPermission(device)
            if (!permissionGranted) {
                return Result.failure(SecurityException("USB permission not granted"))
            }
        }

        val connection = usbManager.openDevice(device)
            ?: return Result.failure(IOException("Failed to open device"))

        return try {
            serialPort = port.apply {
                open(connection)
                setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            }
            ioManager = SerialInputOutputManager(serialPort, this).also {
                it.start()
            }
            Result.success(Unit)
        } catch (e: IOException) {
            disconnect()
            Result.failure(e)
        }
    }

    private suspend fun requestUsbPermission(device: UsbDevice): Boolean {
        val permissionDeferred = CompletableDeferred<Boolean>()
        val intent = Intent(ACTION_USB_PERMISSION)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (ACTION_USB_PERMISSION == intent.action) {
                    synchronized(this) {
                        context.unregisterReceiver(this)
                        val granted =
                            intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                        permissionDeferred.complete(granted)
                    }
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            broadcastReceiver,
            IntentFilter(ACTION_USB_PERMISSION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        usbManager.requestPermission(device, pendingIntent)

        return permissionDeferred.await()
    }

    override fun disconnect() {
        try {
            ioManager?.stop()
            serialPort?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        ioManager = null
        serialPort = null
    }

    override suspend fun sendCommand(command: String) {
        try {
            val data = (command + "\r\n").toByteArray()
            serialPort?.write(data, 2000)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onNewData(data: ByteArray) {
        val receivedStr = String(data)
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
        disconnect()
    }
}