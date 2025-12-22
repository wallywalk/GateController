package com.cm.gatecontroller.core.logger

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugLogger @Inject constructor() : Logger {

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs = _logs.asStateFlow()

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
        addLog("D", tag, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
        addLog("E", tag, "$message\n${throwable?.stackTraceToString().orEmpty()}")
    }

    private fun addLog(level: String, tag: String, message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
        val newLog = "[$timestamp] $level/$tag: $message"
        _logs.value = (_logs.value + newLog).takeLast(100)
    }
}
