package com.esupplemental.presentation.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esupplemental.presentation.ui.components.AppLogoIcon
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.spacing

@Composable
fun HeaderSection(title: String, subTitle: String) {
    val spacing = MaterialTheme.spacing
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.medium),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = spacing.small)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(spacing.small))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(
                    topStart = 0.dp, 
                    topEnd = spacing.medium, 
                    bottomStart = spacing.medium, 
                    bottomEnd = spacing.medium
                )
            ) {
                Text(
                    text = subTitle,
                    modifier = Modifier.padding(
                        horizontal = spacing.medium, 
                        vertical = spacing.extraSmall
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(Modifier.width(spacing.small))

        AppLogoIcon(size = 122.dp)
    }
}

@Preview(showBackground = true, name = "Header - Normal Name")
@Composable
fun HeaderSectionPreview() {
    ESupplementalTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HeaderSection(title = "Adriel", subTitle = "Ready to play and learn?")
        }
    }
}
