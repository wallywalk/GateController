package com.cm.gatecontroller.configuration.model

import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Yellow300

enum class UsageStatus {
    USE,
    UNUSE
}

val UsageStatus.color: Color
    get() = when (this) {
        UsageStatus.USE -> Yellow300
        UsageStatus.UNUSE -> Gray400
    }
