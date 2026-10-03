package com.esupplemental.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esupplemental.R
import com.esupplemental.presentation.ui.theme.ESupplementalTheme
import com.esupplemental.presentation.ui.theme.Quicksand

/** Official E-Supplemental logo. The source artwork is always rendered without cropping. */
@Composable
fun AppLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Image(
        painter = painterResource(R.drawable.e_s_logo),
        contentDescription = "E-Supplemental logo",
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    iconSize: Dp = 64.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppLogoIcon(size = iconSize)
        if (showLabel) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "E-SUPPLEMENTAL",
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (1.2).sp,
                        fontWeight = FontWeight.Black,
                        fontSize = (iconSize.value * 0.39f).sp
                    ),
                    fontFamily = Quicksand
                )
                Spacer(modifier = Modifier.height(5.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "LISTEN • LEARN • GROW",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            letterSpacing = 1.1.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (iconSize.value * 0.15f).sp
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Official Logo")
@Composable
private fun AppLogoPreview() {
    ESupplementalTheme(darkTheme = false) {
        Box(Modifier.padding(24.dp).background(MaterialTheme.colorScheme.background)) { AppLogo(iconSize = 80.dp) }
    }
}

@Preview(showBackground = true, name = "Official Logo Dark")
@Composable
private fun AppLogoDarkPreview() {
    ESupplementalTheme(darkTheme = true) {
        Box(Modifier.padding(24.dp).background(MaterialTheme.colorScheme.background)) { AppLogo(iconSize = 80.dp) }
    }
}
