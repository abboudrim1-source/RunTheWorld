package com.runtheworld.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.model.Run
import com.runtheworld.domain.model.UserProfile
import com.runtheworld.domain.repository.RunRepository
import com.runtheworld.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryState(
    val runs: List<Run> = emptyList(),
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val deletingId: String? = null
)

class HistoryViewModel(
    private val runRepository: RunRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        _state.update { it.copy(profile = userProfileRepository.getProfile()) }

        runRepository.observeRuns()
            .onEach { runs ->
                _state.update { it.copy(runs = runs, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun deleteRun(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(deletingId = id) }
            runRepository.deleteRun(id)
            _state.update { it.copy(deletingId = null) }
        }
    }
}
