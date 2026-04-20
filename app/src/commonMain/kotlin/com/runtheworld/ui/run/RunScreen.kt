package com.runtheworld.ui.run

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.presentation.run.RunState
import com.runtheworld.presentation.run.RunStatus
import com.runtheworld.presentation.run.RunViewModel
import com.runtheworld.presentation.run.SyncStatus
import com.runtheworld.ui.map.RunTheWorldMap
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RunScreen(
    onRunFinished: () -> Unit,
    onBack: () -> Unit,
    viewModel: RunViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Permission state
    var permissionGranted by remember { mutableStateOf(false) }
    var permissionDenied  by remember { mutableStateOf(false) }

    // Request permission on first composition
    LocationPermissionEffect(
        onGranted = { permissionGranted = true },
        onDenied  = { permissionDenied  = true }
    )

    // Start tracking only once permission is confirmed
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) viewModel.startRun()
    }

    LaunchedEffect(state.status, state.syncStatus) {
        if (state.status == RunStatus.DONE &&
            (state.syncStatus == SyncStatus.SYNCED || state.syncStatus == SyncStatus.FAILED)
        ) {
            if (state.syncStatus == SyncStatus.FAILED) {
                kotlinx.coroutines.delay(2_000)
            }
            onRunFinished()
            viewModel.resetToIdle()
        }
    }

    // ── Permission denied screen ─────────────────────────────────────────────
    if (permissionDenied) {
        AppBackground {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.LocationOff,
                    contentDescription = null,
                    tint = NeonOrange.copy(alpha = 0.7f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.height(24.dp))
                GradientText(
                    text = "Location needed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Run The World uses your GPS to track your runs and claim territory. " +
                           "Please enable location in Settings → App Permissions.",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(40.dp))
                GlassButton(
                    text = "Go back",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        return
    }

    // ── Loading / waiting for permission ────────────────────────────────────
    if (!permissionGranted) {
        AppBackground {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp)
                    Spacer(Modifier.height(20.dp))
                    Text("Checking location permission…",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        return
    }

    // ── Main run UI ──────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {
        RunTheWorldMap(
            modifier = Modifier.fillMaxSize(),
            territories = emptyList(),
            currentUserUsername = null,
            currentPath = state.currentPath,
            userLocation = state.userLocation,
            showMyLocationButton = true
        )

        // Back button (only when not actively running)
        if (state.status != RunStatus.RUNNING) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(12.dp)
                    .size(44.dp)
                    .mapGlassSurface(CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Stats overlay — top center (glass pill)
        RunStatsOverlay(
            state = state,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp)
        )

        // Bottom controls
        when (state.status) {
            RunStatus.RUNNING -> {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 36.dp)
                        .size(76.dp)
                        .colorGlow(Color(0xFFFF4466), elevation = 24.dp)
                        .clip(CircleShape)
                        .background(
                            androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(Color(0xFFFF4466), Color(0xFFCC0033))
                            )
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = viewModel::stopRun
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Stop, "Stop run", tint = Color.White, modifier = Modifier.size(38.dp))
                }
            }

            RunStatus.SAVING -> {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp)
                }
            }

            RunStatus.DONE -> {
                when (state.syncStatus) {
                    SyncStatus.SYNCING -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(bottom = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Syncing with server\u2026",
                                    color = Color.White.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    SyncStatus.FAILED -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .mapGlassSurface(RoundedCornerShape(14.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                state.syncMessage ?: "Server sync failed",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    else -> Unit
                }
            }

            RunStatus.ERROR -> {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .mapGlassSurface(RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Text(state.error ?: "An error occurred.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun RunStatsOverlay(state: RunState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .mapGlassSurface(RoundedCornerShape(20.dp))
            .padding(horizontal = 28.dp, vertical = 14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            StatItem("DISTANCE", formatDistance(state.distanceMeters))
            StatItem("TIME",     formatDuration(state.elapsedSeconds))
            StatItem("POINTS",   state.currentPath.size.toString())
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall, color = NeonOrange.copy(alpha = 0.8f), letterSpacing = 1.sp)
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
    val m = seconds / 60; val s = seconds % 60
    return "${m}:${s.toString().padStart(2, '0')}"
}
