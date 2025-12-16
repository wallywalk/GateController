package com.cm.gatecontroller.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.OnOff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val serialRepository: SerialRepository
) : ViewModel() {

    private val _sideEffect = MutableSharedFlow<MonitoringSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    val uiState = serialRepository.deviceStatus
        .map { it.toMonitoringUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MonitoringUiState()
        )

    init {
        viewModelScope.launch {
            serialRepository.refreshStatus()
        }
    }

    fun handleIntent(intent: MonitoringIntent) {
        when (intent) {
            is MonitoringIntent.ToggleTest -> toggleTest()
        }
    }

    private fun toggleTest() {
        viewModelScope.launch {
            if (uiState.value.isTestRunning) {
                serialRepository.stopTest()
            } else {
                serialRepository.startTest()
            }
        }
    }
}

private fun GateControllerState.toMonitoringUiState(): MonitoringUiState {
    return MonitoringUiState(
        version = this.version,
        gateState = when (this.gateState) {
            GateState.OPEN -> GateStatus.OPEN
            else -> GateStatus.CLOSE
        },
        lampState = when (this.lampState) {
            OnOff.ON -> OnOffStatus.ON
            else -> OnOffStatus.OFF
        },
        ledState = when (this.ledColor) {
            LedColor.BLUE -> LedStatus.BLUE
            LedColor.GREEN -> LedStatus.GREEN
            LedColor.RED -> LedStatus.RED
            LedColor.WHITE -> LedStatus.WHITE
            else -> LedStatus.OFF
        },
        relay1State = if (this.relay1 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        relay2State = if (this.relay2 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        photo1State = if (this.photo1 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        photo2State = if (this.photo2 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        open1State = if (this.open1 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        open2State = if (this.open2 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        open3State = if (this.open3 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        close1State = if (this.close1 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        close2State = if (this.close2 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        close3State = if (this.close3 == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        loopAState = if (this.loopA_mon == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        loopBState = if (this.loopB_mon == OnOff.ON) OnOffStatus.ON else OnOffStatus.OFF,
        mainPower = this.mainPower,
        testCount = this.testCount.toIntOrNull() ?: 0,
        delayTime = this.delayTime_mon.replace("sec", "").toIntOrNull() ?: 0,
        isTestRunning = this.isTestRunning
    )
}