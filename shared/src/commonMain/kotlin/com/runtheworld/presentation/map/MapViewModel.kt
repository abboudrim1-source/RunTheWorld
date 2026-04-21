package com.runtheworld.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Run
import com.runtheworld.domain.model.Territory
import com.runtheworld.domain.repository.FriendRepository
import com.runtheworld.domain.repository.RemoteTerritoryRepository
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
    val friendTerritories: List<Territory> = emptyList(),
    val runs: List<Run> = emptyList(),
    val currentUsername: String? = null,
    val userColorHex: String = "#1A73E8",
    val isLoading: Boolean = true,
    val userLocation: GpsPoint? = null
)

class MapViewModel(
    private val territoryRepository: TerritoryRepository,
    private val userProfileRepository: UserProfileRepository,
    private val locationService: LocationService,
    private val runRepository: RunRepository,
    private val friendRepository: FriendRepository,
    private val remoteTerritoryRepository: RemoteTerritoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    private suspend fun restoreOwnTerritoriesIfEmpty() {
        try {
            val uid = userProfileRepository.getCurrentUid() ?: return
            val serverTerritories = remoteTerritoryRepository.fetchForUsers(setOf(uid))
            serverTerritories.forEach { territoryRepository.claimTerritory(it) }
        } catch (_: Exception) {}
    }

    fun loadFriendTerritories() {
        viewModelScope.launch {
            try {
                val friendUids = friendRepository.getFriendUids()
                val territories = remoteTerritoryRepository.fetchForUsers(friendUids)
                _state.update { it.copy(friendTerritories = territories) }
            } catch (_: Exception) {}
        }
    }

    init {
        val profile = userProfileRepository.getProfile()
        _state.update { it.copy(
            currentUsername = profile?.username,
            userColorHex = profile?.colorHex ?: "#1A73E8"
        ) }

        viewModelScope.launch {
            try { userProfileRepository.syncToServer() } catch (_: Exception) {}
        }

        viewModelScope.launch { loadFriendTerritories() }
        viewModelScope.launch { restoreOwnTerritoriesIfEmpty() }

        territoryRepository.observeTerritories()
            .onEach { territories ->
                _state.update { it.copy(territories = territories, isLoading = false) }
            }
            .launchIn(viewModelScope)

        runRepository.observeRuns()
            .onEach { runs ->
                _state.update { it.copy(runs = runs) }
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
