package com.runtheworld.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.runtheworld.domain.model.LeaderboardEntry
import com.runtheworld.presentation.leaderboard.LeaderboardViewModel
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.load()
        }
    }

    AppBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                GradientText(
                    text = "Leaderboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp)
                    }
                }

                state.error != null -> {
                    Box(
                        Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Could not reach server",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                state.error ?: "",
                                color = Color.White.copy(alpha = 0.45f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(24.dp))
                            GlassButton(
                                text = "Retry",
                                onClick = viewModel::load,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                state.entries.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No runners yet.", color = Color.White.copy(alpha = 0.45f))
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.entries) { entry ->
                            LeaderboardRow(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    val rankColor = when (entry.rank) {
        1    -> Color(0xFFFFD700L) // gold
        2    -> Color(0xFFC0C0C0L) // silver
        3    -> Color(0xFFCD7F32L) // bronze
        else -> Color.White.copy(alpha = 0.5f)
    }
    val dotColor = entry.colorHex.toComposeColor()
    val areaParts = (entry.totalAreaKm2 * 100).toLong().let { v ->
        "${v / 100}.${(v % 100).toString().padStart(2, '0')} km\u00B2"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .mapGlassSurface(RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "#${entry.rank}",
            color = rankColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            modifier = Modifier.width(40.dp)
        )
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(dotColor, CircleShape)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.username,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${entry.runCount} run${if (entry.runCount != 1) "s" else ""}",
                color = Color.White.copy(alpha = 0.45f),
                style = MaterialTheme.typography.labelSmall
            )
        }
        Text(
            text = areaParts,
            color = NeonOrange,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun String.toComposeColor(): Color = try {
    val hex = removePrefix("#")
    val argbHex = if (hex.length == 6) "FF$hex" else hex
    Color(argbHex.toLong(16))
} catch (_: Exception) {
    NeonOrange
}
