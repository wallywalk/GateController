package com.cm.gatecontroller.auth

sealed interface AuthIntent {
    data class EmailChanged(val email: String) : AuthIntent
    data class PasswordChanged(val password: String) : AuthIntent
    data object SubmitLogin : AuthIntent
    data object SubmitSignUp : AuthIntent
    data object ResetPassword : AuthIntent
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)

sealed interface AuthSideEffect {
    data class ShowSnackbar(val message: String) : AuthSideEffect
    data object NavigateToMonitoring : AuthSideEffect
}
