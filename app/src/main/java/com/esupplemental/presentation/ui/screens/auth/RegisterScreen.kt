package com.esupplemental.presentation.ui.screens.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.presentation.state.AuthState
import com.esupplemental.presentation.state.RegistrationFormState
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.components.AppLogoIcon
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Get the state from ViewModel
    val state = viewModel.state
    val formState = viewModel.formState

    // Handle navigation on successful registration
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
        }
    }

    RegisterScreenContent(
        state = state,
        formState = formState,
        onRegisterClick = { name, email, password ->
            viewModel.onRegister(name, email, password)
        },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreenContent(
    state: AuthState,
    formState: RegistrationFormState,
    onRegisterClick: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Pulse animation for the logo - consistent with Login
    val infiniteTransition = rememberInfiniteTransition(label = "hero_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            // 1. Playful Hero Section
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulse),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                ) {}

                AppLogoIcon(size = 140.dp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Start Your Quest!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Create your explorer profile 🗺️",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            // 2. The "Profile Creator" Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayfulTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Callsign (Your Name)",
                        icon = Icons.Rounded.Face,
                        enabled = !state.isLoading,
                        isError = formState.nameError != null,
                        supportingText = formState.nameError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PlayfulTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        icon = Icons.Rounded.Email,
                        enabled = !state.isLoading,
                        isError = formState.emailError != null,
                        supportingText = formState.emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PlayfulTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Secret Code (Password)",
                        icon = Icons.Rounded.Lock,
                        isPassword = true,
                        enabled = !state.isLoading,
                        isError = formState.passwordError != null,
                        supportingText = formState.passwordError
                    )

                    if (state.error != null) {
                        Text(
                            text = "Oh no! ${state.error}",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. High-Energy "Create" Button
                    Button(
                        onClick = { onRegisterClick(name, email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Text(
                            "Join the Club! 🚀",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
            }

            TextButton(
                onClick = onNavigateToLogin,
                enabled = !state.isLoading
            ) {
                Text(
                    "Already an explorer? Sign in",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun RegisterScreenPreview() {
    ESupplementalTheme {
        val state = AuthState(
            isLoading = true
        )

        RegisterScreenContent(
            state = state,
            formState = RegistrationFormState(
                nameError = "Name is required",
                emailError = "Email is required",
                passwordError = "Password is required"
            ),
            onRegisterClick = { _, _, _ -> },
            onNavigateToLogin = {}
        )
    }
}
