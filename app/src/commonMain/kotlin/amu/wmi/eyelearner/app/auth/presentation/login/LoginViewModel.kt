package amu.wmi.eyelearner.app.auth.presentation.login

import amu.wmi.eyelearner.app.auth.domain.usecase.FinalizeLoginUseCase
import amu.wmi.eyelearner.app.auth.domain.usecase.InitLoginUseCase
import amu.wmi.eyelearner.app.auth.domain.usecase.ObserveSessionUseCase
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val authorizeUrl: String? = null,
    val isLoginSuccessful: Boolean = false
)

sealed interface LoginIntent {
    data class Login(val callbackUrl: String = "") : LoginIntent
    data class Finalize(val oauthToken: String, val oauthVerifier: String) : LoginIntent
    data object DismissError : LoginIntent
}

class LoginViewModel(
    private val initLoginUseCase: InitLoginUseCase,
    private val finalizeLoginUseCase: FinalizeLoginUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val onOpenUrl: (String) -> Unit = {}
) : ViewModel() {
    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessionUseCase().collect { session ->
                _state.update { it.copy(isLoginSuccessful = session != null) }
            }
        }
    }

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> viewModelScope.launch { initLogin(intent.callbackUrl) }
            is LoginIntent.Finalize -> viewModelScope.launch { finalizeLogin(intent.oauthToken, intent.oauthVerifier) }
            is LoginIntent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    suspend fun initLogin(callbackUrl: String = "") {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val url = initLoginUseCase(callbackUrl)
            _state.update { it.copy(isLoading = false, authorizeUrl = url) }
            onOpenUrl(url)
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to initiate login") }
        }
    }

    suspend fun finalizeLogin(oauthToken: String, oauthVerifier: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            when (val response = finalizeLoginUseCase(oauthToken, oauthVerifier)) {
                is AuthSessionResponse.Success -> {
                    _state.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                }
                is AuthSessionResponse.InvalidOrExpiredToken -> {
                    _state.update { it.copy(isLoading = false, error = "Invalid or expired token") }
                }
                is AuthSessionResponse.Error -> {
                    _state.update { it.copy(isLoading = false, error = response.message) }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to finalize login") }
        }
    }
}