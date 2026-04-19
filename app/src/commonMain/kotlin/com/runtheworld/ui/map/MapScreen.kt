package com.runtheworld.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
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
import com.runtheworld.presentation.friends.FriendsViewModel
import com.runtheworld.presentation.map.MapViewModel
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(
    onStartRun: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
    onInbox: () -> Unit,
    viewModel: MapViewModel = koinViewModel(),
    friendsViewModel: FriendsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val friendsState by friendsViewModel.state.collectAsState()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            friendsViewModel.loadInboxCount()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RunTheWorldMap(
            modifier = Modifier.fillMaxSize(),
            territories = state.territories,
            currentUserUsername = state.currentUsername,
            currentPath = emptyList(),
            userLocation = null
        )

        // Inbox bell — top left with badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp)
        ) {
            BadgedBox(
                badge = {
                    if (friendsState.inboxCount > 0) {
                        Badge(
                            containerColor = NeonOrange,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = friendsState.inboxCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .mapGlassSurface(CircleShape)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onInbox
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Inbox",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Profile button — top right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(12.dp)
                .size(44.dp)
                .mapGlassSurface(CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onProfile
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(26.dp))
        }

        // Territory count pill — top center
        if (!state.isLoading) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp)
                    .mapGlassSurface(RoundedCornerShape(50.dp))
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text(
                    text = "${state.territories.size} territories claimed",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Bottom action row
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .mapGlassSurface(CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onHistory
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.History, contentDescription = "History", tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .brandGlow(CircleShape, elevation = 24.dp)
                    .clip(CircleShape)
                    .background(BrandGradient)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onStartRun
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DirectionsRun, contentDescription = "Start run", tint = Color.White, modifier = Modifier.size(36.dp))
            }

            Spacer(Modifier.size(52.dp))
        }
    }
}
