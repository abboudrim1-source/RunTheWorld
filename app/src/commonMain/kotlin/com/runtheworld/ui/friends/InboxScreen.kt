package com.runtheworld.ui.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.runtheworld.domain.model.FriendRequest
import com.runtheworld.presentation.friends.FriendsViewModel
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InboxScreen(
    onBack: () -> Unit,
    viewModel: FriendsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadAll()
        }
    }

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                GradientText(
                    text = "FRIEND REQUESTS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (state.inbox.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No pending friend requests",
                        color = Color.White.copy(alpha = 0.35f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.inbox, key = { it.id }) { request ->
                        FriendRequestRow(
                            request = request,
                            onAccept  = { viewModel.acceptRequest(request) },
                            onDecline = { viewModel.declineRequest(request) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendRequestRow(
    request: FriendRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassSurface(RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        val color = parseHexColor(request.sender.colorHex)
        Box(
            modifier = Modifier
                .size(48.dp)
                .colorGlow(color, elevation = 12.dp)
                .clip(CircleShape)
                .background(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(color, color.copy(alpha = 0.6f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = request.sender.username.first().uppercaseChar().toString(),
                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            if (request.sender.displayName.isNotBlank()) {
                Text(request.sender.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Text("@${request.sender.username}", color = NeonOrange.copy(alpha = 0.8f), fontSize = 13.sp)
            Text("wants to be your friend", color = Color.White.copy(alpha = 0.45f), fontSize = 12.sp)
        }

        // Decline
        IconButton(onClick = onDecline, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Decline", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
        }

        // Accept
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(NeonOrange),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onAccept, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}
