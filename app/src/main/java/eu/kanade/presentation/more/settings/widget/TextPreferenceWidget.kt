package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import tachiyomi.presentation.core.util.secondaryItemAlpha

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig

@Composable
fun TextPreferenceWidget(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: CharSequence? = null,
    // KMK -->
    /** Can be either [ImageVector] or [Painter] */
    icon: Any? = null,
    // KMK <--
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconContent: @Composable (() -> Unit)? = null,
    widget: @Composable (() -> Unit)? = null,
    onPreferenceClick: (() -> Unit)? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveWidget: @Composable (() -> Unit)? = widget ?: if (isGlass && onPreferenceClick != null) {
        {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF8E8E93).copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp),
            )
        }
    } else {
        null
    }

    BasePreferenceWidget(
        modifier = modifier,
        title = title,
        subcomponent = if (!subtitle.isNullOrBlank()) {
            {
                val textStartPadding = if (icon != null || iconContent != null) 0.dp else PrefsHorizontalPadding
                val subtitleStyle = if (isGlass) {
                    MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF8E8E93),
                    )
                } else {
                    MaterialTheme.typography.bodySmall
                }
                // SY -->
                if (subtitle is AnnotatedString) {
                    Text(
                        text = subtitle,
                        modifier = Modifier
                            .padding(start = textStartPadding, end = PrefsHorizontalPadding)
                            .secondaryItemAlpha(),
                        style = subtitleStyle,
                        maxLines = 10,
                    )
                } else {
                    // SY <--
                    Text(
                        text = subtitle.toString(),
                        modifier = Modifier
                            .padding(start = textStartPadding, end = PrefsHorizontalPadding)
                            .secondaryItemAlpha(),
                        style = subtitleStyle,
                        maxLines = 10,
                    )
                }
            }
        } else {
            null
        },
        icon = iconContent ?: if (icon != null) {
            {
                if (icon is ImageVector) {
                    Icon(
                        imageVector = icon,
                        tint = iconTint,
                        contentDescription = null,
                    )
                // KMK -->
                } else if (icon is Painter) {
                    Icon(
                        painter = icon,
                        tint = iconTint,
                        contentDescription = null,
                    )
                // KMK <--
                }
            }
        } else {
            null
        },
        onClick = onPreferenceClick,
        widget = effectiveWidget,
    )
}

@PreviewLightDark
@Composable
private fun TextPreferenceWidgetPreview() {
    TachiyomiPreviewTheme {
        Surface {
            Column {
                TextPreferenceWidget(
                    title = "Text preference with icon",
                    subtitle = "Text preference summary",
                    icon = Icons.Filled.Preview,
                    onPreferenceClick = {},
                )
                TextPreferenceWidget(
                    title = "Text preference",
                    subtitle = "Text preference summary",
                    onPreferenceClick = {},
                )
                // SY -->
                TextPreferenceWidget(
                    title = "Text preference",
                    subtitle = buildAnnotatedString {
                        append("Text preference ")

                        withStyle(SpanStyle(Color.Red)) {
                            append("summary")
                        }
                    },
                    onPreferenceClick = {},
                )
                // SY <--
            }
        }
    }
}
