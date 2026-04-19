package com.runtheworld.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Territory
import com.runtheworld.domain.repository.RunRepository
import com.runtheworld.domain.repository.TerritoryRepository
import com.runtheworld.domain.repository.UserProfileRepository
import com.runtheworld.platform.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapState(
    val territories: List<Territory> = emptyList(),
    val runPaths: List<List<GpsPoint>> = emptyList(),
    val currentUsername: String? = null,
    val isLoading: Boolean = true,
    val userLocation: GpsPoint? = null
)

class MapViewModel(
    private val territoryRepository: TerritoryRepository,
    private val userProfileRepository: UserProfileRepository,
    private val locationService: LocationService,
    private val runRepository: RunRepository
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

        runRepository.observeRuns()
            .onEach { runs ->
                _state.update { it.copy(runPaths = runs.map { r -> r.path }) }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            try {
                locationService.locationUpdates().collect { point ->
                    _state.update { it.copy(userLocation = point) }
                }
            } catch (_: SecurityException) {
                // Location permission not yet granted — camera stays at default
            }
        }
    }
}
