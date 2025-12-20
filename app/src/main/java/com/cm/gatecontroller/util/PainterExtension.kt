package com.cm.gatecontroller.util

import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.painter.Painter

fun Painter.aspectRatioOr(defaultRatio: Float = 16f / 9f): Float {
    val size = intrinsicSize
    return if (size.isSpecified && size.width > 0f && size.height > 0f) {
        size.width / size.height
    } else {
        defaultRatio
    }
}