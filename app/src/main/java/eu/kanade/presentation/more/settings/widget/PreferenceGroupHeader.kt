package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig

@Composable
fun PreferenceGroupHeader(title: String) {
    val isGlass = LocalLiquidGlassConfig.current.enabled

    if (isGlass) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
        ) {
            Text(
                text = title.uppercase(),
                color = Color(0xFF8E8E93),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
            )
        }
    } else {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 14.dp),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = PrefsHorizontalPadding),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
