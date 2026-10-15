package tachiyomi.presentation.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kyant.shapes.Capsule
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassSurface

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = LocalTextStyle.current,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val pillModifier = if (isGlass) {
        modifier
            .padding(start = 4.dp)
            .liquidGlassSurface(
                shape = Capsule(),
                blurRadius = 6.dp,
                refractionHeight = 8.dp,
                refractionAmount = 10.dp,
            )
    } else {
        modifier.padding(start = 4.dp)
    }

    Surface(
        modifier = pillModifier,
        shape = if (isGlass) Capsule() else MaterialTheme.shapes.extraLarge,
        color = if (isGlass) Color.Transparent else color,
        contentColor = contentColor,
    ) {
        Box(
            modifier = Modifier
                .padding(6.dp, 1.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                maxLines = 1,
                style = style,
            )
        }
    }
}

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
) {
    Pill(
        text = text,
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        style = LocalTextStyle.current.merge(fontSize = fontSize),
    )
}
