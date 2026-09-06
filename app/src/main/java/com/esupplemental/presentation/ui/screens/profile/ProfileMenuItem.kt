package com.esupplemental.presentation.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.ESupplementalTheme

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    baseColor: Color,
    onClick: () -> Unit = {} // Added onClick
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick // Ensure your Material3 Card supports onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = baseColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White, // High contrast for the badge
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile Menu - Light Mode")
@Composable
fun ProfileMenuItemLightPreview() {
    ESupplementalTheme(darkTheme = false) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Rounded.Person,
                    label = "Account Information",
                    baseColor = MaterialTheme.colorScheme.primary
                )
                ProfileMenuItem(
                    icon = Icons.Rounded.History,
                    label = "Exercise History",
                    baseColor = MaterialTheme.colorScheme.secondary
                )
                ProfileMenuItem(
                    icon = Icons.Rounded.Settings,
                    label = "Settings",
                    baseColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Profile Menu - Dark Mode")
@Composable
fun ProfileMenuItemDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Rounded.Person,
                    label = "Profile Details",
                    baseColor = MaterialTheme.colorScheme.surfaceContainer
                )
            }
        }
    }
}