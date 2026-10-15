package tachiyomi.presentation.core.components.material

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.kyant.shapes.Capsule

/**
 * Authentic Apple iOS 26 / Cupertino Alert Dialog
 * Features 280dp squircle form factor, centered typography, 0.5dp hairline dividers, and liquid glass refraction.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupertinoAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
) {
    val isDark = isSystemInDarkTheme()
    val dialogBg = if (isDark) Color(0xFF252528).copy(alpha = 0.70f) else Color(0xFFFFFFFF).copy(alpha = 0.85f)

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .width(280.dp)
            .liquidGlassSurface(
                shape = RoundedCornerShape(20.dp),
                blurRadius = 24.dp,
                refractionHeight = 24.dp,
                refractionAmount = 32.dp,
                tintColor = dialogBg,
            ),
        properties = properties,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (title != null) {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        title()
                    }
                }
                if (text != null) {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                        ),
                    ) {
                        text()
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (dismissButton != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides Color(0xFF0A84FF),
                        ) {
                            ProvideTextStyle(
                                value = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF0A84FF),
                                ),
                            ) {
                                dismissButton()
                            }
                        }
                    }

                    VerticalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides Color(0xFF0A84FF),
                    ) {
                        ProvideTextStyle(
                            value = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0A84FF),
                            ),
                        ) {
                            confirmButton()
                        }
                    }
                }
            }
        }
    }
}

/**
 * iOS Sheet Grabber Capsule
 */
@Composable
fun CupertinoGrabber(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
) {
    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .size(width = 36.dp, height = 5.dp)
            .clip(Capsule())
            .liquidGlassSurface(
                shape = Capsule(),
                blurRadius = 4.dp,
                refractionHeight = 4.dp,
                tintColor = color,
            ),
    )
}

/**
 * iOS Circular Checkmark Indicator
 */
@Composable
fun CupertinoCheckmark(
    checked: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0A84FF),
) {
    if (checked) {
        Box(
            modifier = modifier
                .size(22.dp)
                .liquidGlassSurface(
                    shape = CircleShape,
                    blurRadius = 6.dp,
                    refractionHeight = 8.dp,
                    tintColor = tint.copy(alpha = 0.90f),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.40f),
                    shape = CircleShape,
                ),
        )
    }
}

/**
 * iOS Translucent Action Sheet Card
 */
@Composable
fun CupertinoActionSheetCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF222226).copy(alpha = 0.75f) else Color(0xFFF9F9F9).copy(alpha = 0.85f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlassSurface(
                shape = RoundedCornerShape(20.dp),
                blurRadius = 22.dp,
                refractionHeight = 22.dp,
                refractionAmount = 30.dp,
                tintColor = cardBg,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

/**
 * iOS Detached Action Sheet Cancel Pill Button
 */
@Composable
fun CupertinoCancelPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Cancel",
) {
    val isDark = isSystemInDarkTheme()
    val pillBg = if (isDark) Color(0xFF222226).copy(alpha = 0.80f) else Color(0xFFFFFFFF).copy(alpha = 0.90f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .liquidGlassSurface(
                shape = RoundedCornerShape(20.dp),
                blurRadius = 20.dp,
                refractionHeight = 20.dp,
                refractionAmount = 28.dp,
                tintColor = pillBg,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color(0xFF0A84FF),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
