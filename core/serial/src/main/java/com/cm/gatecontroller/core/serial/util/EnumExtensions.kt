package com.cm.gatecontroller.core.serial.util

inline fun <reified T : Enum<T>> T?.orParsed(value: String): T? {
    return safeValueOf<T>(value) ?: this
}

inline fun <reified T : Enum<T>> safeValueOf(value: String): T? {
    return runCatching {
        java.lang.Enum.valueOf(T::class.java, value.uppercase().replace(" ", "_"))
    }.getOrNull()
}