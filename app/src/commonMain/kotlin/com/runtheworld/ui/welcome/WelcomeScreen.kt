package com.runtheworld.ui.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.ui.theme.*

@Composable
fun WelcomeScreen(
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit
) {
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 3D animated globe (SceneView PBR on Android, Canvas on iOS)
            SceneViewGlobe(
                modifier = Modifier
                    .size(200.dp)
                    .colorGlow(NeonOrange, elevation = 32.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Title
            GradientText(
                text = "RUN THE WORLD",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                gradient = BrandGradient
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Claim the streets.\nOwn the city.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                lineHeight = 26.sp,
                letterSpacing = 0.3.sp
            )

            Spacer(Modifier.height(72.dp))

            GradientButton(
                text = "CREATE ACCOUNT",
                onClick = onCreateAccount,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            GlassButton(
                text = "SIGN IN",
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(40.dp))

            Text(
                text = "Your runs. Your territory.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.3f),
                letterSpacing = 1.sp
            )
        }
    }
}
