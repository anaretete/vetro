package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Surface
import tachiyomi.presentation.core.components.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import eu.kanade.presentation.theme.TachiyomiPreviewTheme

@Composable
fun SwitchPreferenceWidget(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: CharSequence? = null,
    // KMK -->
    /** Can be either [ImageVector] or [Painter] */
    icon: Any? = null,
    iconContent: @Composable (() -> Unit)? = null,
    // KMK <--
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChanged: (Boolean) -> Unit,
) {
    TextPreferenceWidget(
        modifier = modifier.alpha(if (enabled) 1f else 0.38f),
        title = title,
        subtitle = subtitle,
        icon = icon,
        iconContent = iconContent,
        widget = {
            Switch(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChanged else null,
                enabled = enabled,
                modifier = Modifier.padding(start = TrailingWidgetBuffer),
            )
        },
        onPreferenceClick = if (enabled) { { onCheckedChanged(!checked) } } else null,
    )
}

@PreviewLightDark
@Composable
private fun SwitchPreferenceWidgetPreview() {
    TachiyomiPreviewTheme {
        Surface {
            Column {
                SwitchPreferenceWidget(
                    title = "Text preference with icon",
                    subtitle = "Text preference summary",
                    icon = Icons.Filled.Preview,
                    checked = true,
                    onCheckedChanged = {},
                )
                SwitchPreferenceWidget(
                    title = "Text preference",
                    subtitle = "Text preference summary",
                    checked = false,
                    onCheckedChanged = {},
                )
                SwitchPreferenceWidget(
                    title = "Text preference no summary",
                    checked = false,
                    onCheckedChanged = {},
                )
                SwitchPreferenceWidget(
                    title = "Another text preference no summary",
                    checked = false,
                    onCheckedChanged = {},
                )
            }
        }
    }
}
