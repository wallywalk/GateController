package com.cm.gatecontroller.boardtest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateStage
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.OnOff
import com.cm.gatecontroller.core.serial.model.PositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardTestScreen(
    viewModel: BoardTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val testState = uiState.testState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is BoardTestSideEffect.ShowSnackbar -> {
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
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
            Text("BOARD TEST", fontSize = 24.sp, fontWeight = MaterialTheme.typography.titleLarge.fontWeight)
            Spacer(modifier = Modifier.height(16.dp))

            // Version Display
            ConfigItem(label = "VERSION", value = testState.version)
            Spacer(modifier = Modifier.height(16.dp))

            // Output Test
            Text("OUTPUT TEST", fontSize = 20.sp, fontWeight = MaterialTheme.typography.titleMedium.fontWeight)
            Spacer(modifier = Modifier.height(8.dp))
            OutputTestSection(viewModel, testState)
            Spacer(modifier = Modifier.height(16.dp))

            // Input Test
            Text("INPUT TEST", fontSize = 20.sp, fontWeight = MaterialTheme.typography.titleMedium.fontWeight)
            Spacer(modifier = Modifier.height(8.dp))
            InputTestSection(testState)
            Spacer(modifier = Modifier.height(16.dp))

            // Operation Test
            Text("OPERATION TEST", fontSize = 20.sp, fontWeight = MaterialTheme.typography.titleMedium.fontWeight)
            Spacer(modifier = Modifier.height(8.dp))
            OperationTestSection(viewModel, testState)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OutputTestSection(viewModel: BoardTestViewModel, testState: GateControllerState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // LAMP
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("LAMP", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = testState.controlLamp == OnOff.ON,
                onCheckedChange = { viewModel.handleIntent(BoardTestIntent.ToggleControlLamp(it)) }
            )
        }
        // RELAY1
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RELAY1", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = testState.controlRelay1 == OnOff.ON,
                onCheckedChange = { viewModel.handleIntent(BoardTestIntent.ToggleControlRelay1(it)) }
            )
        }
        // RELAY2
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RELAY2", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = testState.controlRelay2 == OnOff.ON,
                onCheckedChange = { viewModel.handleIntent(BoardTestIntent.ToggleControlRelay2(it)) }
            )
        }
        // LED
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("LED", style = MaterialTheme.typography.bodyLarge)
            LedColorSelector(
                selectedColor = testState.controlLed,
                onColorSelected = { viewModel.handleIntent(BoardTestIntent.SetControlLed(it)) }
            )
        }
        // POSITION
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("POSITION", style = MaterialTheme.typography.bodyLarge)
            PositionSelector(
                selectedPosition = testState.controlPosition,
                onPositionSelected = { viewModel.handleIntent(BoardTestIntent.SetControlPosition(it)) }
            )
        }
    }
}

@Composable
fun InputTestSection(testState: GateControllerState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        InputStatusDisplay("PHOTO1", testState.inPhoto1)
        InputStatusDisplay("PHOTO2", testState.inPhoto2)
        InputStatusDisplay("LOOP A", testState.inLoopA)
        InputStatusDisplay("LOOP B", testState.inLoopB)
        InputStatusDisplay("OPEN1", testState.inOpen1)
        InputStatusDisplay("OPEN2", testState.inOpen2)
        InputStatusDisplay("OPEN3", testState.inOpen3)
        InputStatusDisplay("CLOSE1", testState.inClose1)
        InputStatusDisplay("CLOSE2", testState.inClose2)
        InputStatusDisplay("CLOSE3", testState.inClose3)
        InputStatusDisplay("SW OPEN", testState.swOpen)
        InputStatusDisplay("SW CLOSE", testState.swClose)
    }
}

@Composable
fun OperationTestSection(viewModel: BoardTestViewModel, testState: GateControllerState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.handleIntent(BoardTestIntent.OpenGate) }, modifier = Modifier.weight(1f)) {
                Text("GATE OPEN")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.handleIntent(BoardTestIntent.CloseGate) }, modifier = Modifier.weight(1f)) {
                Text("GATE CLOSE")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.handleIntent(BoardTestIntent.StopGate) }, modifier = Modifier.weight(1f)) {
                Text("STOP")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ConfigItem(label = "GATE STAGE", value = testState.gateStage.name)
    }
}

@Composable
fun LedColorSelector(selectedColor: LedColor, onColorSelected: (LedColor) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        OutlinedTextField(
            value = selectedColor.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
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

@Composable
fun PositionSelector(selectedPosition: PositionState, onPositionSelected: (PositionState) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        OutlinedTextField(
            value = selectedPosition.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PositionState.values().forEach { position ->
                DropdownMenuItem(
                    text = { Text(position.name) },
                    onClick = {
                        onPositionSelected(position)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun InputStatusDisplay(label: String, value: OnOff) {
    val isActive = value == OnOff.ON
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value.name,
            color = if (isActive) Color(0xFFFFA500) else Color.Gray,
            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
        )
    }
}

// Reusing ConfigItem from ConfigurationScreen
@Composable
fun ConfigItem(label: String, value: Any?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = MaterialTheme.typography.bodyLarge.fontWeight)
    }
}