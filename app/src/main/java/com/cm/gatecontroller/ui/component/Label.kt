package com.cm.gatecontroller.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cm.gatecontroller.ui.theme.Purple700

@Composable
fun LabelAndBadge(
    modifier: Modifier = Modifier,
    label: String,
    badgeModifier: Modifier = Modifier,
    badgeText: String,
    badgeBackgroundColor: Color? = null,
    onClickBadge: (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Purple700
        )
        StatusBadge(
            modifier = badgeModifier.weight(2f), // TODO: 위임을...
            text = badgeText,
            backgroundColor = badgeBackgroundColor ?: MaterialTheme.colorScheme.inversePrimary,
            onClick = onClickBadge
        )
    }
}