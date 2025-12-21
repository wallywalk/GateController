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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
    viewModel: ConfigViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showRelayMapDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is ConfigSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is ConfigSideEffect.ShowRelayMapDialog -> {
                    showRelayMapDialog = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar( // TODO: 반복되는 뷰
                title = { Text("CONFIGURATION") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    DeviceSettings(
                        uiState = uiState,
                        onIntent = viewModel::handleIntent
                    )
                }
                item {
                    ControlButtons(
                        onIntent = viewModel::handleIntent
                    )
                }
            }
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
                painter = painter,
                contentDescription = "Relay Mode Map",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(painter.aspectRatioOr()),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun DeviceSettings(uiState: ConfigUiState, onIntent: (ConfigIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LabelAndValue("VERSION", uiState.version)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IntDropdownSettingRow(
                label = "OPEN SPEED",
                valueText = uiState.levelOpen.toString(),
                options = (1..5).toList(),
                modifier = Modifier.weight(1f),
                onValueChange = {
                    onIntent(ConfigIntent.SetLevelOpen(it))
                }
            )
            IntDropdownSettingRow(
                label = "CLOSE SPEED",
                valueText = uiState.levelClose.toString(),
                options = (1..5).toList(),
                modifier = Modifier.weight(1f),
                onValueChange = {
                    onIntent(ConfigIntent.SetLevelClose(it))
                }
            )
        }
        TwoLabelSwitchRow(
            label1 = "LAMP",
            checked1 = uiState.lamp == UsageStatus.USE,
            onCheckedChange1 = { onIntent(ConfigIntent.SetLamp(it)) },
            label2 = "BUZZER",
            checked2 = uiState.buzzer == UsageStatus.USE,
            onCheckedChange2 = { onIntent(ConfigIntent.SetBuzzer(it)) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GateStatusSettingRow(
                label = "LAMP ON",
                options = listOf(GateStatus.OPENING, GateStatus.OPENED),
                selectedValue = uiState.lampPosOn,
                modifier = Modifier.weight(1f),
                onValueChange = { onIntent(ConfigIntent.SetLampPosOn(it)) }
            )
            GateStatusSettingRow(
                label = "LAMP OFF",
                options = listOf(GateStatus.CLOSING, GateStatus.CLOSED),
                selectedValue = uiState.lampPosOff,
                modifier = Modifier.weight(1f),
                onValueChange = { onIntent(ConfigIntent.SetLampPosOff(it)) }
            )
        }
        LedSettingRow(
            label = "LED OPEN",
            ledStatus = uiState.ledOpenColor,
            gateStatus = uiState.ledOpenPos,
            gateOptions = listOf(GateStatus.OPENING, GateStatus.OPENED),
            onLedStatusChange = { onIntent(ConfigIntent.SetLedOpen(it)) },
            onGateStatusChange = { onIntent(ConfigIntent.SetLedOpenPos(it)) }
        )
        LedSettingRow(
            label = "LED CLOSE",
            ledStatus = uiState.ledCloseColor,
            gateStatus = uiState.ledClosePos,
            gateOptions = listOf(GateStatus.CLOSING, GateStatus.CLOSED),
            onLedStatusChange = { onIntent(ConfigIntent.SetLedClose(it)) },
            onGateStatusChange = { onIntent(ConfigIntent.SetLedClosePos(it)) }
        )
        TwoLabelBadgeRow(
            label1 = "LOOP A",
            background1 = uiState.loopA.color,
            label2 = "LOOP B",
            background2 = uiState.loopB.color,
        )

        val enabled = uiState.loopB != UsageStatus.UNUSE
        val backgroundColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant else Gray400

        IntDropdownSettingRow(
            label = "DELAY TIME",
            valueText = "${uiState.delayTime} sec",
            options = (1..6).map { it * 10 },
            onValueChange = {
                onIntent(ConfigIntent.SetDelayTime(it))
            },
            modifier = Modifier.background(backgroundColor),
            enabled = enabled
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IntDropdownSettingRow(
                label = "RELAY1",
                valueText = uiState.relay1.toString(),
                options = (1..12).toList(),
                modifier = Modifier.weight(1f),
                onValueChange = {
                    onIntent(ConfigIntent.SetRelay1(it))
                }
            )
            IntDropdownSettingRow(
                label = "RELAY2",
                valueText = uiState.relay2.toString(),
                options = (1..12).toList(),
                modifier = Modifier.weight(1f),
                onValueChange = {
                    onIntent(ConfigIntent.SetRelay2(it))
                }
            )
        }
    }
}

@Composable
private fun IntDropdownSettingRow(
    label: String,
    valueText: String,
    options: List<Int>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
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
fun GateStatusSettingRow(
    label: String,
    options: List<GateStatus>,
    selectedValue: GateStatus,
    modifier: Modifier = Modifier,
    onValueChange: (GateStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Box {
            Text(text = selectedValue.name, fontSize = 16.sp)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name) },
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
    label1: String,
    checked1: Boolean,
    onCheckedChange1: (Boolean) -> Unit,
    label2: String,
    checked2: Boolean,
    onCheckedChange2: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LabelSwitch(
            label = label1,
            checked = checked1,
            onCheckedChange = onCheckedChange1,
            modifier = Modifier.weight(1f)
        )
        LabelSwitch(
            label = label2,
            checked = checked2,
            onCheckedChange = onCheckedChange2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun LedSettingRow(
    label: String,
    ledStatus: LedStatus,
    gateStatus: GateStatus,
    gateOptions: List<GateStatus>,
    onLedStatusChange: (LedStatus) -> Unit,
    onGateStatusChange: (GateStatus) -> Unit
) {
    var ledExpanded by remember { mutableStateOf(false) }
    var gateExpanded by remember { mutableStateOf(false) }

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
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(2f)
        )
        Box(modifier = Modifier.weight(1.5f)) {
            ColorBadge(
                color = ledStatus.color,
                colorName = ledStatus.name,
                modifier = Modifier.clickable { ledExpanded = true }
            )
            DropdownMenu(
                expanded = ledExpanded,
                onDismissRequest = { ledExpanded = false }
            ) {
                LedStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.name) },
                        onClick = {
                            onLedStatusChange(status)
                            ledExpanded = false
                        }
                    )
                }
            }
        }
        Box(modifier = Modifier.weight(1.5f), contentAlignment = Alignment.Center) {
            Text(gateStatus.name, modifier = Modifier.clickable { gateExpanded = true })
            DropdownMenu(
                expanded = gateExpanded,
                onDismissRequest = { gateExpanded = false }
            ) {
                gateOptions.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.name) },
                        onClick = {
                            onGateStatusChange(status)
                            gateExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ColorBadge(color: Color, colorName: String, modifier: Modifier = Modifier) {
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
    label1: String,
    background1: Color,
    label2: String,
    background2: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusBadge(
            text = label1,
            textColor = textColor,
            backgroundColor = background1,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.weight(1f)
        )
        StatusBadge(
            text = label2,
            textColor = textColor,
            backgroundColor = background2,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ControlButtons(onIntent: (ConfigIntent) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ControlButton(
            modifier = Modifier.weight(1f),
            text = "Relay\nMode",
            onClick = { onIntent(ConfigIntent.ShowRelayMap) }
        )
        ControlButton(
            modifier = Modifier.weight(1f),
            text = "Load\nconfig",
            onClick = { onIntent(ConfigIntent.LoadConfig) }
        )
        ControlButton(
            modifier = Modifier.weight(1f),
            text = "Save\nconfig",
            onClick = { onIntent(ConfigIntent.SaveConfig) }
        )
        ControlButton(
            text = "Factory",
            modifier = Modifier.weight(1f),
            onClick = { onIntent(ConfigIntent.FactoryReset) }
        )
    }
}