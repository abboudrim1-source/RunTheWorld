package com.runtheworld.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.data.network.LebanonGovernorates
import com.runtheworld.domain.model.LeaderboardEntry
import com.runtheworld.domain.repository.LeaderboardRepository
import com.runtheworld.domain.repository.UserProfileRepository
import com.runtheworld.util.onError
import com.runtheworld.util.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderboardState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val cities: List<String> = LebanonGovernorates,
    val selectedCity: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardState())
    val state: StateFlow<LeaderboardState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val userCity = userProfileRepository.getProfile()?.city?.takeIf { city -> city in LebanonGovernorates }
            val selectedCity = _state.value.selectedCity?.takeIf { city -> city in LebanonGovernorates } ?: userCity
            _state.update { it.copy(cities = LebanonGovernorates, selectedCity = selectedCity) }
            fetchLeaderboard(selectedCity)
        }
    }

    fun selectCity(city: String?) {
        val normalizedCity = city?.takeIf { it in LebanonGovernorates }
        _state.update { it.copy(selectedCity = normalizedCity) }
        viewModelScope.launch { fetchLeaderboard(normalizedCity) }
    }

    private suspend fun fetchLeaderboard(city: String?) {
        _state.update { it.copy(isLoading = true, error = null) }
        leaderboardRepository.getLeaderboard(city)
            .onSuccess { entries ->
                val filtered = entries.filter { entry ->
                    city == null || entry.city == city
                }
                _state.update { it.copy(entries = filtered, isLoading = false) }
            }
            .onError { err ->
                _state.update { it.copy(isLoading = false, error = err) }
            }
    }
}
