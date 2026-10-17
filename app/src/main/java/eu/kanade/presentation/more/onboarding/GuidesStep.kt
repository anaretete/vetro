package eu.kanade.presentation.more.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import tachiyomi.presentation.core.components.material.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

internal class GuidesStep(
    private val onRestoreBackup: () -> Unit,
) : OnboardingStep {

    override val isComplete: Boolean = true

    @Composable
    override fun Content() {
        val handler = LocalUriHandler.current

        val isGlass = tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig.current.enabled

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        ) {
            Text(stringResource(MR.strings.onboarding_guides_new_user, stringResource(MR.strings.app_name)))

            if (isGlass) {
                com.kyant.backdrop.catalog.components.LiquidButton(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    onClick = { handler.openUri(GETTING_STARTED_URL) },
                    tint = androidx.compose.ui.graphics.Color(0xFF007AFF),
                    shape = com.kyant.shapes.Capsule(),
                ) {
                    Text(
                        text = stringResource(MR.strings.getting_started_guide),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { handler.openUri(GETTING_STARTED_URL) },
                ) {
                    Text(stringResource(MR.strings.getting_started_guide))
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Text(stringResource(MR.strings.onboarding_guides_returning_user, stringResource(MR.strings.app_name)))

            if (isGlass) {
                com.kyant.backdrop.catalog.components.LiquidButton(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    onClick = onRestoreBackup,
                    tint = androidx.compose.ui.graphics.Color(0xFF007AFF),
                    shape = com.kyant.shapes.Capsule(),
                ) {
                    Text(
                        text = stringResource(MR.strings.pref_restore_backup),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRestoreBackup,
                ) {
                    Text(stringResource(MR.strings.pref_restore_backup))
                }
            }
        }
    }
}

const val GETTING_STARTED_URL = "https://komikku-app.github.io/docs/guides/getting-started"

@PreviewLightDark
@Composable
private fun GuidesStepPreview() {
    TachiyomiPreviewTheme {
        GuidesStep(
            onRestoreBackup = {},
        ).Content()
    }
}
