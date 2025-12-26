package com.cm.gatecontroller.configuration

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.R
import com.cm.gatecontroller.configuration.model.color
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.color
import com.cm.gatecontroller.ui.component.ControlButton
import com.cm.gatecontroller.ui.component.LabelAndBadge
import com.cm.gatecontroller.ui.component.StatusBadge
import com.cm.gatecontroller.ui.theme.Purple700
import com.cm.gatecontroller.util.aspectRatioOr
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ConfigurationScreen(
    navController: NavHostController,
    viewModel: ConfigViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRelayMapDialog by remember { mutableStateOf(false) }

    val loadConfigLauncher = rememberLauncherForActivityResult( // TODO: Activity로 위임
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.handleIntent(ConfigIntent.LoadConfigFromUri(it))
            }
        }
    )

    val saveConfigLauncher = rememberLauncherForActivityResult( // TODO: Activity로 위임
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.handleIntent(ConfigIntent.SaveConfigToUri(it))
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is ConfigSideEffect.ShowSnackbar -> {
                    showSnackbar(effect.message)
                }

                is ConfigSideEffect.ShowRelayMapDialog -> {
                    showRelayMapDialog = true
                }

                is ConfigSideEffect.OpenFileForLoad -> {
                    loadConfigLauncher.launch(arrayOf("*/*"))
                }

                is ConfigSideEffect.CreateFileForSave -> {
                    saveConfigLauncher.launch(effect.fileName)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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

        uiState.progressMessage?.let {
            ProgressView(it)
        }
    }
}

@Composable
private fun ProgressView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false, onClick = {}),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(color = Color.White)
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RelayMapDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        val painter = painterResource(id = R.drawable.relay_mode_map)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
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
        LabelAndBadge(
            label = stringResource(R.string.common_version),
            badgeModifier = Modifier.weight(2f),
            badgeText = uiState.version
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IntDropdownSettingRow(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.config_open_speed),
                badgeText = uiState.levelOpen.toString(),
                options = (1..5).toList(),
                onValueChange = {
                    onIntent(ConfigIntent.SetLevelOpen(it))
                }
            )
            IntDropdownSettingRow(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.config_close_speed),
                badgeText = uiState.levelClose.toString(),
                options = (1..5).toList(),
                onValueChange = {
                    onIntent(ConfigIntent.SetLevelClose(it))
                }
            )
        }
        TwoLabelBadgeRow(
            label1 = stringResource(R.string.common_lamp),
            badgeText1 = uiState.lamp.name,
            badgeBackgroundColor1 = uiState.lamp.color,
            onClickBadge1 = { onIntent(ConfigIntent.SetLamp) },
            label2 = stringResource(R.string.config_buzzer),
            badgeText2 = uiState.buzzer.name,
            badgeBackgroundColor2 = uiState.buzzer.color,
            onClickBadge2 = { onIntent(ConfigIntent.SetBuzzer) }
        )
        TwoLabelBadgeRow(
            label1 = stringResource(R.string.config_lamp_on),
            badgeText1 = uiState.lampPosOn.name,
            onClickBadge1 = { onIntent(ConfigIntent.SetLampPosOn) },
            label2 = stringResource(R.string.config_lamp_off),
            badgeText2 = uiState.lampPosOff.name,
            onClickBadge2 = { onIntent(ConfigIntent.SetLampPosOff) }
        )
        LedSettingRow(
            label = stringResource(R.string.config_led_open),
            ledStatus = uiState.ledOpenColor,
            gateText = uiState.ledOpenPos.name,
            onLedStatusChange = { onIntent(ConfigIntent.SetLedOpen(it)) },
            onGateStatusChange = { onIntent(ConfigIntent.SetLedOpenPos) }
        )
        LedSettingRow(
            label = stringResource(R.string.config_led_close),
            ledStatus = uiState.ledCloseColor,
            gateText = uiState.ledClosePos.name,
            onLedStatusChange = { onIntent(ConfigIntent.SetLedClose(it)) },
            onGateStatusChange = { onIntent(ConfigIntent.SetLedClosePos) }
        )
        TwoBadgeRow(
            text1 = stringResource(R.string.common_loop_a),
            background1 = uiState.loopA.color,
            text2 = stringResource(R.string.common_loop_b),
            background2 = uiState.loopB.color,
        )
        IntDropdownSettingRow(
            label = stringResource(R.string.common_delay_time),
            badgeText = "${uiState.delayTime} sec",
            options = (1..6).map { it * 10 },
            onValueChange = { onIntent(ConfigIntent.SetDelayTime(it)) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IntDropdownSettingRow(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.common_relay1),
                badgeText = uiState.relay1.toString(),
                options = (1..12).toList(),
                onValueChange = { onIntent(ConfigIntent.SetRelay1(it)) }
            )
            IntDropdownSettingRow(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.common_relay2),
                badgeText = uiState.relay2.toString(),
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
    badgeText: String,
    options: List<Int>,
    onValueChange: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Purple700
        )
        Box(modifier = Modifier.weight(1f)) {
            StatusBadge(
                modifier = Modifier.fillMaxWidth(),
                text = badgeText,
                onClick = { expanded = true }
            )
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
private fun TwoLabelBadgeRow(
    modifier: Modifier = Modifier,
    label1: String,
    badgeText1: String,
    badgeBackgroundColor1: Color = MaterialTheme.colorScheme.inversePrimary,
    onClickBadge1: () -> Unit,
    label2: String,
    badgeText2: String,
    badgeBackgroundColor2: Color = MaterialTheme.colorScheme.inversePrimary,
    onClickBadge2: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LabelAndBadge(
            modifier = Modifier.weight(1f),
            label = label1,
            badgeModifier = Modifier.weight(1f),
            badgeText = badgeText1,
            badgeBackgroundColor = badgeBackgroundColor1,
            onClickBadge = onClickBadge1
        )
        LabelAndBadge(
            modifier = Modifier.weight(1f),
            label = label2,
            badgeModifier = Modifier.weight(1f),
            badgeText = badgeText2,
            badgeBackgroundColor = badgeBackgroundColor2,
            onClickBadge = onClickBadge2
        )
    }
}

