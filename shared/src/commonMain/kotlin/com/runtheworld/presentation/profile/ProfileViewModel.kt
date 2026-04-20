package com.runtheworld.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.model.UserProfile
import com.runtheworld.domain.repository.TerritoryRepository
import com.runtheworld.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val username: String = "",
    val displayName: String = "",
    val selectedColorHex: String = "#FF5733",
    val totalAreaKm2: Double = 0.0,
    val runCount: Int = 0,
    val avatarBase64: String? = null,
    val isSaved: Boolean = false,
    val error: String? = null
)

val AVATAR_COLORS = listOf(
    "#FF5733", "#33AFFF", "#75FF33", "#FF33A8",
    "#FFC733", "#A833FF", "#33FFD7", "#FF8C33"
)

class ProfileViewModel(
    private val userProfileRepository: UserProfileRepository,
    private val territoryRepository: TerritoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    val isProfileSetUp: Boolean get() = userProfileRepository.isProfileSetUp()

    fun onAvatarSelected(base64: String) {
        _state.update { it.copy(avatarBase64 = base64) }
    }

    fun onUsernameChange(value: String) {
        _state.update { it.copy(username = value, error = null) }
    }

    fun onDisplayNameChange(value: String) {
        _state.update { it.copy(displayName = value, error = null) }
    }

    fun onColorSelect(colorHex: String) {
        _state.update { it.copy(selectedColorHex = colorHex) }
    }

    fun saveProfile() {
        val username = _state.value.username.trim()
        if (username.isBlank()) {
            _state.update { it.copy(error = "Runner name cannot be empty") }
            return
        }
        viewModelScope.launch {
            try {
                val profile = userProfileRepository.getProfile()
                userProfileRepository.saveProfile(
                    UserProfile(
                        username     = username,
                        displayName  = _state.value.displayName.trim(),
                        colorHex     = _state.value.selectedColorHex,
                        totalAreaKm2 = profile?.totalAreaKm2 ?: 0.0,
                        runCount     = profile?.runCount ?: 0,
                        avatarBase64 = _state.value.avatarBase64
                    )
                )
                territoryRepository.updateColorForOwner(username, _state.value.selectedColorHex)
                _state.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to save profile") }
            }
        }
    }

    suspend fun tryRestoreFromServer(uid: String): Boolean {
        val profile = userProfileRepository.fetchFromServer(uid) ?: return false
        userProfileRepository.saveProfile(profile)
        loadExistingProfile()
        return true
    }

    fun logout() {
        _state.update { ProfileState() }
    }

    fun loadExistingProfile() {
        val profile = userProfileRepository.getProfile() ?: return
        _state.update {
            it.copy(
                username         = profile.username,
                displayName      = profile.displayName,
                selectedColorHex = profile.colorHex,
                totalAreaKm2     = profile.totalAreaKm2,
                runCount         = profile.runCount,
                avatarBase64     = profile.avatarBase64,
                isSaved          = false
            )
        }
    }

    fun resetSaved() {
        _state.update { it.copy(isSaved = false) }
    }
}
