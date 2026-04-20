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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
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
import com.runtheworld.domain.model.FriendUser
import com.runtheworld.presentation.friends.FriendsViewModel
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddFriendsScreen(
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
                    text = "ADD FRIENDS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text("Search by runner tag…", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = NeonOrange.copy(alpha = 0.8f))
                },
                trailingIcon = {
                    if (state.isSearching) {
                        CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonOrange,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    cursorColor = NeonOrange,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Spacer(Modifier.height(8.dp))

            when {
                state.query.isBlank() -> EmptyHint("Search for a runner by their tag")
                state.isSearching -> { /* spinner already in text field trailing icon */ }
                state.searchResults.isEmpty() -> EmptyHint("No runners found for \"${state.query}\"")
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.searchResults, key = { it.uid }) { user ->
                            val relationStatus = when {
                                user.uid in state.friendUids      -> RelationStatus.FRIENDS
                                user.uid in state.sentPendingUids -> RelationStatus.REQUESTED
                                else                              -> RelationStatus.NONE
                            }
                            UserSearchRow(
                                user = user,
                                status = relationStatus,
                                onSendRequest = { viewModel.sendRequest(user) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class RelationStatus { NONE, REQUESTED, FRIENDS }

@Composable
private fun EmptyHint(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = Color.White.copy(alpha = 0.35f), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun UserSearchRow(
    user: FriendUser,
    status: RelationStatus,
    onSendRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassSurface(RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        val color = parseHexColor(user.colorHex)
        Box(
            modifier = Modifier
                .size(44.dp)
                .colorGlow(color, elevation = 10.dp)
                .clip(CircleShape)
                .background(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(color, color.copy(alpha = 0.6f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.first().uppercaseChar().toString(),
                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            if (user.displayName.isNotBlank()) {
                Text(user.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Text("@${user.username}", color = NeonOrange.copy(alpha = 0.8f), fontSize = 13.sp)
        }

        when (status) {
            RelationStatus.FRIENDS -> Text("Friends", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            RelationStatus.REQUESTED -> Icon(Icons.Default.Check, contentDescription = "Requested", tint = NeonOrange.copy(alpha = 0.6f), modifier = Modifier.size(22.dp))
            RelationStatus.NONE -> IconButton(onClick = onSendRequest) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Send request", tint = NeonOrange, modifier = Modifier.size(22.dp))
            }
        }
    }
}
