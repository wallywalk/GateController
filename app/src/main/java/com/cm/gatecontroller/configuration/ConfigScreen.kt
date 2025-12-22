package com.cm.gatecontroller.configuration

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.R
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.configuration.model.color
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.color
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.White100
import com.cm.gatecontroller.ui.theme.component.ControlButton
import com.cm.gatecontroller.ui.theme.component.LabelAndValue
import com.cm.gatecontroller.ui.theme.component.LabelSwitch
import com.cm.gatecontroller.ui.theme.component.StatusBadge
import com.cm.gatecontroller.util.aspectRatioOr
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    navController: NavHostController,
    viewModel: ConfigViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRelayMapDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is ConfigSideEffect.ShowSnackbar -> {
                    showSnackbar(effect.message)
                }

                is ConfigSideEffect.ShowRelayMapDialog -> {
                    showRelayMapDialog = true
                }
            }
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    DeviceSettings(
                        uiState = uiState,
                        onIntent = viewModel::handleIntent
                    )
                }
            }
            ControlButtons(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                onIntent = viewModel::handleIntent
            )
        }
    }

    if (showRelayMapDialog) {
        RelayMapDialog(onDismissRequest = { showRelayMapDialog = false })
    }
}

@Composable
fun RelayMapDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        val painter = painterResource(id = R.drawable.relay_mode_map)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable { onDismissRequest() }
                .padding(12.dp)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(painter.aspectRatioOr()),
                painter = painter,
                contentDescription = stringResource(R.string.config_relay_map_description),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun DeviceSettings(uiState: ConfigUiState, onIntent: (ConfigIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LabelAndValue(stringResource(R.string.common_version), uiState.version)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IntDropdownSettingRow(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.config_open_speed),
                valueText = uiState.levelOpen.toString(),
                options = (1..5).toList(),
                onValueChange = {
                    onIntent(ConfigIntent.SetLevelOpen(it))
                }
            )
            IntDropdownSettingRow(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.config_close_speed),
                valueText = uiState.levelClose.toString(),
                options = (1..5).toList(),
                onValueChange = {
                    onIntent(ConfigIntent.SetLevelClose(it))
                }
            )
        }
        TwoLabelSwitchRow(
            label1 = stringResource(R.string.common_lamp),
            checked1 = uiState.lamp == UsageStatus.USE,
            onCheckedChange1 = { onIntent(ConfigIntent.SetLamp(it)) },
            label2 = stringResource(R.string.config_buzzer),
            checked2 = uiState.buzzer == UsageStatus.USE,
            onCheckedChange2 = { onIntent(ConfigIntent.SetBuzzer(it)) }
        )
        TwoLabelSwitchRow(
            label1 = stringResource(R.string.config_lamp_on),
            checked1 = uiState.lampPosOn == GateStatus.OPENING,
            onCheckedChange1 = {
                onIntent(ConfigIntent.SetLampPosOn(it))
            },
            label2 = stringResource(R.string.config_lamp_off),
            checked2 = uiState.lampPosOff == GateStatus.CLOSING,
            onCheckedChange2 = {
                onIntent(ConfigIntent.SetLampPosOff(it))
            }
        )
        LedSettingRow(
            label = stringResource(R.string.config_led_open),
            ledStatus = uiState.ledOpenColor,
            gateStatus = uiState.ledOpenPos,
            onLedStatusChange = { onIntent(ConfigIntent.SetLedOpen(it)) },
            onGateStatusChange = { onIntent(ConfigIntent.SetLedOpenPos(it)) }
        )
        LedSettingRow(
            label = stringResource(R.string.config_led_close),
            ledStatus = uiState.ledCloseColor,
            gateStatus = uiState.ledClosePos,
            onLedStatusChange = { onIntent(ConfigIntent.SetLedClose(it)) },
            onGateStatusChange = { onIntent(ConfigIntent.SetLedClosePos(it)) }
        )
        TwoLabelBadgeRow(
            label1 = stringResource(R.string.common_loop_a),
            background1 = uiState.loopA.color,
            label2 = stringResource(R.string.common_loop_b),
            background2 = uiState.loopB.color,
        )

        val enabled = uiState.loopB != UsageStatus.UNUSE
        val backgroundColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant else Gray400

        IntDropdownSettingRow(
            modifier = Modifier.background(backgroundColor),
            label = stringResource(R.string.common_delay_time),
            valueText = "${uiState.delayTime} sec",
            options = (1..6).map { it * 10 },
            onValueChange = {
                onIntent(ConfigIntent.SetDelayTime(it))
            },
            enabled = enabled
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IntDropdownSettingRow(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.common_relay1),
                valueText = uiState.relay1.toString(),
                options = (1..12).toList(),
                onValueChange = {
                    onIntent(ConfigIntent.SetRelay1(it))
                }
            )
            IntDropdownSettingRow(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.common_relay2),
                valueText = uiState.relay2.toString(),
                options = (1..12).toList(),
                onValueChange = {
                    onIntent(ConfigIntent.SetRelay2(it))
                }
            )
        }
    }
}

