package com.runtheworld.presentation.run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Run
import com.runtheworld.domain.repository.RunRepository
import com.runtheworld.domain.repository.TerritoryRepository
import com.runtheworld.domain.repository.UserProfileRepository
import com.runtheworld.domain.model.Territory
import com.runtheworld.platform.LocationService
import com.runtheworld.util.ConvexHull
import com.runtheworld.util.DistanceCalculator
import com.runtheworld.util.onError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class RunStatus { IDLE, RUNNING, SAVING, DONE, ERROR }

data class RunState(
    val status: RunStatus = RunStatus.IDLE,
    val currentPath: List<GpsPoint> = emptyList(),
    val distanceMeters: Double = 0.0,
    val elapsedSeconds: Long = 0L,
    val startedAt: Long = 0L,
    val error: String? = null,
    val lastSavedRunId: String? = null,
    val userLocation: GpsPoint? = null
)

@OptIn(ExperimentalUuidApi::class)
class RunViewModel(
    private val locationService: LocationService,
    private val runRepository: RunRepository,
    private val territoryRepository: TerritoryRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RunState())
    val state: StateFlow<RunState> = _state.asStateFlow()

    private var trackingJob: Job? = null
    private var timerJob: Job? = null

    fun startRun() {
        if (_state.value.status == RunStatus.RUNNING) return
        val now = currentTimeMillis()
        _state.update { RunState(status = RunStatus.RUNNING, startedAt = now) }

        // GPS tracking — only append a point if the user moved at least 5 m
        trackingJob = viewModelScope.launch {
            locationService.locationUpdates().collect { point ->
                _state.update { s ->
                    val last = s.currentPath.lastOrNull()
                    val moved = last == null ||
                        DistanceCalculator.distanceBetween(last, point) >= 5.0
                    if (!moved) return@update s.copy(userLocation = point)
                    val newPath = s.currentPath + point
                    s.copy(
                        currentPath = newPath,
                        distanceMeters = DistanceCalculator.totalDistance(newPath),
                        userLocation = point
                    )
                }
            }
        }

        // Elapsed timer — updates every second
        timerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1_000)
                _state.update { it.copy(elapsedSeconds = (currentTimeMillis() - it.startedAt) / 1000) }
            }
        }
    }

    fun stopRun() {
        trackingJob?.cancel()
        timerJob?.cancel()

        val s = _state.value
        if (s.currentPath.size < 3) {
            _state.update { it.copy(status = RunStatus.ERROR, error = "Not enough GPS points to claim territory (need at least 3).") }
            return
        }
        _state.update { it.copy(status = RunStatus.SAVING) }
        viewModelScope.launch { persistRun(s) }
    }

    private suspend fun persistRun(s: RunState) {
        val profile = userProfileRepository.getProfile()
        val polygon = ConvexHull.compute(s.currentPath)
        val areaKm2 = DistanceCalculator.polygonAreaKm2(polygon)
        val runId = Uuid.random().toString()
        val now = currentTimeMillis()

        val run = Run(
            id = runId,
            startedAt = s.startedAt,
            endedAt = now,
            distanceMeters = s.distanceMeters,
            areaKm2 = areaKm2,
            path = s.currentPath,
            claimedPolygon = polygon
        )

        runRepository.saveRun(run).onError { err ->
            _state.update { it.copy(status = RunStatus.ERROR, error = err) }
            return
        }

        if (profile != null) {
            val territory = Territory(
                id = runId,
                ownerUsername = profile.username,
                ownerColorHex = profile.colorHex,
                polygon = polygon,
                claimedAt = now,
                areaKm2 = areaKm2
            )
            territoryRepository.claimTerritory(territory)
            userProfileRepository.updateStats(areaKm2)
        }

        _state.update { it.copy(status = RunStatus.DONE, lastSavedRunId = runId) }
    }

    fun resetToIdle() {
        _state.update { RunState() }
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
        timerJob?.cancel()
    }
}

expect fun currentTimeMillis(): Long
