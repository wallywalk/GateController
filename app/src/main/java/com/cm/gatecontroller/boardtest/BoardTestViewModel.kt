package com.cm.gatecontroller.boardtest

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
class BoardTestViewModel @Inject constructor(
    private val serialRepository: SerialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardTestUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<BoardTestSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        serialRepository.deviceStatus
            .onEach { state ->
                _uiState.update { it.copy(testState = state, isLoading = false) }
            }
            .catch { e ->
                _sideEffect.send(BoardTestSideEffect.ShowSnackbar("Error: ${e.message}"))
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)

        refreshStatus()
    }

    fun handleIntent(intent: BoardTestIntent) {
        viewModelScope.launch {
            try {
                when (intent) {
                    BoardTestIntent.RefreshStatus -> refreshStatus()
                    is BoardTestIntent.ToggleControlLamp -> serialRepository.setControlLamp(intent.on)
                    is BoardTestIntent.ToggleControlRelay1 -> serialRepository.setControlRelay1(intent.on)
                    is BoardTestIntent.ToggleControlRelay2 -> serialRepository.setControlRelay2(intent.on)
                    is BoardTestIntent.SetControlLed -> serialRepository.setControlLed(intent.color.name)
                    is BoardTestIntent.SetControlPosition -> serialRepository.setControlPosition(intent.position.name)
                    BoardTestIntent.OpenGate -> serialRepository.openGateTest()
                    BoardTestIntent.CloseGate -> serialRepository.closeGateTest()
                    BoardTestIntent.StopGate -> serialRepository.stopGateTest()
                }
            } catch (e: Exception) {
                _sideEffect.send(BoardTestSideEffect.ShowSnackbar("Command failed: ${e.message}"))
            }
        }
    }

    private fun refreshStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                serialRepository.refreshStatus()
            } catch (e: Exception) {
                _sideEffect.send(BoardTestSideEffect.ShowSnackbar("Failed to refresh status: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
