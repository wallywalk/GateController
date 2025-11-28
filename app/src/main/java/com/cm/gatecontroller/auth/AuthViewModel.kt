package com.cm.gatecontroller.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<AuthSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            AuthIntent.CheckCurrentUser -> checkCurrentUser()
            is AuthIntent.EmailChanged -> _uiState.update { it.copy(email = intent.email) }
            is AuthIntent.PasswordChanged -> _uiState.update { it.copy(password = intent.password) }
            AuthIntent.SubmitLogin -> login()
            AuthIntent.SubmitSignUp -> signUp()
            AuthIntent.ResetPassword -> resetPassword()
        }
    }

    private fun checkCurrentUser() {
        if (authRepository.getCurrentUser() != null) {
            viewModelScope.launch {
                _sideEffect.send(AuthSideEffect.NavigateToMain)
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.login(uiState.value.email, uiState.value.password)
            result.onSuccess {
                _sideEffect.send(AuthSideEffect.NavigateToMain)
            }.onFailure {
                _sideEffect.send(AuthSideEffect.ShowSnackbar(it.message ?: "Login failed"))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.signUp(uiState.value.email, uiState.value.password)
            result.onSuccess {
                _sideEffect.send(AuthSideEffect.ShowSnackbar("Sign up successful! Please log in."))
            }.onFailure {
                _sideEffect.send(AuthSideEffect.ShowSnackbar(it.message ?: "Sign up failed"))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun resetPassword() {
        if (uiState.value.email.isBlank()) {
            viewModelScope.launch { _sideEffect.send(AuthSideEffect.ShowSnackbar("Please enter your email address.")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.sendPasswordResetEmail(uiState.value.email)
            result.onSuccess {
                _sideEffect.send(AuthSideEffect.ShowSnackbar("Password reset email sent."))
            }.onFailure {
                _sideEffect.send(
                    AuthSideEffect.ShowSnackbar(
                        it.message ?: "Failed to send reset email."
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
