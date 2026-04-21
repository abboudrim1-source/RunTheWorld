package com.runtheworld.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runtheworld.presentation.auth.AuthViewModel
import com.runtheworld.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    initialSignUpMode: Boolean = false,
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(initialSignUpMode) { viewModel.setMode(initialSignUpMode) }
    LaunchedEffect(state.success) { if (state.success) onAuthSuccess() }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Header
            GradientText(
                text = if (state.isSignUpMode) "CREATE\nACCOUNT" else "SIGN IN",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                gradient = BrandGradient
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (state.isSignUpMode) "Join the runners" else "Welcome back",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(36.dp))

            // Glass form card
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                // Email
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null,
                            tint = NeonOrange.copy(alpha = 0.8f))
                    },
                    colors = authFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null,
                            tint = NeonOrange.copy(alpha = 0.8f))
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    },
                    colors = authFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Password strength indicators (sign-up)
                if (state.isSignUpMode && state.password.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    PasswordStrengthRow(password = state.password)
                }

                // Confirm password (sign-up)
                if (state.isSignUpMode) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = { Text("Confirm password") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.LockOpen, contentDescription = null,
                                tint = NeonOrange.copy(alpha = 0.8f))
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = authFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Error message
                if (state.error != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Submit button
                if (state.isLoading) {
                    Box(Modifier.fillMaxWidth().height(54.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp)
                    }
                } else {
                    GradientButton(
                        text = if (state.isSignUpMode) "CREATE ACCOUNT" else "SIGN IN",
                        onClick = viewModel::submit,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

            Spacer(Modifier.height(24.dp))

            // Toggle mode
            TextButton(onClick = viewModel::toggleMode) {
                Text(
                    text = if (state.isSignUpMode) "Already have an account? Sign in"
                    else "No account? Create one",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonOrange.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NeonOrange,
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    focusedLabelColor = NeonOrange,
    unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
    cursorColor = NeonOrange,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White.copy(alpha = 0.9f)
)

@Composable
private fun PasswordStrengthRow(password: String) {
    val checks = listOf(
        "8+ chars"    to (password.length >= 8),
        "Uppercase"   to password.any { it.isUpperCase() },
        "Lowercase"   to password.any { it.isLowerCase() },
        "Number"      to password.any { it.isDigit() },
        "Symbol"      to password.any { !it.isLetterOrDigit() }
    )
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        checks.forEach { (label, met) ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (met) NeonOrange else Color.White.copy(alpha = 0.3f),
                fontWeight = if (met) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
