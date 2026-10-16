package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
internal fun InfoWidget(text: String) {
    val isGlass = LocalLiquidGlassConfig.current.enabled

    if (isGlass) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 24.dp, top = 4.dp, bottom = 8.dp),
        ) {
            Text(
                text = text,
                color = Color(0xFF8E8E93),
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
    } else {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = PrefsHorizontalPadding,
                    vertical = MaterialTheme.padding.medium,
                )
                .secondaryItemAlpha(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun InfoWidgetPreview() {
    TachiyomiPreviewTheme {
        Surface {
            InfoWidget(text = stringResource(MR.strings.download_ahead_info))
        }
    }
}
