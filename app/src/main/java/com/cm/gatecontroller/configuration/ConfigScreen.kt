package com.cm.gatecontroller.configuration

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.configuration.model.LampStatus
import com.cm.gatecontroller.configuration.model.UsageStatus
import com.cm.gatecontroller.model.LedStatus
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
            TopAppBar(
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
                    DeviceSettingsCard(uiState = uiState, onIntent = viewModel::handleIntent)
                }
                item {
                    ActionButtonsCard(onIntent = viewModel::handleIntent)
                }
            }
        }
    }
}

@Composable
private fun DeviceSettingsCard(
    uiState: ConfigUiState,
    onIntent: (ConfigIntent) -> Unit
) {
    SettingsCard(title = "Device Settings") {
        Text("Version: ${uiState.version}", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        IntDropdownSettingRow("Level Open", (1..5).toList(), uiState.levelOpen) {
            onIntent(
                ConfigIntent.SetLevelOpen(it)
            )
        }
        IntDropdownSettingRow("Level Close", (1..5).toList(), uiState.levelClose) {
            onIntent(
                ConfigIntent.SetLevelClose(it)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        SwitchSettingRow(
            "Lamp",
            uiState.lamp == UsageStatus.USE
        ) { onIntent(ConfigIntent.SetLamp(if (it) UsageStatus.USE else UsageStatus.UNUSE)) }
        SwitchSettingRow(
            "Buzzer",
            uiState.buzzer == UsageStatus.USE
        ) { onIntent(ConfigIntent.SetBuzzer(if (it) UsageStatus.USE else UsageStatus.UNUSE)) }
        Spacer(modifier = Modifier.height(8.dp))

        EnumDropdownSettingRow(
            "Lamp On Position",
            LampStatus.entries.toTypedArray(),
            uiState.lampPosOn
        ) { onIntent(ConfigIntent.SetLampPosOn(it)) }
        EnumDropdownSettingRow(
            "Lamp Off Position",
            LampStatus.entries.toTypedArray(),
            uiState.lampPosOff
        ) { onIntent(ConfigIntent.SetLampPosOff(it)) }
        Spacer(modifier = Modifier.height(8.dp))

        EnumDropdownSettingRow(
            "LED Open Color",
            LedStatus.entries.toTypedArray(),
            uiState.ledOpenColor
        ) { onIntent(ConfigIntent.SetLedOpen(it)) }
        IntDropdownSettingRow("LED Open Position", (1..4).toList(), uiState.ledOpenPos) {
            onIntent(
                ConfigIntent.SetLedOpenPos(it)
            )
        }
        EnumDropdownSettingRow(
            "LED Close Color",
            LedStatus.entries.toTypedArray(),
            uiState.ledClose
        ) { onIntent(ConfigIntent.SetLedClose(it)) }
        IntDropdownSettingRow(
            "LED Close Position",
            (1..4).toList(),
            uiState.ledClosePos
        ) { onIntent(ConfigIntent.SetLedClosePos(it)) }
        Spacer(modifier = Modifier.height(8.dp))

        SwitchSettingRow(
            "Loop A",
            uiState.loopA == UsageStatus.USE
        ) { onIntent(ConfigIntent.SetLoopA(if (it) UsageStatus.USE else UsageStatus.UNUSE)) }
        SwitchSettingRow(
            "Loop B",
            uiState.loopB == UsageStatus.USE
        ) { onIntent(ConfigIntent.SetLoopB(if (it) UsageStatus.USE else UsageStatus.UNUSE)) }
        Spacer(modifier = Modifier.height(8.dp))

        IntDropdownSettingRow(
            "Delay Time (sec)",
            listOf(0, 5, 10, 15, 30, 60),
            uiState.delayTime
        ) { onIntent(ConfigIntent.SetDelayTime(it)) }
        Spacer(modifier = Modifier.height(8.dp))

        SwitchSettingRow("Relay 1", uiState.relay1 == UsageStatus.USE) {
            onIntent(
                ConfigIntent.SetRelay1(if (it) UsageStatus.USE else UsageStatus.UNUSE)
            )
        }
        SwitchSettingRow("Relay 2", uiState.relay2 == UsageStatus.USE) {
            onIntent(
                ConfigIntent.SetRelay2(if (it) UsageStatus.USE else UsageStatus.UNUSE)
            )
        }
    }
}

@Composable
private fun ActionButtonsCard(onIntent: (ConfigIntent) -> Unit) {
    SettingsCard(title = "Actions") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { onIntent(ConfigIntent.SaveConfig) }) { Text("Save Config") }
            Button(onClick = { onIntent(ConfigIntent.LoadConfig) }) { Text("Load Config") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { onIntent(ConfigIntent.FactoryReset) }) { Text("Factory Reset") }
            Button(onClick = { onIntent(ConfigIntent.ShowRelayMap) }) { Text("Relay Map") }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun SwitchSettingRow(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun IntDropdownSettingRow(
    label: String,
    options: List<Int>,
    selectedValue: Int,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp)
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedValue.toString())
            }
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
private fun <T : Enum<T>> EnumDropdownSettingRow(
    label: String,
    options: Array<T>,
    selectedValue: T,
    onValueChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp)
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedValue.name)
            }
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