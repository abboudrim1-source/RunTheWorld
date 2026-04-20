package com.runtheworld.ui.profile

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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.presentation.profile.AVATAR_COLORS
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileSetupScreen(
    onProfileSaved: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val pickImage = rememberImagePickerLauncher { base64 -> base64?.let { viewModel.onAvatarSelected(it) } }

    LaunchedEffect(Unit) { viewModel.loadExistingProfile() }
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onProfileSaved()
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Branding
            GradientText(
                text = "RUN THE WORLD",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Set up your runner profile",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.45f),
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(40.dp))

            // Avatar preview — tap to pick photo
            val selectedColor = parseHexColor(state.selectedColorHex)
            val avatarBitmap = rememberBase64Bitmap(state.avatarBase64)
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .colorGlow(selectedColor, elevation = 20.dp)
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(selectedColor, selectedColor.copy(alpha = 0.7f))
                        )
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { pickImage() },
                contentAlignment = Alignment.Center
            ) {
                if (avatarBitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = avatarBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val initial = state.displayName.firstOrNull()?.uppercaseChar()
                        ?: state.username.firstOrNull()?.uppercaseChar()
                    if (initial != null) {
                        Text(initial.toString(), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    } else {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Add photo", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (state.avatarBase64 != null) "Tap to change photo" else "Tap to add photo",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f)
            )

            Spacer(Modifier.height(36.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SectionLabel("YOUR INFO")
                Spacer(Modifier.height(12.dp))

                // Full name
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

                // Runner name
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

                Spacer(Modifier.height(12.dp))

                // City
                OutlinedTextField(
                    value = state.city,
                    onValueChange = viewModel::onCityChange,
                    label = { Text("City") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.LocationCity, null, tint = NeonOrange.copy(alpha = 0.8f))
                    },
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

                Spacer(Modifier.height(32.dp))

                GradientButton(
                    text = "LET'S RUN",
                    onClick = viewModel::saveProfile,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
internal fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NeonOrange,
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    focusedLabelColor = NeonOrange,
    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
    cursorColor = NeonOrange,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White.copy(alpha = 0.9f)
)
