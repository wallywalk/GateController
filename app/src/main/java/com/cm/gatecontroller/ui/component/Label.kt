package com.cm.gatecontroller.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            modifier = badgeModifier,
            text = badgeText,
            backgroundColor = badgeBackgroundColor ?: MaterialTheme.colorScheme.inversePrimary,
            onClick = onClickBadge
        )
    }
}

@Composable
fun LabelAndButton(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f), // TODO: wrapContent...
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        ControlButton(
            modifier = Modifier.weight(1f),
            text = value,
            fontSize = 18.sp,
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        )
    }
}