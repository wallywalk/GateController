package com.cm.gatecontroller.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.R
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.SwitchState
import com.cm.gatecontroller.core.serial.model.AccessMode
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
            gateModeRes = when (this.accessMode) {
                AccessMode.OPEN -> R.string.common_open
                else -> R.string.common_close
            },
            lamp = when (this.lampState) {
                SwitchState.ON -> SwitchStatus.ON
                else -> SwitchStatus.OFF
            },
            led = when (this.ledColor) {
                LedColor.BLUE -> LedStatus.BLUE
                LedColor.GREEN -> LedStatus.GREEN
                LedColor.RED -> LedStatus.RED
                LedColor.WHITE -> LedStatus.WHITE
                else -> LedStatus.OFF
            },
            relay1 = if (this.stRelay1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            relay2 = if (this.stRelay2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            photo1 = if (this.stPhoto1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            photo2 = if (this.stPhoto2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open1 = if (this.stOpen1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open2 = if (this.stOpen2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            open3 = if (this.stOpen3 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close1 = if (this.stClose1 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close2 = if (this.stClose2 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            close3 = if (this.stClose3 == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            loopA = if (this.stLoopA == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            loopB = if (this.stLoopB == SwitchState.ON) SwitchStatus.ON else SwitchStatus.OFF,
            mainPower = this.mainPower,
            testCount = this.testCount.toIntOrNull() ?: 0,
            delayTime = this.stDelayTime.replace("sec", "").toIntOrNull() ?: 0,
            isTestRunning = this.isTestRunning
        )
    }
}