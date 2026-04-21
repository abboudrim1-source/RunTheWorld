package com.runtheworld.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                    text = "LEADERBOARD",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            val allCities = listOf<String?>(null) + state.cities
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allCities.forEach { city ->
                    val selected = state.selectedCity == city
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (selected) NeonOrange else Color.White.copy(alpha = 0.08f))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { viewModel.selectCity(city) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = city ?: "Global",
                            color = if (selected) Color.Black else Color.White.copy(alpha = 0.7f),
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

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
                                state.error.orEmpty(),
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
                        Text(
                            if (state.selectedCity != null)
                                "No runners in ${state.selectedCity} yet"
                            else
                                "No runners yet",
                            color = Color.White.copy(alpha = 0.45f)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.entries) { entry ->
                            LeaderboardRow(entry = entry, showCity = state.selectedCity == null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry, showCity: Boolean) {
    val rankColor = when (entry.rank) {
        1    -> Color(0xFFFFD700)
        2    -> Color(0xFFC0C0C0)
        3    -> Color(0xFFCD7F32)
        else -> Color.White.copy(alpha = 0.5f)
    }
    val dotColor = entry.colorHex.toComposeColor()
    val scoreText = "${entry.totalScore} pts"

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
        Box(modifier = Modifier.size(10.dp).background(dotColor, CircleShape))
        Column(modifier = Modifier.weight(1f)) {
            Text(entry.username, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            val city = entry.city
            if (showCity && city != null) {
                Text(city, color = Color.White.copy(alpha = 0.45f), style = MaterialTheme.typography.labelSmall)
            }
        }
        Text(scoreText, color = NeonOrange, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun String.toComposeColor(): Color = try {
    val hex = removePrefix("#")
    Color((0xFF000000 or hex.toLong(16)))
} catch (_: Exception) { NeonOrange }
