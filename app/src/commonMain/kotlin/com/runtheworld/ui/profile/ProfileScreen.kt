package com.runtheworld.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.presentation.friends.FriendsViewModel
import com.runtheworld.presentation.profile.AVATAR_COLORS
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onAddFriends: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
    friendsViewModel: FriendsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val friendsState by friendsViewModel.state.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadExistingProfile()
            friendsViewModel.loadAll()
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            viewModel.resetSaved()
            onBack()
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = DarkSurface,
            title = {
                Text("Sign out?", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Your territory and run history stay saved on this device.",
                    color = Color.White.copy(alpha = 0.6f)
                )
            },
            confirmButton = {
                TextButton(onClick = { showSignOutDialog = false; onSignOut() }) {
                    Text("Sign out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                    text = "PROFILE",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Avatar section
            val avatarColor = parseHexColor(state.selectedColorHex)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .colorGlow(avatarColor, elevation = 24.dp)
                        .clip(CircleShape)
                        .background(
                            androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(avatarColor, avatarColor.copy(alpha = 0.6f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = state.displayName.firstOrNull()?.uppercaseChar()
                        ?: state.username.firstOrNull()?.uppercaseChar()
                    if (initial != null) {
                        Text(
                            text = initial.toString(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (state.displayName.isNotBlank()) {
                    Text(
                        text = state.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                if (state.username.isNotBlank()) {
                    Text(
                        text = "@${state.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonOrange.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Edit form card
            GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

                SectionLabel("EDIT INFO")
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.displayName,
                    onValueChange = viewModel::onDisplayNameChange,
                    label = { Text("Full name") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Person, null, tint = NeonOrange.copy(alpha = 0.8f))
                    },
                    colors = profileFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange,
                    label = { Text("Runner name") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Badge, null, tint = NeonOrange.copy(alpha = 0.8f))
                    },
                    isError = state.error != null,
                    supportingText = state.error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = profileFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                SectionLabel("TERRITORY COLOUR")
                Spacer(Modifier.height(14.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(AVATAR_COLORS) { colorHex ->
                        val color = parseHexColor(colorHex)
                        val selected = state.selectedColorHex == colorHex
                        Box(
                            modifier = Modifier
                                .size(if (selected) 52.dp else 44.dp)
                                .then(if (selected) Modifier.colorGlow(color, elevation = 18.dp) else Modifier)
                                .clip(CircleShape)
                                .background(
                                    androidx.compose.ui.graphics.Brush.radialGradient(
                                        colors = listOf(color, color.copy(alpha = 0.7f))
                                    )
                                )
                                .then(
                                    if (selected) Modifier.border(2.dp, Color.White, CircleShape)
                                    else Modifier
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { viewModel.onColorSelect(colorHex) }
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                GradientButton(
                    text = "SAVE CHANGES",
                    onClick = viewModel::saveProfile,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Friends section
            GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SectionLabel("FRIENDS (${friendsState.friends.size})")
                    IconButton(onClick = onAddFriends) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = "Add friends",
                            tint = NeonOrange,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                if (friendsState.friends.isEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No friends yet — search by username to add some!",
                        color = Color.White.copy(alpha = 0.4f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))
                } else {
                    Spacer(Modifier.height(12.dp))
                    friendsState.friends.forEach { friend ->
                        val friendColor = parseHexColor(friend.colorHex)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.radialGradient(
                                            colors = listOf(friendColor, friendColor.copy(alpha = 0.6f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = friend.username.first().uppercaseChar().toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Column {
                                if (friend.displayName.isNotBlank()) {
                                    Text(friend.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Text("@${friend.username}", color = NeonOrange.copy(alpha = 0.8f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sign out button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x18FF4466))
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { showSignOutDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "SIGN OUT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
