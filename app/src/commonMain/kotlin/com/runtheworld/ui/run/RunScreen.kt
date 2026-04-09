package com.runtheworld.ui.run

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.presentation.run.RunState
import com.runtheworld.presentation.run.RunStatus
import com.runtheworld.presentation.run.RunViewModel
import com.runtheworld.ui.map.RunTheWorldMap
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunScreen(
    onRunFinished: () -> Unit,
    onBack: () -> Unit,
    viewModel: RunViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.status) {
        if (state.status == RunStatus.DONE) {
            onRunFinished()
            viewModel.resetToIdle()
        }
    }

    // Auto-start tracking when screen opens
    LaunchedEffect(Unit) {
        viewModel.startRun()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Running") },
                navigationIcon = {
                    if (state.status != RunStatus.RUNNING) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Live map with current path
            RunTheWorldMap(
                modifier = Modifier.fillMaxSize(),
                territories = emptyList(),
                currentUserUsername = null,
                currentPath = state.currentPath,
                userLocation = state.currentPath.lastOrNull()
            )

            // Stats overlay at the top
            RunStatsOverlay(
                state = state,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )

            // Stop button + status
            when (state.status) {
                RunStatus.RUNNING -> {
                    FloatingActionButton(
                        onClick = viewModel::stopRun,
                        containerColor = MaterialTheme.colorScheme.error,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(32.dp)
                            .size(72.dp)
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop run",
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }

                RunStatus.SAVING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)
                    )
                }

                RunStatus.ERROR -> {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = state.error ?: "An error occurred.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun RunStatsOverlay(state: RunState, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            StatItem(
                label = "Distance",
                value = formatDistance(state.distanceMeters)
            )
            StatItem(
                label = "Time",
                value = formatDuration(state.elapsedSeconds)
            )
            StatItem(
                label = "Points",
                value = state.currentPath.size.toString()
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

private fun formatDistance(meters: Double): String =
    if (meters >= 1000) {
        val v = kotlin.math.round(meters / 1000.0 * 100).toLong()
        "${v / 100}.${(v % 100).toString().padStart(2, '0')} km"
    } else {
        "${kotlin.math.round(meters).toLong()} m"
    }

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m}:${s.toString().padStart(2, '0')}"
}
