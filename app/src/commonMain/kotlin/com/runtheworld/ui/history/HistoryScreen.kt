package com.runtheworld.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.domain.model.Run
import com.runtheworld.presentation.history.HistoryViewModel
import com.runtheworld.ui.theme.parseHexColor
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Runs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Profile stats card
            state.profile?.let { profile ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = parseHexColor(profile.colorHex).copy(alpha = 0.15f),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                profile.username,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                "${profile.runCount} runs · ${formatArea3f(profile.totalAreaKm2)} km² claimed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = parseHexColor(profile.colorHex),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.runs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No runs yet.", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Head to the map and start running!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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

@Composable
private fun RunCard(run: Run, isDeleting: Boolean, onDelete: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    formatEpochMillis(run.startedAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatPill("📍 ${run.distanceFormatted}")
                    StatPill("⏱ ${run.durationFormatted}")
                    StatPill("🏳 ${formatArea4f(run.areaKm2)} km²")
                }
            }

            if (isDeleting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun StatPill(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    )
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
