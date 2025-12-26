package com.cm.gatecontroller.configuration.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Purple700

enum class UsageStatus {
    USE,
    UNUSE
}

val UsageStatus.color: Color
    @Composable
    get() = when (this) {
        UsageStatus.USE -> Purple700
        UsageStatus.UNUSE -> MaterialTheme.colorScheme.inversePrimary
    }
