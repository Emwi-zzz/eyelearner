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
    val isLoggedOut: Boolean = false,

    // Edit mode state
    val isEditing: Boolean = false,
    val editedUsername: String = "",
    val editedAvatarUri: String? = null,
    val isSaving: Boolean = false,

    // Messages
    val errorMessage: String? = null,
    val savedMessage: String? = null
)

sealed interface ProfileIntent {
    data object Logout : ProfileIntent

    data object StartEditing : ProfileIntent
    data object CancelEditing : ProfileIntent
    data class UpdateUsername(val name: String) : ProfileIntent
    data class UpdateAvatar(val uri: String) : ProfileIntent
    data object SaveProfile : ProfileIntent
    data object DismissMessage : ProfileIntent
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
                _state.update { current ->
                    if (!current.isEditing) {
                        current.copy(
                            session = session,
                            editedUsername = session?.username ?: "",
                            editedAvatarUri = session?.avatarUrl
                        )
                    } else {
                        current.copy(session = session)
                    }
                }
            }
        }
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Logout -> viewModelScope.launch { logout() }

            ProfileIntent.StartEditing -> _state.update { current ->
                current.copy(
                    isEditing = true,
                    // Re-seed strictly from the session, not stale edits
                    editedUsername = current.session?.username ?: "",
                    editedAvatarUri = current.session?.avatarUrl,
                    errorMessage = null,
                    savedMessage = null
                )
            }

            ProfileIntent.CancelEditing -> _state.update { current ->
                current.copy(
                    isEditing = false,
                    editedUsername = current.session?.username ?: "",
                    editedAvatarUri = current.session?.avatarUrl,
                    errorMessage = null
                )
            }

            is ProfileIntent.UpdateUsername -> _state.update {
                it.copy(editedUsername = intent.name, errorMessage = null)
            }

            is ProfileIntent.UpdateAvatar -> _state.update {
                it.copy(editedAvatarUri = intent.uri, errorMessage = null)
            }

            ProfileIntent.SaveProfile -> viewModelScope.launch { saveProfile() }

            ProfileIntent.DismissMessage -> _state.update {
                it.copy(errorMessage = null, savedMessage = null)
            }
        }
    }

    private suspend fun saveProfile() {
        val current = _state.value
        val username = current.editedUsername.trim()

        if (username.isBlank()) {
            _state.update { it.copy(errorMessage = "Username can't be empty") }
            return
        }
        if (username.length < 3) {
            _state.update { it.copy(errorMessage = "Username must be at least 3 characters") }
            return
        }

        _state.update { it.copy(isSaving = true, errorMessage = null) }
        try {
            // TODO: Replace with UpdateProfileUseCase(username, current.editedAvatarUri)
            // updateProfileUseCase(username, current.editedAvatarUri)

            _state.update {
                it.copy(
                    isSaving = false,
                    isEditing = false,
                    // Optimistically reflect the change if no use case is wired yet
                    session = it.session?.copy(
                        username = username,
                        avatarUrl = current.editedAvatarUri ?: ""
                    ),
                    savedMessage = "Profile updated successfully"
                )
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Failed to save profile"
                )
            }
        }
    }

    private suspend fun logout() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            logoutUseCase()
            _state.update { it.copy(isLoading = false, isLoggedOut = true) }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Logout failed"
                )
            }
        }
    }
}