@Composable
private fun LedSettingRow(
    label: String,
    ledStatus: LedStatus,
    gateText: String,
    onLedStatusChange: (LedStatus) -> Unit,
    onGateStatusChange: () -> Unit
) {
    var ledExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
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
        Row(
            modifier = Modifier.weight(3f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                StatusBadge(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResourceForLedStatus(ledStatus),
                    backgroundColor = ledStatus.color,
                    onClick = { ledExpanded = true }
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
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                StatusBadge(
                    modifier = Modifier.fillMaxWidth(),
                    text = gateText,
                    onClick = onGateStatusChange
                )
            }
        }
    }
}

@Composable
private fun TwoBadgeRow(
    modifier: Modifier = Modifier,
    text1: String,
    background1: Color,
    text2: String,
    background2: Color,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusBadge(
            modifier = Modifier.weight(1f),
            text = text1,
            backgroundColor = background1
        )
        StatusBadge(
            modifier = Modifier.weight(1f),
            text = text2,
            backgroundColor = background2
        )
    }
}

@Composable
private fun ControlButtons(
    modifier: Modifier = Modifier,
    onIntent: (ConfigIntent) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ControlButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            text = stringResource(R.string.config_relay_map_button),
            onClick = { onIntent(ConfigIntent.ShowRelayMap) }
        )
        ControlButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            text = stringResource(R.string.config_load_config_button),
            onClick = { onIntent(ConfigIntent.LoadConfig) }
        )
        ControlButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            text = stringResource(R.string.config_save_config_button),
            onClick = { onIntent(ConfigIntent.SaveConfig) }
        )
        ControlButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            text = stringResource(R.string.config_factory_button),
            onClick = { onIntent(ConfigIntent.FactoryReset) }
        )
    }
}

@Composable
private fun stringResourceForLedStatus(ledStatus: LedStatus): String { // TODO: 함수 통합
    return when (ledStatus) {
        LedStatus.OFF -> stringResource(R.string.common_off)
        LedStatus.BLUE -> stringResource(R.string.common_blue)
        LedStatus.GREEN -> stringResource(R.string.common_green)
        LedStatus.RED -> stringResource(R.string.common_red)
        LedStatus.WHITE -> stringResource(R.string.common_white)
    }
}