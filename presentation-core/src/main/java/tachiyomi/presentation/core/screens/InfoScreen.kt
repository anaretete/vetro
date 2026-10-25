package tachiyomi.presentation.core.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kyant.backdrop.catalog.components.LiquidButton
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle
import tachiyomi.presentation.core.components.material.Button
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.OutlinedButton
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
fun InfoScreen(
    icon: ImageVector,
    headingText: String,
    subtitleText: String,
    acceptText: String,
    onAcceptClick: () -> Unit,
    canAccept: Boolean = true,
    rejectText: String? = null,
    onRejectClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    Scaffold(
        bottomBar = {
            val strokeWidth = Dp.Hairline
            val borderColor = if (isGlass) {
                if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA)
            } else {
                MaterialTheme.colorScheme.outline
            }
            val bottomBg = if (isGlass) {
                if (isDark) Color(0xFF121212) else Color(0xFFF9F9F9)
            } else {
                MaterialTheme.colorScheme.background
            }
            Column(
                modifier = Modifier
                    .background(bottomBg)
                    .drawBehind {
                        drawLine(
                            borderColor,
                            Offset(0f, 0f),
                            Offset(size.width, 0f),
                            strokeWidth.value,
                        )
                    }
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                    .padding(
                        horizontal = MaterialTheme.padding.medium,
                        vertical = MaterialTheme.padding.small,
                    ),
            ) {
                if (isGlass) {
                    LiquidButton(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = canAccept,
                        onClick = onAcceptClick,
                        tint = MaterialTheme.colorScheme.primary,
                        shape = Capsule(),
                    ) {
                        Text(
                            text = acceptText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White,
                        )
                    }
                    if (rejectText != null && onRejectClick != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LiquidButton(
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            onClick = onRejectClick,
                            tint = Color(0xFF8E8E93),
                            shape = Capsule(),
                        ) {
                            Text(
                                text = rejectText,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isDark) Color.White else Color(0xFF1C1C1E),
                            )
                        }
                    }
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canAccept,
                        onClick = onAcceptClick,
                    ) {
                        Text(text = acceptText)
                    }
                    if (rejectText != null && onRejectClick != null) {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onRejectClick,
                        ) {
                            Text(text = rejectText)
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        // Status bar scrim
        Box(
            modifier = Modifier
                .zIndex(2f)
                .secondaryItemAlpha()
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .height(paddingValues.calculateTopPadding()),
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(top = 48.dp)
                .padding(horizontal = MaterialTheme.padding.medium),
        ) {
            if (isGlass) {
                Box(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.padding.medium)
                        .size(64.dp)
                        .clip(RoundedRectangle(16.dp, RoundedCornerStyle.Continuous))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = Color.White,
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.padding.small)
                        .size(48.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = headingText,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = subtitleText,
                modifier = Modifier
                    .secondaryItemAlpha()
                    .padding(vertical = MaterialTheme.padding.small),
                style = MaterialTheme.typography.titleSmall,
            )

            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun InfoScaffoldPreview() {
    InfoScreen(
        icon = Icons.Outlined.Newspaper,
        headingText = "Heading",
        subtitleText = "Subtitle",
        acceptText = "Accept",
        onAcceptClick = {},
        rejectText = "Reject",
        onRejectClick = {},
    ) {
        Text("Hello world")
    }
}
