package com.cm.gatecontroller.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.SwitchState
import com.cm.gatecontroller.monitoring.model.MonitoringGateStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus
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

    private val _sideEffect = MutableSharedFlow<MonitoringSideEffect>() // TODO: Channel
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

    private fun GateControllerState.toMonitoringUiState(): MonitoringUiState {
        return MonitoringUiState( // TODO: copy 불가능?
            version = this.version,
            gateState = when (this.mGateState) {
                mGateState.OPEN -> MonitoringGateStatus.OPEN
                else -> MonitoringGateStatus.CLOSE
            },
            lampState = when (this.lampState) {
                SwitchState.ON -> SwitchStatus.ON
                else -> SwitchStatus.OFF
            },
            ledState = when (this.ledColor) {
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
                else -> LedStatus.OFF
            },
            relay1State = if (this.relay1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            relay2State = if (this.relay2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            photo1State = if (this.photo1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            photo2State = if (this.photo2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open1State = if (this.open1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open2State = if (this.open2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open3State = if (this.open3 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close1State = if (this.close1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close2State = if (this.close2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close3State = if (this.close3 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            loopAState = if (this.loopA_mon == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            loopBState = if (this.loopB_mon == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            mainPower = this.mainPower,
            testCount = this.testCount.toIntOrNull() ?: 0,
            delayTime = this.delayTime_mon.replace("sec", "").toIntOrNull() ?: 0,
            isTestRunning = this.isTestRunning
        )
    }
}