@Composable
private fun IntDropdownSettingRow(
    modifier: Modifier = Modifier,
    label: String,
    valueText: String,
    options: List<Int>,
    onValueChange: (Int) -> Unit,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { expanded = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Box {
            Text(valueText, fontSize = 16.sp)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.toString()) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TwoLabelSwitchRow(
    modifier: Modifier = Modifier,
    label1: String,
    checked1: Boolean,
    onCheckedChange1: (Boolean) -> Unit,
    label2: String,
    checked2: Boolean,
    onCheckedChange2: (Boolean) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LabelSwitch(
            modifier = Modifier.weight(1f),
            label = label1,
            checked = checked1,
            onCheckedChange = onCheckedChange1
        )
        LabelSwitch(
            modifier = Modifier.weight(1f),
            label = label2,
            checked = checked2,
            onCheckedChange = onCheckedChange2
        )
    }
}

@Composable
fun LedSettingRow(
    label: String,
    ledStatus: LedStatus,
    gateStatus: GateStatus,
    onLedStatusChange: (LedStatus) -> Unit,
    onGateStatusChange: (Boolean) -> Unit
) {
    var ledExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(2f),
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Box(modifier = Modifier.weight(1.5f)) {
            ColorBadge(
                modifier = Modifier.clickable { ledExpanded = true },
                color = ledStatus.color,
                colorName = stringResourceForLedStatus(ledStatus)
            )
            DropdownMenu(
                expanded = ledExpanded,
                onDismissRequest = { ledExpanded = false }
            ) {
                LedStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(stringResourceForLedStatus(status)) },
                        onClick = {
                            onLedStatusChange(status)
                            ledExpanded = false
                        }
                    )
                }
            }
        }

        val configLedOpenLabel = stringResource(R.string.config_led_open)
        val configLedCloseLabel = stringResource(R.string.config_led_close)

        val isChecked = when (label) { // TODO: 하드코딩 제거
            configLedOpenLabel -> gateStatus == GateStatus.OPENING
            configLedCloseLabel -> gateStatus == GateStatus.CLOSING
            else -> false
        }

        LabelSwitch(
            modifier = Modifier.weight(1.5f),
            label = "",
            checked = isChecked,
            onCheckedChange = {
                when (label) {
                    configLedOpenLabel -> onGateStatusChange(it)
                    configLedCloseLabel -> onGateStatusChange(it)
                }
            }
        )
    }
}

@Composable
fun ColorBadge(modifier: Modifier = Modifier, color: Color, colorName: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = colorName,
            color = White100,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TwoLabelBadgeRow(
    modifier: Modifier = Modifier,
    label1: String,
    background1: Color,
    label2: String,
    background2: Color,
    textColor: Color = Color.Black
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusBadge(
            modifier = Modifier.weight(1f),
            text = label1,
            textColor = textColor,
            backgroundColor = background1,
            shape = RoundedCornerShape(24.dp)
        )
        StatusBadge(
            modifier = Modifier.weight(1f),
            text = label2,
            textColor = textColor,
            backgroundColor = background2,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun ControlButtons(
    modifier: Modifier = Modifier,
    onIntent: (ConfigIntent) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ControlButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.config_relay_map_button),
            onClick = { onIntent(ConfigIntent.ShowRelayMap) }
        )
        ControlButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.config_load_config_button),
            onClick = { onIntent(ConfigIntent.LoadConfig) }
        )
        ControlButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.config_save_config_button),
            onClick = { onIntent(ConfigIntent.SaveConfig) }
        )
        ControlButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.config_factory_button),
            onClick = { onIntent(ConfigIntent.FactoryReset) }
        )
    }
}

@Composable
fun stringResourceForLedStatus(ledStatus: LedStatus): String {
    return when (ledStatus) {
        LedStatus.OFF -> stringResource(R.string.common_off)
        LedStatus.BLUE -> stringResource(R.string.common_blue)
        LedStatus.GREEN -> stringResource(R.string.common_green)
        LedStatus.RED -> stringResource(R.string.common_red)
        LedStatus.WHITE -> stringResource(R.string.common_white)
    }
}