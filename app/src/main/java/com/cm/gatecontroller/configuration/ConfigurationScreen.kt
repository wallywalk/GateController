package com.cm.gatecontroller.configuration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.LampPosition
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.UseState
import com.cm.gatecontroller.ui.theme.GateControllerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val config = uiState.configState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is ConfigurationSideEffect.ShowSnackbar -> {
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
                is ConfigurationSideEffect.ShowConfirmDialog -> {
                    // Show dialog logic here
                    // For now, we'll just confirm directly for Factory Reset
                    viewModel.confirmFactoryReset()
                }
                ConfigurationSideEffect.LaunchFilePicker -> {
                    scope.launch { snackbarHostState.showSnackbar("Load Config: File picker not implemented") }
                }
                ConfigurationSideEffect.LaunchFileSaver -> {
                    scope.launch { snackbarHostState.showSnackbar("Save Config: File saver not implemented") }
                }
                ConfigurationSideEffect.ShowRelayMapDialog -> {
                    scope.launch { snackbarHostState.showSnackbar("Relay Map Dialog not implemented") }
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("CONFIGURATION", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            // Version Display
            ConfigRow(
                label = "Version",
                content = {
                    ConfigButton(
                        text = config.version,
                        isActive = true,
                        activeColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )

            // Open/Close Level
            ConfigRow(
                content = {
                    ConfigButton(
                        label = "Open speed",
                        text = config.openLevel.toString(),
                        isActive = true,
                        activeColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConfigButton(
                        label = "Close speed",
                        text = config.closeLevel.toString(),
                        isActive = true,
                        activeColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            )

            // LAMP, BUZZER
            ConfigRow(
                content = {
                    ConfigToggleButton(
                        label = "LAMP",
                        currentState = config.lampUsage,
                        onToggle = { viewModel.handleIntent(ConfigurationIntent.SetLampUsage(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConfigToggleButton(
                        label = "BUZZER",
                        currentState = config.buzzerUsage,
                        onToggle = { viewModel.handleIntent(ConfigurationIntent.SetBuzzerUsage(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            )

            // LAMP ON, LAMP OFF
            ConfigRow(
                content = {
                    ConfigDropdownButton(
                        label = "LAMP ON",
                        currentValue = config.lampOnPosition,
                        options = LampPosition.values().toList(),
                        onValueChange = { viewModel.handleIntent(ConfigurationIntent.SetLampOnPosition(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConfigDropdownButton(
                        label = "LAMP OFF",
                        currentValue = config.lampOffPosition,
                        options = LampPosition.values().toList(),
                        onValueChange = { viewModel.handleIntent(ConfigurationIntent.SetLampOffPosition(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            )

            // LED OPEN, LED CLOSE
            ConfigRow(
                content = {
                    ConfigLedColorButton(
                        label = "LED OPEN",
                        currentColor = config.ledOpenColor,
                        onColorSelected = { viewModel.handleIntent(ConfigurationIntent.SetLedOpenColor(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConfigDropdownButton(
                        label = "LED OPEN POS",
                        currentValue = config.ledOpenPosition,
                        options = LampPosition.values().toList(),
                        onValueChange = { viewModel.handleIntent(ConfigurationIntent.SetLedOpenPosition(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            )
            ConfigRow(
                content = {
                    ConfigLedColorButton(
                        label = "LED CLOSE",
                        currentColor = config.ledCloseColor,
                        onColorSelected = { viewModel.handleIntent(ConfigurationIntent.SetLedCloseColor(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConfigDropdownButton(
                        label = "LED CLOSE POS",
                        currentValue = config.ledClosePosition,
                        options = LampPosition.values().toList(),
                        onValueChange = { viewModel.handleIntent(ConfigurationIntent.SetLedClosePosition(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            )

            // LOOP A, LOOP B
            ConfigRow(
                content = {
                    ConfigToggleButton(
                        label = "LOOP A",
                        currentState = config.loopA_conf,
                        onToggle = { viewModel.handleIntent(ConfigurationIntent.SetLoopAUsage(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConfigToggleButton(
                        label = "LOOP B",
                        currentState = config.loopB_conf,
                        onToggle = { viewModel.handleIntent(ConfigurationIntent.SetLoopBUsage(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            )

            // DELAY TIME
            ConfigRow(
                label = "DELAY TIME",
                content = {
                    ConfigButton(
                        text = "${config.delayTime_conf}sec",
                        isActive = true,
                        activeColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )

            // RELAY1, RELAY2
            ConfigRow(
                content = {
                    ConfigButton(
                        label = "RELAY1",
                        text = config.relay1Mode.toString(),
                        isActive = true,
                        activeColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConfigButton(
                        label = "RELAY2",
                        text = config.relay2Mode.toString(),
                        isActive = true,
                        activeColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ConfigButton(
                    text = "RELAY MAP",
                    onClick = { viewModel.handleIntent(ConfigurationIntent.ShowRelayMap) },
                    isActive = true,
                    activeColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ConfigButton(
                    text = "LOAD CONFIG",
                    onClick = { viewModel.handleIntent(ConfigurationIntent.LoadConfig) },
                    isActive = true,
                    activeColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ConfigButton(
                    text = "SAVE CONFIG",
                    onClick = { viewModel.handleIntent(ConfigurationIntent.SaveConfig) },
                    isActive = true,
                    activeColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ConfigButton(
                    text = "FACTORY RESET",
                    onClick = { viewModel.handleIntent(ConfigurationIntent.FactoryReset) },
                    isActive = true,
                    activeColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                )
            }
        }
    }
}

@Composable
fun ConfigRow(
    label: String? = null,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(120.dp) // Fixed width for label
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun ConfigButton(
    text: String,
    label: String? = null,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    textColor: Color = Color.White
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = if (isActive) activeColor else Color.Gray,
        contentColor = textColor
    )
    Button(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = RoundedCornerShape(8.dp),
        colors = buttonColors,
        modifier = modifier.height(50.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (label != null) {
                Text(label, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
            Text(text, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ConfigToggleButton(
    label: String,
    currentState: UseState,
    onToggle: (UseState) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(8.dp))
        Row(modifier = Modifier.weight(1f)) {
            ConfigButton(
                text = "USE",
                isActive = currentState == UseState.USE,
                activeColor = MaterialTheme.colorScheme.primary,
                onClick = { onToggle(UseState.USE) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ConfigButton(
                text = "UNUSE",
                isActive = currentState == UseState.UNUSE,
                activeColor = MaterialTheme.colorScheme.primary,
                onClick = { onToggle(UseState.UNUSE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> ConfigDropdownButton(
    label: String,
    currentValue: T,
    options: List<T>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = currentValue.name.replace("_", " "),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name.replace("_", " ")) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigLedColorButton(
    label: String,
    currentColor: LedColor,
    onColorSelected: (LedColor) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = currentColor.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                LedColor.values().forEach { color ->
                    DropdownMenuItem(
                        text = { Text(color.name) },
                        onClick = {
                            onColorSelected(color)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// Mock ViewModel for Preview
class MockConfigurationViewModel : ConfigurationViewModel(
    object : com.cm.gatecontroller.core.serial.SerialRepository {
        override val deviceStatus: StateFlow<GateControllerState> = MutableStateFlow(
            GateControllerState(
                version = "1.00JAN24",
                openLevel = 3,
                closeLevel = 2,
                lampUsage = UseState.USE,
                buzzerUsage = UseState.UNUSE,
                lampOnPosition = LampPosition.OPENING_START,
                lampOffPosition = LampPosition.CLOSED_STOP,
                ledOpenColor = LedColor.BLUE,
                ledOpenPosition = LampPosition.OPENED_STOP,
                ledCloseColor = LedColor.RED,
                ledClosePosition = LampPosition.CLOSING_START,
                loopA_conf = UseState.USE,
                loopB_conf = UseState.UNUSE,
                delayTime_conf = 40,
                relay1Mode = 7,
                relay2Mode = 11
            )
        )

        override suspend fun refreshStatus() {}
        override suspend fun openGate() {}
        override suspend fun closeGate() {}
        override suspend fun stopGate() {}
        override suspend fun toggleLamp() {}
        override suspend fun setLedColor(color: String) {}
        override suspend fun startTest() {}
        override suspend fun stopTest() {}
        override suspend fun refreshConfiguration() {}
        override suspend fun setOpenLevel(level: Int) {}
        override suspend fun setCloseLevel(level: Int) {}
        override suspend fun setLampUsage(use: Boolean) {}
        override suspend fun setBuzzerUsage(use: Boolean) {}
        override suspend fun setLampOnPosition(position: String) {}
        override suspend fun setLampOffPosition(position: String) {}
        override suspend fun setLedOpenColor(color: String) {}
        override suspend fun setLedOpenPosition(position: String) {}
        override suspend fun setLedCloseColor(color: String) {}
        override suspend fun setLedClosePosition(position: String) {}
        override suspend fun setLoopAUsage(use: Boolean) {}
        override suspend fun setLoopBUsage(use: Boolean) {}
        override suspend fun setDelayTime(time: Int) {}
        override suspend fun setRelay1Mode(mode: Int) {}
        override suspend fun setRelay2Mode(mode: Int) {}
        override suspend fun factoryReset(): Result<Unit> { return Result.success(Unit) }
        override suspend fun setControlLamp(on: Boolean) {}
        override suspend fun setControlRelay1(on: Boolean) {}
        override suspend fun setControlRelay2(on: Boolean) {}
        override suspend fun setControlLed(color: String) {}
        override suspend fun setControlPosition(position: String) {}
        override suspend fun openGateTest() {}
        override suspend fun closeGateTest() {}
        override suspend fun stopGateTest() {}
    },
    object : Context() {
        override fun getApplicationContext(): Context = this
        // Implement other Context methods if needed for the preview, or use mockk/mockito
        // For now, a minimal mock context might suffice if not directly used by the ViewModel's init
    }
)

@Preview(showBackground = true)
@Composable
fun ConfigurationScreenPreview() {
    GateControllerTheme {
        ConfigurationScreen(viewModel = MockConfigurationViewModel())
    }
}