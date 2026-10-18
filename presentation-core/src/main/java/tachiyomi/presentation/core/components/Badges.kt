package tachiyomi.presentation.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.shapes.Capsule
import kotlinx.collections.immutable.persistentMapOf
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig

@Composable
fun BadgeGroup(
    modifier: Modifier = Modifier,
    shape: Shape = Capsule(),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .clip(shape)
            .height(18.dp),
    ) {
        content()
    }
}

@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
    shape: Shape = RectangleShape,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    Text(
        text = text,
        modifier = modifier
            .clip(shape)
            .then(
                if (isGlass) {
                    Modifier.background(color.copy(alpha = 0.45f))
                } else {
                    Modifier.background(color)
                },
            )
            .padding(horizontal = 4.dp, vertical = 1.dp),
        color = textColor,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
fun Badge(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    iconColor: Color = MaterialTheme.colorScheme.onSecondary,
    shape: Shape = RectangleShape,
) {
    val iconContentPlaceholder = "[icon]"
    val text = buildAnnotatedString {
        appendInlineContent(iconContentPlaceholder)
    }
    val inlineContent = persistentMapOf(
        Pair(
            iconContentPlaceholder,
            InlineTextContent(
                Placeholder(
                    width = MaterialTheme.typography.bodySmall.fontSize,
                    height = MaterialTheme.typography.bodySmall.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                ),
            ) {
                Icon(
                    imageVector = imageVector,
                    tint = iconColor,
                    contentDescription = null,
                )
            },
        ),
    )

    Text(
        text = text,
        inlineContent = inlineContent,
        modifier = modifier
            .clip(shape)
            .background(color)
            .padding(horizontal = 3.dp, vertical = 1.dp),
        color = iconColor,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        style = MaterialTheme.typography.bodySmall,
    )
}

// KMK -->
@Composable
fun Badge(
    painter: Painter,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    tint: Color = Color.Unspecified,
    shape: Shape = RectangleShape,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(shape)
            .background(color),
    ) {
        Icon(
            painter = painter,
            tint = tint,
            contentDescription = null,
            modifier = modifier,
        )
    }
}

@Composable
fun Badge(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    tint: Color? = null,
    shape: Shape = RectangleShape,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(shape)
            .background(color),
    ) {
        Image(
            bitmap = imageBitmap,
            colorFilter = tint?.let { ColorFilter.tint(it) },
            contentDescription = null,
            modifier = modifier,
        )
    }
}
// KMK <--
