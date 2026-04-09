package com.runtheworld.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.runtheworld.presentation.map.MapViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(
    onStartRun: () -> Unit,
    onHistory: () -> Unit,
    onLogout: () -> Unit,
    viewModel: MapViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        RunTheWorldMap(
            modifier = Modifier.fillMaxSize(),
            territories = state.territories,
            currentUserUsername = state.currentUsername,
            currentPath = emptyList(),
            userLocation = null
        )

        // Logout button — top right
        IconButton(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                Icons.Default.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Top overlay — territory count
        if (!state.isLoading) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Text(
                    text = "${state.territories.size} territories claimed",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Bottom action row
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // History button
            FilledTonalIconButton(
                onClick = onHistory,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = "History")
            }

            // Start run FAB
            FloatingActionButton(
                onClick = onStartRun,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.size(68.dp)
            ) {
                Icon(
                    Icons.Default.DirectionsRun,
                    contentDescription = "Start run",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
