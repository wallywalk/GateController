package com.cm.gatecontroller.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<UserSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        authRepository.getCurrentUser()?.email?.let { email ->
            _uiState.update { it.copy(userEmail = email) }
        }
    }

    fun checkUserSession() {
        viewModelScope.launch {
            val result = authRepository.reloadUser()
            if (result.isFailure || authRepository.getCurrentUser() == null) {
                _sideEffect.send(UserSideEffect.NavigateToLogin)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _sideEffect.send(UserSideEffect.NavigateToLogin)
        }
    }
}
