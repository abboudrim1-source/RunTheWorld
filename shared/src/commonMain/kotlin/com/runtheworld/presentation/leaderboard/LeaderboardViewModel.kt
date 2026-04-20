package com.runtheworld.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.model.LeaderboardEntry
import com.runtheworld.domain.repository.LeaderboardRepository
import com.runtheworld.util.onError
import com.runtheworld.util.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderboardState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardState())
    val state: StateFlow<LeaderboardState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            leaderboardRepository.getLeaderboard()
                .onSuccess { entries ->
                    _state.update { it.copy(entries = entries, isLoading = false) }
                }
                .onError { err ->
                    _state.update { it.copy(isLoading = false, error = err) }
                }
        }
    }
}
