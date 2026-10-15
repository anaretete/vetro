package tachiyomi.presentation.core.components.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val isGlass = LocalLiquidGlassConfig.current.enabled
    AlertDialogContent(
        modifier = modifier,
        icon = icon,
        title = title,
        content = {
            if (isGlass) {
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
                            LocalContentColor provides androidx.compose.ui.graphics.Color(0xFF0A84FF),
                        ) {
                            val textStyle = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 17.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                color = androidx.compose.ui.graphics.Color(0xFF0A84FF),
                            )
                            ProvideTextStyle(value = textStyle, content = buttons)
                        }
                    }
                }
            } else {
                Column {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                    ) {
                        val textStyle = MaterialTheme.typography.bodyMedium
                        ProvideTextStyle(textStyle) {
                            Box(
                                Modifier
                                    .weight(weight = 1f, fill = false)
                                    .padding(horizontal = DialogPadding)
                                    .padding(TextPadding)
                                    .align(Alignment.Start),
                            ) {
                                text()
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(
                                start = DialogPadding,
                                end = DialogPadding,
                                bottom = DialogPadding,
                            )
                            .align(Alignment.End),
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.primary,
                        ) {
                            val textStyle = MaterialTheme.typography.labelLarge
                            ProvideTextStyle(value = textStyle, content = buttons)
                        }
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
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val dialogBg = if (isDark) androidx.compose.ui.graphics.Color(0xFF222226).copy(alpha = 0.75f) else androidx.compose.ui.graphics.Color(0xFFF9F9F9).copy(alpha = 0.85f)

    val dialogModifier = if (isGlass) {
        modifier
            .sizeIn(minWidth = 280.dp, maxWidth = 320.dp)
            .liquidGlassSurface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                blurRadius = 22.dp,
                refractionHeight = 22.dp,
                refractionAmount = 30.dp,
                tintColor = dialogBg,
            )
    } else {
        modifier
            .sizeIn(minWidth = MinWidth, maxWidth = MaxWidth)
    }
    Column(
        modifier = dialogModifier,
        horizontalAlignment = if (isGlass) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        if (icon != null || title != null) {
            Column(
                modifier = Modifier
                    .padding(
                        start = if (isGlass) 16.dp else DialogPadding,
                        top = if (isGlass) 18.dp else DialogPadding,
                        end = if (isGlass) 16.dp else DialogPadding,
                        bottom = if (isGlass) 8.dp else 0.dp,
                    )
                    .fillMaxWidth(),
                horizontalAlignment = if (isGlass) Alignment.CenterHorizontally else Alignment.Start,
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
                        LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                    ) {
                        val textStyle = if (isGlass) {
                            MaterialTheme.typography.titleMedium.copy(
                                fontSize = 17.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        } else {
                            MaterialTheme.typography.headlineSmall
                        }
                        ProvideTextStyle(textStyle) {
                            Box(
                                Modifier
                                    .padding(if (isGlass) PaddingValues(bottom = 6.dp) else TitlePadding)
                                    .align(
                                        if (isGlass || icon != null) {
                                            Alignment.CenterHorizontally
                                        } else {
                                            Alignment.Start
                                        },
                                    ),
                            ) {
                                title()
                            }
                        }
                    }
                }
            }
        }
        content?.invoke(this)
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
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        CupertinoAlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = confirmButton,
            modifier = modifier,
            dismissButton = dismissButton,
            title = title,
            text = text,
            properties = properties,
        )
    } else {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = confirmButton,
            modifier = modifier,
            dismissButton = dismissButton,
            icon = icon,
            title = title,
            text = text,
            shape = shape,
            containerColor = containerColor,
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            textContentColor = textContentColor,
            tonalElevation = tonalElevation,
            properties = properties,
        )
    }
}

