package com.cm.gatecontroller.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Yellow300

enum class SwitchStatus {
    ON,
    OFF
}

val SwitchStatus.color: Color // TODO: 이렇게 쓰는게 맞을까?
    @Composable
    get() = when (this) {
        SwitchStatus.ON -> Yellow300
        SwitchStatus.OFF -> MaterialTheme.colorScheme.primary
    }