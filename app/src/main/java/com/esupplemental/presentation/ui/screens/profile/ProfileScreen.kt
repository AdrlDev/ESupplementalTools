package com.esupplemental.presentation.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.ThemeColorPresets
import com.esupplemental.presentation.viewmodel.AuthViewModel
import com.esupplemental.presentation.viewmodel.HomeViewModel
import com.esupplemental.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: HomeViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onLoggedOut: () -> Unit,
    onNavigateToGeneralSettings: () -> Unit = {},
    onNavigateToProfileSettings: () -> Unit = {}
) {
    val homeState by viewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val displayName = settingsState.currentUser?.name
        ?: homeState.userStats.userName.ifBlank { "Student" }

    AppBackground {
        ProfileScreenContent(
            userName = displayName,
            onLogoutClick = {
                showLogoutDialog = true
            },
            onGeneralSettingsClick = onNavigateToGeneralSettings,
            onProfileSettingsClick = onNavigateToProfileSettings
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Logout Account?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text("Are you sure you want to log out? Your game scores and progress are saved! 🏆") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        onLoggedOut()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Yes, Logout", color = MaterialTheme.colorScheme.onError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Stay Here", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun ProfileScreenContent(
    userName: String = "adriel espiritu",
    onLogoutClick: () -> Unit = {},
    onGeneralSettingsClick: () -> Unit = {},
    onProfileSettingsClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_wave")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // --- Avatar Section ---
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .scale(waveScale),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {}

            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userName.trim().take(1).uppercase().ifEmpty { "S" },
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val formattedName = userName.split(" ").filter { it.isNotBlank() }.joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }.ifEmpty { "Student" }

        Text(
            text = formattedName,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(36.dp))

        // --- Menu Section ---
        MenuHeader("ACCOUNT SETTINGS")
        ProfileMenuItem(
            icon = Icons.Rounded.Settings,
            label = "General Settings",
            baseColor = MaterialTheme.colorScheme.primary,
            onClick = onGeneralSettingsClick
        )
        ProfileMenuItem(
            icon = Icons.Rounded.People,
            label = "Profile & Security",
            baseColor = MaterialTheme.colorScheme.secondary,
            onClick = onProfileSettingsClick
        )

        Spacer(Modifier.height(24.dp))

        // --- EXIT ---
        MenuHeader("SESSION")
        ProfileMenuItem(
            icon = Icons.AutoMirrored.Rounded.ExitToApp,
            label = "Logout",
            baseColor = MaterialTheme.colorScheme.error,
            onClick = onLogoutClick
        )

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
fun MenuHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneralSettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val darkModeSetting by settingsViewModel.darkModeSetting.collectAsStateWithLifecycle()
    val themeColorSetting by settingsViewModel.themeColorSetting.collectAsStateWithLifecycle()
    val soundEffectsSetting by settingsViewModel.soundEffectsSetting.collectAsStateWithLifecycle()

    val systemDark = isSystemInDarkTheme()
    val isDarkActive = when (darkModeSetting) {
        "dark" -> true
        "light" -> false
        else -> systemDark
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsTopBar(title = "General Settings", onBack = onBack)

            Spacer(Modifier.height(28.dp))

            SettingsSection(title = "THEME & DISPLAY") {
                // Dark Mode Switch
                SettingsItem(
                    icon = Icons.Rounded.DarkMode,
                    title = "Dark Mode",
                    subtitle = when (darkModeSetting) {
                        "dark" -> "Dark theme enabled"
                        "light" -> "Light theme enabled"
                        else -> "Following system theme (${if (systemDark) "Dark" else "Light"})"
                    },
                    trailing = {
                        Switch(
                            checked = isDarkActive,
                            onCheckedChange = { isChecked ->
                                settingsViewModel.setDarkMode(if (isChecked) "dark" else "light")
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                )

                // Quick System Default Reset Row
                if (darkModeSetting != "system") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { settingsViewModel.setDarkMode("system") }
                        ) {
                            Text(
                                text = "Use System Default",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "ACCENT & COLOR THEME") {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Choose App Theme Color",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ThemeColorPresets.forEach { preset ->
                            val isSelected = preset.key == themeColorSetting
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, preset.previewColor) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .clickable {
                                        settingsViewModel.setThemeColor(preset.key)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(preset.previewColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = preset.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "AUDIO & FEEDBACK") {
                SettingsItem(
                    icon = Icons.Rounded.VolumeUp,
                    title = "Sound Effects & Audio",
                    subtitle = if (soundEffectsSetting) "Interactive game audio enabled" else "Sound muted",
                    trailing = {
                        Switch(
                            checked = soundEffectsSetting,
                            onCheckedChange = { isChecked ->
                                settingsViewModel.setSoundEffects(isChecked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "ABOUT APP") {
                SettingsItem(
                    icon = Icons.Rounded.Info,
                    title = "E-Supplemental Learning App",
                    subtitle = "Version 1.0.0 (Grade School Edition)"
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileSettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val state by settingsViewModel.uiState.collectAsStateWithLifecycle()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.successMessage, state.errorMessage) {
        if (state.successMessage != null || state.errorMessage != null) {
            delay(3500)
            settingsViewModel.clearMessages()
        }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsTopBar(title = "Profile & Security", onBack = onBack)

            Spacer(Modifier.height(20.dp))

            // Feedback Message Banners
            AnimatedVisibility(
                visible = state.successMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = state.successMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = state.errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            SettingsSection(title = "ACCOUNT INFORMATION") {
                val currentUserName = state.currentUser?.name.orEmpty().ifBlank { "Student" }
                val currentUserEmail = state.currentUser?.email.orEmpty().ifBlank { "student@esupplemental.app" }

                SettingsItem(
                    icon = Icons.Rounded.AccountCircle,
                    title = "Learner Name",
                    subtitle = currentUserName,
                    trailing = {
                        IconButton(onClick = { showEditNameDialog = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Edit name",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    onClick = { showEditNameDialog = true }
                )

                SettingsItem(
                    icon = Icons.Rounded.Email,
                    title = "Email Address",
                    subtitle = currentUserEmail
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "SECURITY") {
                SettingsItem(
                    icon = Icons.Rounded.Lock,
                    title = "Change Password",
                    subtitle = "Update your login credentials",
                    onClick = { showChangePasswordDialog = true }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // --- Edit Name Dialog ---
    if (showEditNameDialog) {
        var newNameInput by remember { mutableStateOf(state.currentUser?.name.orEmpty()) }

        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text(
                    text = "Update Learner Name",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter your full name or nickname for the app.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newNameInput,
                        onValueChange = { newNameInput = it },
                        label = { Text("Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEditNameDialog = false
                        settingsViewModel.updateUsername(newNameInput)
                    },
                    shape = RoundedCornerShape(12.dp),
                    enabled = newNameInput.isNotBlank() && !state.isUpdating
                ) {
                    if (state.isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // --- Change Password Dialog ---
    if (showChangePasswordDialog) {
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isCurrentVisible by remember { mutableStateOf(false) }
        var isNewVisible by remember { mutableStateOf(false) }
        var localPasswordError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = {
                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (localPasswordError != null) {
                        Text(
                            text = localPasswordError.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            localPasswordError = null
                        },
                        label = { Text("Current Password") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (isCurrentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { isCurrentVisible = !isCurrentVisible }) {
                                Icon(
                                    imageVector = if (isCurrentVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = "Toggle visibility"
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            localPasswordError = null
                        },
                        label = { Text("New Password (min 6 chars)") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (isNewVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { isNewVisible = !isNewVisible }) {
                                Icon(
                                    imageVector = if (isNewVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = "Toggle visibility"
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            localPasswordError = null
                        },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (isNewVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPassword.isBlank() || newPassword.isBlank()) {
                            localPasswordError = "All fields are required"
                            return@Button
                        }
                        if (newPassword.length < 6) {
                            localPasswordError = "Password must be at least 6 characters"
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            localPasswordError = "New passwords do not match"
                            return@Button
                        }
                        showChangePasswordDialog = false
                        settingsViewModel.updatePassword(currentPassword, newPassword)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Update Password", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun SettingsTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick ?: {},
        color = Color.Transparent,
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailing != null) {
                trailing()
            } else if (onClick != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Profile Screen - Day Mode")
@Composable
fun ProfileScreenDayPreview() {
    ESupplementalTheme(darkTheme = false) {
        AppBackground {
            ProfileScreenContent()
        }
    }
}

@Preview(showBackground = true, name = "Profile Screen - Night Mode")
@Composable
fun ProfileScreenNightPreview() {
    ESupplementalTheme(darkTheme = true) {
        AppBackground {
            ProfileScreenContent()
        }
    }
}
