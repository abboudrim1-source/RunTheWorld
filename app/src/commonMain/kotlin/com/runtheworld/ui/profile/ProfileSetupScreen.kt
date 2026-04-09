package com.runtheworld.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.presentation.profile.AVATAR_COLORS
import com.runtheworld.presentation.profile.ProfileViewModel
import com.runtheworld.ui.theme.parseHexColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileSetupScreen(
    onProfileSaved: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onProfileSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Run The World",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "Claim the streets. Own the city.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(48.dp))

        // Username field
        OutlinedTextField(
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Runner name") },
            singleLine = true,
            isError = state.error != null,
            supportingText = state.error?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Choose your territory colour",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(AVATAR_COLORS) { colorHex ->
                val color = parseHexColor(colorHex)
                val selected = state.selectedColorHex == colorHex
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selected) 3.dp else 0.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                        .clickable { viewModel.onColorSelect(colorHex) }
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = viewModel::saveProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Let's Run", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
