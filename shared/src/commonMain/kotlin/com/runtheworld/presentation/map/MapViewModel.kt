package com.runtheworld.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.model.Territory
import com.runtheworld.domain.repository.TerritoryRepository
import com.runtheworld.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class MapState(
    val territories: List<Territory> = emptyList(),
    val currentUsername: String? = null,
    val isLoading: Boolean = true
)

class MapViewModel(
    private val territoryRepository: TerritoryRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    init {
        val profile = userProfileRepository.getProfile()
        _state.update { it.copy(currentUsername = profile?.username) }

        territoryRepository.observeTerritories()
            .onEach { territories ->
                _state.update { it.copy(territories = territories, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }
}
