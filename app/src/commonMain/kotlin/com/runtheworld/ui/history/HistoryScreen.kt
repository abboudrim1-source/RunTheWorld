package com.runtheworld.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.domain.model.Run
import com.runtheworld.presentation.history.HistoryViewModel
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                GradientText(
                    text = "MY RUNS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Stats card
            state.profile?.let { profile ->
                val accentColor = parseHexColor(profile.colorHex)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .colorGlow(accentColor, elevation = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.35f),
                                    accentColor.copy(alpha = 0.12f)
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                profile.displayName.ifBlank { profile.username },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Text(
                                "@${profile.username}",
                                style = MaterialTheme.typography.bodySmall,
                                color = accentColor.copy(alpha = 0.9f)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "${profile.runCount} runs · ${formatArea3f(profile.totalAreaKm2)} km² claimed",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp)
                    }
                }
                state.runs.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = NeonOrange.copy(alpha = 0.3f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No runs yet.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                "Head to the map and start running!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.35f)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.runs, key = { it.id }) { run ->
                            RunCard(
                                run = run,
                                isDeleting = state.deletingId == run.id,
                                onDelete = { viewModel.deleteRun(run.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RunCard(run: Run, isDeleting: Boolean, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x22FFFFFF))
    ) {
        // Accent left bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(BrandGradientVertical)
        )
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 4.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatEpochMillis(run.startedAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonOrange,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    RunStat(icon = Icons.Default.Straighten, value = run.distanceFormatted)
                    RunStat(icon = Icons.Default.Timer, value = run.durationFormatted)
                    RunStat(icon = Icons.Default.Map, value = "${formatArea4f(run.areaKm2)} km²")
                }
            }

            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = NeonOrange
                )
            } else {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RunStat(icon: ImageVector, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(13.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.75f)
        )
    }
}

private fun formatArea3f(value: Double): String {
    val v = kotlin.math.round(value * 1000).toLong()
    return "${v / 1000}.${(v % 1000).toString().padStart(3, '0')}"
}

private fun formatArea4f(value: Double): String {
    val v = kotlin.math.round(value * 10000).toLong()
    return "${v / 10000}.${(v % 10000).toString().padStart(4, '0')}"
}

private fun formatEpochMillis(epochMillis: Long): String {
    val totalSeconds = epochMillis / 1000
    val secondsInDay = ((totalSeconds % 86400) + 86400) % 86400
    var remaining = (totalSeconds - secondsInDay) / 86400
    val hours = secondsInDay / 3600
    val minutes = (secondsInDay % 3600) / 60

    var year = 1970
    while (true) {
        val daysInYear = if (isLeapYear(year)) 366 else 365
        if (remaining < daysInYear) break
        remaining -= daysInYear
        year++
    }

    val daysPerMonth = intArrayOf(31, if (isLeapYear(year)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var month = 1
    for (d in daysPerMonth) {
        if (remaining < d) break
        remaining -= d
        month++
    }
    val day = remaining + 1

    return "${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/$year " +
            "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}

private fun isLeapYear(year: Int) = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
