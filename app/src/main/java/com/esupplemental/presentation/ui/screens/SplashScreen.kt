package com.esupplemental.presentation.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.esupplemental.presentation.ui.components.AppBackground
import com.esupplemental.presentation.ui.components.AppLogo
import com.esupplemental.presentation.ui.components.AppLogoIcon
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun SplashScreen(
    progress: Float,
    onAnimationFinished: () -> Unit
) {
    val context = LocalContext.current

    // Tracks whether storage permission has been resolved (granted or denied — either way proceed)
    var permissionResolved by remember { mutableStateOf(false) }

    // ── API 30+ (Android 11+): MANAGE_ALL_FILES_ACCESS ───────────────────
    // requestLegacyExternalStorage is ignored on API 30+, so we need the
    // all-files-access permission to write to /storage/emulated/0/...
    val manageStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // User returned from Settings — granted or not, continue
        permissionResolved = true
    }

    // ── API 23–29: Runtime READ / WRITE ──────────────────────────────────
    val legacyPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionResolved = true
    }

    // ── Request permissions once on entry ────────────────────────────────
    LaunchedEffect(Unit) {
        when {
            // Android 11+ (API 30+) — need MANAGE_EXTERNAL_STORAGE
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        "package:${context.packageName}".toUri()
                    )
                    manageStorageLauncher.launch(intent)
                } else {
                    permissionResolved = true   // already granted
                }
            }

            // Android 6–10 (API 23–29) — runtime READ + WRITE

            // Below API 23 — permissions are granted at install time
            else -> {
                legacyPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    // ── Navigate only after permission dialog is dismissed AND progress is 100% ──
    LaunchedEffect(permissionResolved, progress) {
        if (permissionResolved && progress >= 1f) {
            onAnimationFinished()
        }
    }

    AppBackground {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Header Text Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 40.dp)
                ) {
                    AppLogo(iconSize = 80.dp)
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "for Listening Comprehension",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (80.dp.value * 0.18).sp
                        )
                    )
                    Text(
                        text = "Grade 6 Students",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (80.dp.value * 0.18).sp
                        )
                    )
                }

                // 2. Official listening-comprehension logo
                AppLogoIcon(
                    size = 180.dp,
                    modifier = Modifier.weight(1f, fill = false)
                )

                // 3. Footer Loading Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondaryContainer,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading...",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun SplashScreenPreview() {
    ESupplementalTheme {
        // Previewing with 70% progress to check the progress bar styling
        SplashScreen(progress = 0.7f, onAnimationFinished = {})
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun SplashScreenDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        // Previewing with 70% progress to check the progress bar styling
        SplashScreen(progress = 0.7f, onAnimationFinished = {})
    }
}
