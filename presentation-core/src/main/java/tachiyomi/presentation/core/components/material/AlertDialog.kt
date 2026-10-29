package tachiyomi.presentation.core.components.material

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.shapes.RoundedRectangle

@Composable
fun AlertDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: @Composable () -> Unit,
) {
    AlertDialogContent(
        modifier = modifier,
        icon = icon,
        title = title,
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    val textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp,
                    )
                    ProvideTextStyle(textStyle) {
                        Box(
                            Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp),
                        ) {
                            text()
                        }
                    }
                }

                androidx.compose.material3.HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.primary,
                    ) {
                        val textStyle = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 17.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        ProvideTextStyle(value = textStyle, content = buttons)
                    }
                }
            }
        },
    )
}

@Composable
fun AlertDialogContent(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val dialogShape = RoundedCornerShape(20.dp)

    val dialogModifier = if (isGlass) {
        modifier
            .sizeIn(minWidth = 280.dp, maxWidth = 320.dp)
            .liquidGlassPopupSurface(shape = dialogShape)
    } else {
        modifier
            .sizeIn(minWidth = 280.dp, maxWidth = 320.dp)
            .shadow(elevation = 16.dp, shape = dialogShape)
            .clip(dialogShape)
            .background(if (isDark) Color(0xFF222226) else Color(0xFFF9F9F9))
    }
    Column(
        modifier = dialogModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon != null || title != null) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 18.dp,
                        end = 16.dp,
                        bottom = 8.dp,
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                icon?.let {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.secondary,
                    ) {
                        Box(
                            Modifier
                                .padding(IconPadding)
                                .align(Alignment.CenterHorizontally),
                        ) {
                            icon()
                        }
                    }
                }
                title?.let {
                    CompositionLocalProvider(
                        LocalContentColor provides (if (isDark) Color.White else Color(0xFF111113)),
                    ) {
                        val textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = if (isDark) Color.White else Color(0xFF111113),
                        )
                        ProvideTextStyle(textStyle) {
                            Box(
                                Modifier
                                    .padding(PaddingValues(bottom = 6.dp))
                                    .align(Alignment.CenterHorizontally),
                            ) {
                                title()
                            }
                        }
                    }
                }
            }
        }
        CompositionLocalProvider(
            LocalContentColor provides (if (isDark) Color(0xCCFFFFFF) else Color(0xCC111113)),
        ) {
            content?.invoke(this)
        }
    }
}

// Paddings for each of the dialog's parts.
private val DialogPadding = 24.dp
private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)

private val MinWidth = 280.dp
private val MaxWidth = 560.dp

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.material3.AlertDialogDefaults.shape,
    containerColor: androidx.compose.ui.graphics.Color = androidx.compose.material3.AlertDialogDefaults.containerColor,
    iconContentColor: androidx.compose.ui.graphics.Color = androidx.compose.material3.AlertDialogDefaults.iconContentColor,
    titleContentColor: androidx.compose.ui.graphics.Color = androidx.compose.material3.AlertDialogDefaults.titleContentColor,
    textContentColor: androidx.compose.ui.graphics.Color = androidx.compose.material3.AlertDialogDefaults.textContentColor,
    tonalElevation: androidx.compose.ui.unit.Dp = androidx.compose.material3.AlertDialogDefaults.TonalElevation,
    properties: androidx.compose.ui.window.DialogProperties = androidx.compose.ui.window.DialogProperties(),
) {
    CupertinoAlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier,
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = text,
        properties = properties,
    )
}
