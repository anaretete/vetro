package eu.kanade.presentation.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kyant.shapes.Capsule
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.SettingsItemsPaddings
import tachiyomi.presentation.core.components.material.Button
import tachiyomi.presentation.core.components.material.FilledTonalButton
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.OutlinedButton
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ModeSelectionDialog(
    onApply: () -> Unit,
    onUseDefault: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    Box(modifier = Modifier.padding(vertical = 12.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isGlass) {
                // Apple HIG sheet drag handle
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .size(36.dp, 5.dp)
                        .clip(Capsule())
                        .background(if (isDark) Color(0x44FFFFFF) else Color(0x33000000)),
                )
            }

            content()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = SettingsItemsPaddings.Horizontal,
                        vertical = 12.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isGlass) {
                    onUseDefault?.let {
                        Button(
                            onClick = it,
                            modifier = Modifier
                                .height(44.dp)
                                .liquidGlassSurface(
                                    shape = Capsule(),
                                    blurRadius = 12.dp,
                                    refractionHeight = 14.dp,
                                    refractionAmount = 20.dp,
                                    tintColor = if (isDark) Color(0x55252528) else Color(0x66FFFFFF),
                                ),
                        ) {
                            Text(
                                text = stringResource(MR.strings.action_revert_to_default),
                                color = if (isDark) Color.White else Color(0xFF1C1C1E),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onApply,
                        modifier = Modifier
                            .height(44.dp)
                            .liquidGlassSurface(
                                shape = Capsule(),
                                blurRadius = 6.dp,
                                refractionHeight = 16.dp,
                                refractionAmount = 24.dp,
                                tintColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = stringResource(MR.strings.action_apply),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                } else {
                    onUseDefault?.let {
                        OutlinedButton(onClick = it) {
                            Text(text = stringResource(MR.strings.action_revert_to_default))
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    FilledTonalButton(
                        onClick = onApply,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                            )
                            Text(text = stringResource(MR.strings.action_apply))
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    TachiyomiPreviewTheme {
        Surface {
            Column {
                ModeSelectionDialog(
                    onApply = {},
                    onUseDefault = {},
                ) {
                    Text("Dummy content")
                }

                ModeSelectionDialog(
                    onApply = {},
                ) {
                    Text("Dummy content without default")
                }
            }
        }
    }
}
