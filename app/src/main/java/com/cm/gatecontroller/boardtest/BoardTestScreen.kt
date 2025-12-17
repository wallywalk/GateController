@file:OptIn(ExperimentalMaterial3Api::class)

package com.cm.gatecontroller.boardtest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.SwitchState
import com.cm.gatecontroller.core.serial.model.BoardPositionState
import kotlinx.coroutines.launch

@Composable
fun BoardTestScreen(
    viewModel: BoardTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val testState = uiState.testState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // TODO: scope 필요 여부

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
            Text(
                "BOARD TEST",
                fontSize = 24.sp,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
            )
            Spacer(modifier = Modifier.height(16.dp))

            ConfigItem(label = "VERSION", value = testState.version)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "OUTPUT TEST",
                fontSize = 20.sp,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutputTestSection(viewModel, testState)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "INPUT TEST",
                fontSize = 20.sp,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
            )
            Spacer(modifier = Modifier.height(8.dp))
            InputTestSection(testState)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "OPERATION TEST",
                fontSize = 20.sp,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
            )
            Spacer(modifier = Modifier.height(8.dp))
            OperationTestSection(viewModel, testState)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OutputTestSection(viewModel: BoardTestViewModel, testState: GateControllerState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("LAMP", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = testState.controlLamp == SwitchState.ON,
                onCheckedChange = { viewModel.handleIntent(BoardTestIntent.ToggleControlLamp(it)) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RELAY1", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = testState.controlRelay1 == SwitchState.ON,
                onCheckedChange = { viewModel.handleIntent(BoardTestIntent.ToggleControlRelay1(it)) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RELAY2", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = testState.controlRelay2 == SwitchState.ON,
                onCheckedChange = { viewModel.handleIntent(BoardTestIntent.ToggleControlRelay2(it)) }
            )
        }
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
            Button(
                onClick = { viewModel.handleIntent(BoardTestIntent.OpenGate) },
                modifier = Modifier.weight(1f)
            ) {
                Text("GATE OPEN")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.handleIntent(BoardTestIntent.CloseGate) },
                modifier = Modifier.weight(1f)
            ) {
                Text("GATE CLOSE")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.handleIntent(BoardTestIntent.StopGate) },
                modifier = Modifier.weight(1f)
            ) {
                Text("STOP")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ConfigItem(label = "GATE STAGE", value = testState.boardGateState.name)
    }
}

@Composable
fun LedColorSelector( // TODO: PositionSelector와 함께 함수 통합
    selectedColor: LedColor,
    onColorSelected: (LedColor) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { !expanded },
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
            LedColor.entries.forEach { color -> // TODO: serial model 제거
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
fun PositionSelector(selectedPosition: BoardPositionState, onPositionSelected: (BoardPositionState) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { !expanded },
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
            BoardPositionState.entries.forEach { position -> // TODO: serial model 제거
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
fun InputStatusDisplay(label: String, value: SwitchState) {
    val isActive = value == SwitchState.ON
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
        Text(
            value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
        )
    }
}