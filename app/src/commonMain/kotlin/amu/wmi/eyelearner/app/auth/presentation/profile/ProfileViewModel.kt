package amu.wmi.eyelearner.app.auth.presentation.profile

import amu.wmi.eyelearner.app.auth.domain.entities.Session
import amu.wmi.eyelearner.app.auth.domain.usecase.LogoutUseCase
import amu.wmi.eyelearner.app.auth.domain.usecase.ObserveSessionUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val session: Session? = null,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false
)

sealed interface ProfileIntent {
    data object Logout : ProfileIntent
}

class ProfileViewModel(
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessionUseCase().collect { session ->
                _state.update { it.copy(session = session) }
            }
        }
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.Logout -> viewModelScope.launch { logout() }
        }
    }

    private suspend fun logout() {
        _state.update { it.copy(isLoading = true) }
        try {
            logoutUseCase()
        } finally {
            _state.update { it.copy(isLoading = false, isLoggedOut = true) }
        }
    }
}
