package com.cm.gatecontroller.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.core.serial.SerialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val serialRepository: SerialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<MonitoringSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        serialRepository.deviceStatus
            .onEach { status ->
                _uiState.update { it.copy(deviceStatus = status, isLoading = false) }
            }
            .catch { e ->
                _sideEffect.send(MonitoringSideEffect.ShowSnackbar("Error: ${e.message}"))
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)

        refresh()
    }

    fun handleIntent(intent: MonitoringIntent) {
        viewModelScope.launch {
            when (intent) {
                MonitoringIntent.RefreshStatus -> refresh()
                MonitoringIntent.ToggleTest -> toggleTest()
            }
        }
    }

    private suspend fun toggleTest() {
        if (_uiState.value.deviceStatus.isTestRunning) {
            serialRepository.stopTest()
        } else {
            serialRepository.startTest()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            try {
                serialRepository.refreshStatus()
            } catch (e: Exception) {
                _sideEffect.send(MonitoringSideEffect.ShowSnackbar("Failed to refresh: ${e.message}"))
            }
        }
    }
}
