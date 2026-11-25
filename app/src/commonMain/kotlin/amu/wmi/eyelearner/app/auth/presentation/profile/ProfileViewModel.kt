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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ProfileScreenState(
    val session: Session? = null,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,

    val isEditing: Boolean = false,
    val editedUsername: String = "User",
    val editedAvatarUri: String? = null,   // was Uri?
    val isSaving: Boolean = false
)

sealed interface ProfileIntent {
    data object Logout : ProfileIntent

    data object StartEditing : ProfileIntent
    data object CancelEditing : ProfileIntent
    data class UpdateUsername(val name: String) : ProfileIntent
    data class UpdateAvatar(val uri: String) : ProfileIntent   // was Uri
    data object SaveProfile : ProfileIntent
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

            is ProfileIntent.UpdateUsername -> {
                _state.update { it.copy(editedUsername = intent.name) }
            }
            is ProfileIntent.UpdateAvatar -> {
                _state.update { it.copy(editedAvatarUri = intent.uri) }
            }
            is ProfileIntent.SaveProfile -> {
                viewModelScope.launch {
                    _state.update { it.copy(isSaving = true) }
                    try {
                        // 1. If avatar is local URI, upload to cloud first and get URL
                        // 2. Save username and avatar URL to Repository/DataStore
                        // 3. Update state and exit edit mode
                        _state.update { it.copy(isSaving = false, isEditing = false) }
                    } catch (e: Exception) {
                        _state.update { it.copy(isSaving = false) }
                    }
                }
            }

            is ProfileIntent.StartEditing -> {
                _state.update { it.copy(isEditing = true) }
            }
            is ProfileIntent.CancelEditing -> {
                _state.update {
                    it.copy(
                        isEditing = false,
                        editedUsername = it.session?.username ?: "User",
                        editedAvatarUri = null
                    )
                }
            }
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