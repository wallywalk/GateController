package com.cm.gatecontroller.configuration

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Green500
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.White100
import com.cm.gatecontroller.ui.theme.Yellow300
import com.cm.gatecontroller.ui.theme.component.ControlButton
import com.cm.gatecontroller.ui.theme.component.LabelAndValue
import com.cm.gatecontroller.ui.theme.component.LabelBadge
import com.cm.gatecontroller.ui.theme.component.LabelSwitch
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    navController: NavHostController,
    viewModel: ConfigViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is ConfigSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is ConfigSideEffect.ShowRelayMapDialog -> {
                    // TODO: Show Relay Map Dialog
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar( // TODO: 반복되는 뷰
                title = { Text("Configuration") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
                    DeviceSettings(uiState = uiState, onIntent = viewModel::handleIntent)
                }
                item {
                    ControlButtons(onIntent = viewModel::handleIntent)
                }
            }
        }
    }
}

@Composable
private fun DeviceSettings(uiState: ConfigUiState, onIntent: (ConfigIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LabelAndValue("Version", uiState.version)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IntDropdownSettingRow(
                label = "OPEN SPEED",
                options = (1..5).toList(),
                selectedValue = uiState.levelOpen,
                modifier = Modifier.weight(1f)
            ) {
                onIntent(ConfigIntent.SetLevelOpen(it))
            }
            IntDropdownSettingRow(
                label = "CLOSE SPEED",
                options = (1..5).toList(),
                selectedValue = uiState.levelClose,
                modifier = Modifier.weight(1f)
            ) {
                onIntent(ConfigIntent.SetLevelClose(it))
            }
        }
        TwoLabelSwitchRow(
            label1 = "LAMP",
            checked1 = uiState.lamp == UsageStatus.USE,
            label2 = "BUZZER",
            checked2 = uiState.buzzer == UsageStatus.USE
        )
        TwoLabelSwitchRow(
            label1 = "LAMP ON",
            checked1 = uiState.lampPosOn == GateStatus.OPENING,
            label2 = "LAMP OFF",
            checked2 = uiState.lampPosOff == GateStatus.CLOSING
        )
        LedSettingRow(
            label = "LED OPEN",
            color = uiState.ledOpenColor,
            isChecked = uiState.ledOpenPos == GateStatus.OPENING
        )
        LedSettingRow(
            label = "LED CLOSE",
            color = uiState.ledCloseColor,
            isChecked = uiState.ledClosePos == GateStatus.CLOSING
        )

        val loopABadgeColor = if (uiState.loopA == UsageStatus.USE) Yellow300 else Gray400
        val loopBBadgeColor = if (uiState.loopB == UsageStatus.USE) Yellow300 else Gray400

        TwoLabelBadgeRow(
            label1 = "LOOP A",
            background1 = loopABadgeColor,
            label2 = "LOOP B",
            background2 = loopBBadgeColor
        )
        LabelAndValue("DELAY TIME", "${uiState.delayTime} sec")
        TwoLabelValueRow(
            label1 = "RELAY1",
            value1 = uiState.relay1.toString(),
            label2 = "RELAY2",
            value2 = uiState.relay2.toString()
        )
    }
}

@Composable
fun TwoLabelValueRow(
    label1: String,
    value1: String,
    label2: String,
    value2: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabelAndValue(
            label = label1,
            value = value1,
            modifier = Modifier.weight(1f)
        )
        LabelAndValue(
            label = label2,
            value = value2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun IntDropdownSettingRow(
    label: String,
    options: List<Int>,
    selectedValue: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Box {
            Text(selectedValue.toString(), fontSize = 16.sp)
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
    label1: String,
    checked1: Boolean,
    label2: String,
    checked2: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LabelSwitch(
            label = label1,
            checked = checked1,
            modifier = Modifier.weight(1f)
        )
        LabelSwitch(
            label = label2,
            checked = checked2,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun LedSettingRow(
    label: String,
    color: LedStatus,
    isChecked: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
        ColorBadge(color.name, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            Switch(checked = isChecked, onCheckedChange = {}, enabled = false)
        }
    }
}

@Composable
fun ColorBadge(colorName: String, modifier: Modifier = Modifier) {
    val color = when (colorName.uppercase()) { // TODO: 하드코딩
        "RED" -> Red500
        "GREEN" -> Green500
        "BLUE" -> Blue600
        "YELLOW" -> Yellow300
        "WHITE" -> White100
        else -> Gray400
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            colorName,
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
        LabelBadge(
            label = label1,
            backgroundColor = background1,
            textColor = textColor,
            modifier = Modifier.weight(1f)
        )
        LabelBadge(
            label = label2,
            backgroundColor = background2,
            textColor = textColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ControlButtons(onIntent: (ConfigIntent) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ControlButton(
            "Relay\nMode",
            modifier = Modifier.weight(1f)
        ) { onIntent(ConfigIntent.ShowRelayMap) }
        ControlButton(
            "Load\nconfig",
            modifier = Modifier.weight(1f)
        ) { onIntent(ConfigIntent.LoadConfig) }
        ControlButton(
            "Save\nconfig",
            modifier = Modifier.weight(1f)
        ) { onIntent(ConfigIntent.SaveConfig) }
        ControlButton(
            "Factory",
            modifier = Modifier.weight(1f)
        ) { onIntent(ConfigIntent.FactoryReset) }
    }
}