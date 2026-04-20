package com.runtheworld.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSignUpMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val passwordErrors: List<String> = emptyList(),
    val success: Boolean = false
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    val isSignedIn: Boolean get() = authRepository.getCurrentUser() != null
    val currentUid: String? get() = authRepository.getCurrentUser()?.uid

    suspend fun validateAndGetStartDestination(): Boolean {
        if (!isSignedIn) return false
        val valid = authRepository.validateSession()
        if (!valid) authRepository.signOut()
        return valid
    }

    fun setMode(signUp: Boolean) {
        _state.update { AuthState(isSignUpMode = signUp) }
    }

    fun onEmailChange(v: String)           = _state.update { it.copy(email = v, error = null) }
    fun onPasswordChange(v: String)        = _state.update { it.copy(password = v, error = null, passwordErrors = if (it.isSignUpMode) validatePassword(v) else emptyList()) }
    fun onConfirmPasswordChange(v: String) = _state.update { it.copy(confirmPassword = v, error = null) }
    fun toggleMode()                       = _state.update { AuthState(isSignUpMode = !it.isSignUpMode) }

    fun submit() {
        val s = _state.value
        if (s.isSignUpMode) signUp(s.email.trim(), s.password, s.confirmPassword)
        else signIn(s.email.trim(), s.password)
    }

    fun signInWithGoogle(googleId: String, email: String, displayName: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signInWithGoogle(googleId, email, displayName)
                _state.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Google sign-in failed") }
            }
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            _state.update { AuthState() }
            onDone()
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signInWithEmail(email, password)
                _state.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Sign-in failed") }
            }
        }
    }

    private fun signUp(email: String, password: String, confirmPassword: String) {
        val errors = validatePassword(password)
        if (errors.isNotEmpty()) { _state.update { it.copy(passwordErrors = errors) }; return }
        if (password != confirmPassword) { _state.update { it.copy(error = "Passwords do not match") }; return }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signUpWithEmail(email, password)
                _state.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Sign-up failed") }
            }
        }
    }

    private fun validatePassword(password: String): List<String> = buildList {
        if (password.length < 8)                      add("At least 8 characters")
        if (!password.any { it.isUpperCase() })        add("At least 1 uppercase letter")
        if (!password.any { it.isLowerCase() })        add("At least 1 lowercase letter")
        if (!password.any { it.isDigit() })            add("At least 1 number")
        if (!password.any { !it.isLetterOrDigit() })   add("At least 1 special character (!@#\$%^&*)")
    }
}
