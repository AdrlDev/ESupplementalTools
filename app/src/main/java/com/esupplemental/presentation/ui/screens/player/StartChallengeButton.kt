package com.esupplemental.presentation.ui.screens.player

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.theme.PlayfulShapes
import com.esupplemental.presentation.ui.theme.elevations
import com.esupplemental.presentation.ui.theme.spacing

@Composable
fun StartChallengeButton(accentColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = PlayfulShapes.GameCard,
        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = MaterialTheme.elevations.small)
    ) {
        Text("START CHALLENGE", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
        Spacer(Modifier.width(MaterialTheme.spacing.small))
        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
    }
}
