package tachiyomi.presentation.core.components.material

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle

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
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val dialogShape = RoundedCornerShape(20.dp)
    val dialogModifier = if (isGlass) {
        modifier
            .widthIn(min = 270.dp, max = 320.dp)
            .liquidGlassSurface(
                shape = dialogShape,
                blurRadius = 18.dp,
                refractionHeight = 18.dp,
                refractionAmount = 26.dp,
                tintColor = Color.Unspecified,
                surfaceColor = Color.Unspecified,
                depthEffect = true,
            )
            .border(
                width = 0.5.dp,
                color = if (isDark) Color(0x35FFFFFF) else Color(0x20000000),
                shape = dialogShape,
            )
    } else {
        modifier
            .widthIn(min = 270.dp, max = 320.dp)
            .clip(dialogShape)
            .background(if (isDark) Color(0xFF252528) else Color(0xFFFFFFFF))
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = dialogModifier,
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
                if (icon != null) {
                    Box(modifier = Modifier.padding(bottom = 6.dp)) {
                        icon()
                    }
                }
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
                        Box(modifier = Modifier.weight(1f, fill = false)) {
                            text()
                        }
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
                            LocalContentColor provides MaterialTheme.colorScheme.primary,
                        ) {
                            ProvideTextStyle(
                                value = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.primary,
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
                        LocalContentColor provides MaterialTheme.colorScheme.primary,
                    ) {
                        ProvideTextStyle(
                            value = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
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
            .background(color),
    )
}

/**
 * Authentic Apple iOS Circular Checkbox / Multi-Select Indicator (`UICellAccessory.multiselect`)
 * Features smooth spring animation, specular rim lighting, and SF-Symbols rounded-cap stroke geometry.
 */
@Composable
fun CupertinoCheckmark(
    checked: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    val isDark = isSystemInDarkTheme()
    val progress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 520f,
        ),
        label = "cupertino_checkmark_progress",
    )
    val stateAlpha = if (enabled) 1f else 0.42f
    val clickModifier = if (onCheckedChange != null) {
        Modifier
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onCheckedChange(!checked) },
            )
            .padding(6.dp)
    } else {
        Modifier
    }

    Canvas(
        modifier = modifier
            .then(clickModifier)
            .size(22.dp),
    ) {
        val w = size.width
        val h = size.height
        val radius = (minOf(w, h) / 2f) - 1.dp.toPx()
        val center = Offset(w / 2f, h / 2f)

        // 1. Unchecked circular ring + subtle inner glass fill
        val uncheckedAlpha = (1f - progress).coerceIn(0f, 1f) * stateAlpha
        if (uncheckedAlpha > 0.01f) {
            drawCircle(
                color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.05f * uncheckedAlpha),
                radius = radius,
                center = center,
            )
            drawCircle(
                color = (if (isDark) Color(0xFF98989D) else Color(0xFF8E8E93)).copy(alpha = 0.62f * uncheckedAlpha),
                radius = radius,
                center = center,
                style = Stroke(width = 1.6.dp.toPx()),
            )
        }

        // 2. Checked Apple badge + SF Symbols rounded checkmark
        val checkedAlpha = progress.coerceIn(0f, 1f) * stateAlpha
        if (checkedAlpha > 0.01f) {
            val badgeScale = 0.80f + 0.20f * progress
            scale(scale = badgeScale, pivot = center) {
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            tint.copy(alpha = checkedAlpha),
                            tint.copy(alpha = 0.90f * checkedAlpha),
                        ),
                    ),
                    radius = radius + 0.5.dp.toPx(),
                    center = center,
                )
                // Specular top rim
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.34f * checkedAlpha),
                            Color.Transparent,
                        ),
                    ),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 0.8.dp.toPx()),
                )

                val checkPath = Path().apply {
                    moveTo(w * 0.28f, h * 0.52f)
                    lineTo(w * 0.44f, h * 0.68f)
                    lineTo(w * 0.73f, h * 0.35f)
                }
                drawPath(
                    path = checkPath,
                    color = Color.White.copy(alpha = checkedAlpha),
                    style = Stroke(
                        width = 2.05.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    }
}

/**
 * Authentic Apple iOS 3-State Checkbox / Filter Indicator
 * Supports Off (unchecked ring), On (Apple Blue checkmark), and Exclude/Indeterminate (Apple Red X or Blue Dash).
 */
@Composable
fun CupertinoTriStateCheckmark(
    state: ToggleableState,
    modifier: Modifier = Modifier,
    excludeIsCross: Boolean = true,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val isDark = isSystemInDarkTheme()
    val blueTint = MaterialTheme.colorScheme.primary
    val redTint = if (isDark) Color(0xFFFF453A) else Color(0xFFFF3B30)
    val isActive = state != ToggleableState.Off
    val progress by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 520f,
        ),
        label = "cupertino_tristate_progress",
    )
    val stateAlpha = if (enabled) 1f else 0.42f
    val clickModifier = if (onClick != null) {
        Modifier
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(6.dp)
    } else {
        Modifier
    }

    Canvas(
        modifier = modifier
            .then(clickModifier)
            .size(22.dp),
    ) {
        val w = size.width
        val h = size.height
        val radius = (minOf(w, h) / 2f) - 1.dp.toPx()
        val center = Offset(w / 2f, h / 2f)

        val uncheckedAlpha = (1f - progress).coerceIn(0f, 1f) * stateAlpha
        if (uncheckedAlpha > 0.01f) {
            drawCircle(
                color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.05f * uncheckedAlpha),
                radius = radius,
                center = center,
            )
            drawCircle(
                color = (if (isDark) Color(0xFF98989D) else Color(0xFF8E8E93)).copy(alpha = 0.62f * uncheckedAlpha),
                radius = radius,
                center = center,
                style = Stroke(width = 1.6.dp.toPx()),
            )
        }

        val activeAlpha = progress.coerceIn(0f, 1f) * stateAlpha
        if (activeAlpha > 0.01f) {
            val badgeColor = if (state == ToggleableState.Indeterminate && excludeIsCross) redTint else blueTint
            val badgeScale = 0.80f + 0.20f * progress
            scale(scale = badgeScale, pivot = center) {
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            badgeColor.copy(alpha = activeAlpha),
                            badgeColor.copy(alpha = 0.90f * activeAlpha),
                        ),
                    ),
                    radius = radius + 0.5.dp.toPx(),
                    center = center,
                )
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.34f * activeAlpha),
                            Color.Transparent,
                        ),
                    ),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 0.8.dp.toPx()),
                )

                val strokeStyle = Stroke(
                    width = 2.05.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                )
                if (state == ToggleableState.On) {
                    val checkPath = Path().apply {
                        moveTo(w * 0.28f, h * 0.52f)
                        lineTo(w * 0.44f, h * 0.68f)
                        lineTo(w * 0.73f, h * 0.35f)
                    }
                    drawPath(
                        path = checkPath,
                        color = Color.White.copy(alpha = activeAlpha),
                        style = strokeStyle,
                    )
                } else if (excludeIsCross) {
                    val crossPath = Path().apply {
                        moveTo(w * 0.33f, h * 0.33f)
                        lineTo(w * 0.67f, h * 0.67f)
                        moveTo(w * 0.67f, h * 0.33f)
                        lineTo(w * 0.33f, h * 0.67f)
                    }
                    drawPath(
                        path = crossPath,
                        color = Color.White.copy(alpha = activeAlpha),
                        style = strokeStyle,
                    )
                } else {
                    drawLine(
                        color = Color.White.copy(alpha = activeAlpha),
                        start = Offset(w * 0.30f, h * 0.50f),
                        end = Offset(w * 0.70f, h * 0.50f),
                        strokeWidth = 2.2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

/**
 * Authentic Apple iOS Single-Selection List Checkmark (`UICellAccessory.checkmark`)
 * Draws a crisp SF-Symbols rounded blue checkmark without an outer circle for single-choice lists.
 */
@Composable
fun CupertinoListCheckmark(
    selected: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = 550f,
        ),
        label = "cupertino_list_checkmark",
    )
    if (progress > 0.01f) {
        Canvas(
            modifier = modifier.size(20.dp),
        ) {
            val w = size.width
            val h = size.height
            scale(scale = 0.8f + 0.2f * progress) {
                val checkPath = Path().apply {
                    moveTo(w * 0.18f, h * 0.54f)
                    lineTo(w * 0.42f, h * 0.78f)
                    lineTo(w * 0.84f, h * 0.24f)
                }
                drawPath(
                    path = checkPath,
                    color = tint.copy(alpha = progress.coerceIn(0f, 1f)),
                    style = Stroke(
                        width = 2.2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    } else {
        Spacer(modifier = modifier.size(20.dp))
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
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val sheetShape = RoundedCornerShape(20.dp)
    val cardModifier = if (isGlass) {
        modifier
            .fillMaxWidth()
            .liquidGlassSurface(
                shape = sheetShape,
                blurRadius = 18.dp,
                refractionHeight = 18.dp,
                refractionAmount = 26.dp,
                tintColor = Color.Unspecified,
                surfaceColor = Color.Unspecified,
            )
            .border(
                width = 0.5.dp,
                color = if (isDark) Color(0x35FFFFFF) else Color(0x20000000),
                shape = sheetShape,
            )
    } else {
        modifier
            .fillMaxWidth()
            .clip(sheetShape)
            .background(if (isDark) Color(0xFF222226) else Color(0xFFF9F9F9))
    }

    Column(
        modifier = cardModifier,
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
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val pillShape = RoundedCornerShape(20.dp)
    val pillModifier = if (isGlass) {
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .liquidGlassSurface(
                shape = pillShape,
                blurRadius = 18.dp,
                refractionHeight = 18.dp,
                refractionAmount = 26.dp,
                tintColor = Color.Unspecified,
                surfaceColor = Color.Unspecified,
            )
            .border(
                width = 0.5.dp,
                color = if (isDark) Color(0x35FFFFFF) else Color(0x20000000),
                shape = pillShape,
            )
    } else {
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(pillShape)
            .background(if (isDark) Color(0xFF222226) else Color(0xFFFFFFFF))
    }

    Box(
        modifier = pillModifier
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * iOS Authentic Inset Grouped Squircle Icon Badge
 * 30dp continuous squircle container with vibrant background and centered white icon.
 */
@Composable
fun CupertinoIconBadge(
    icon: Any,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 30.dp,
    iconSize: Dp = 18.dp,
    iconTint: Color = Color.White,
) {
    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(RoundedRectangle(7.dp, RoundedCornerStyle.Continuous))
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize),
                )
            }
            is Painter -> {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}

/**
 * iOS Authentic Segmented Control with sliding pill indicator
 */
@Composable
fun CupertinoSegmentedControl(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<Pair<String, Int?>>,
    modifier: Modifier = Modifier,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val trackBg = if (isDark) Color(0x282C2C2E) else Color(0x38E5E5EA)
    val thumbBg = MaterialTheme.colorScheme.primary
    val textSelected = MaterialTheme.colorScheme.onPrimary
    val textUnselected = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)

    BoxWithConstraints(
        modifier = modifier
            .height(36.dp)
            .then(
                if (isGlass) {
                    Modifier.liquidGlassSurface(
                        shape = Capsule(),
                        blurRadius = 6.dp,
                        refractionHeight = 14.dp,
                        refractionAmount = 18.dp,
                        tintColor = trackBg,
                    )
                } else {
                    Modifier
                        .clip(Capsule())
                        .background(trackBg)
                },
            )
            .padding(2.dp),
    ) {
        val tabCount = tabs.size.coerceAtLeast(1)
        val tabWidth = maxWidth / tabCount
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * selectedTabIndex,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
            label = "tab_indicator",
        )

        // Sliding thumb pill matching theme
        Box(
            modifier = Modifier
                .width(tabWidth)
                .fillMaxHeight()
                .offset(x = indicatorOffset)
                .clip(Capsule())
                .background(thumbBg),
        )

        // Tab titles
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, (title, badgeCount) ->
                val selected = index == selectedTabIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(Capsule())
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            onTabSelected(index)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 2.dp),
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 12.5.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                letterSpacing = (-0.2).sp,
                            ),
                            color = if (selected) textSelected else textUnselected,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                        )
                        if (badgeCount != null && badgeCount > 0) {
                            Spacer(Modifier.width(3.dp))
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(
                                        if (selected) {
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.24f)
                                        } else {
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                        },
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                            ) {
                                Text(
                                    text = "$badgeCount",
                                    color = if (selected) textSelected else textUnselected,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Authentic Apple iOS 26 Inset Grouped Table Card for Options Selection
 * Replaces walls of Material 3 chips with clean, elegant rows and CupertinoCheckmarks.
 */
@Composable
fun <T> CupertinoGroupedSelectionCard(
    title: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    labelProvider: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    iconProvider: (@Composable (T) -> Unit)? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val shape = RoundedRectangle(18.dp, RoundedCornerStyle.Continuous)
    val cardBg = if (isGlass) {
        if (isDark) Color(0x331C1C1E) else Color(0x44FFFFFF)
    } else {
        if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    }

    val cardModifier = if (isGlass) {
        Modifier
            .fillMaxWidth()
            .liquidGlassSurface(
                shape = shape,
                blurRadius = 6.dp,
                refractionHeight = 16.dp,
                refractionAmount = 24.dp,
                tintColor = cardBg,
            )
    } else {
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(cardBg)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White.copy(alpha = 0.72f) else Color.Black.copy(alpha = 0.62f),
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
            )
        }
        Column(
            modifier = cardModifier,
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = option == selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable { onSelect(option) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (iconProvider != null) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            iconProvider(option)
                        }
                    }
                    Text(
                        text = labelProvider(option),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isDark) Color.White else Color(0xFF1C1C1E),
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    if (isSelected) {
                        CupertinoCheckmark(checked = true, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                if (index < options.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = if (iconProvider != null) 58.dp else 16.dp),
                        thickness = 0.5.dp,
                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.10f),
                    )
                }
            }
        }
    }
}

/**
 * Authentic Apple iOS 26 Inset Grouped Table Card for Grouped Settings
 */
@Composable
fun CupertinoGroupedCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val shape = RoundedRectangle(18.dp, RoundedCornerStyle.Continuous)
    val cardBg = if (isGlass) {
        if (isDark) Color(0x331C1C1E) else Color(0x44FFFFFF)
    } else {
        if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    }

    val cardModifier = if (isGlass) {
        Modifier
            .fillMaxWidth()
            .liquidGlassSurface(
                shape = shape,
                blurRadius = 6.dp,
                refractionHeight = 16.dp,
                refractionAmount = 24.dp,
                tintColor = cardBg,
            )
    } else {
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(cardBg)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        if (!title.isNullOrEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White.copy(alpha = 0.72f) else Color.Black.copy(alpha = 0.62f),
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
            )
        }
        Column(
            modifier = cardModifier,
            content = content,
        )
    }
}

/**
 * Standard iOS Settings Row with Label and Action/Widget
 */
@Composable
fun CupertinoGroupedRow(
    label: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    widget: (@Composable () -> Unit)? = null,
) {
    val isDark = isSystemInDarkTheme()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isDark) Color.White else Color(0xFF1C1C1E),
                ),
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.68f) else Color.Black.copy(alpha = 0.60f),
                    ),
                )
            }
        }
        if (widget != null) {
            widget()
        }
    }
}

/**
 * Standard iOS Settings Toggle Switch Row powered by LiquidToggle
 */
@Composable
fun CupertinoGroupedToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    subtitle: String? = null,
) {
    CupertinoGroupedRow(
        label = label,
        modifier = modifier,
        subtitle = subtitle,
        onClick = if (enabled) {
            { onCheckedChange(!checked) }
        } else {
            null
        },
        widget = {
            CupertinoSwitch(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled,
            )
        },
    )
}

/**
 * Inset Divider for Grouped Settings Cards
 */
@Composable
fun CupertinoGroupedDivider(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    HorizontalDivider(
        modifier = modifier.padding(start = 16.dp),
        thickness = 0.5.dp,
        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.10f),
    )
}

/**
 * Authentic Apple iOS 26 Segmented Control Setting Row
 */
@Composable
fun CupertinoGroupedSegmentedControl(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White.copy(alpha = 0.72f) else Color.Black.copy(alpha = 0.62f),
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
        )
        CupertinoSegmentedControl(
            selectedTabIndex = selectedIndex.coerceIn(0, (options.size - 1).coerceAtLeast(0)),
            onTabSelected = onSelect,
            tabs = options.map { it to null },
        )
    }
}

val CupertinoMoreHoriz: ImageVector by lazy {
    ImageVector.Builder(
        name = "Filled.MoreHoriz",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6.0f, 10.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
            reflectiveCurveToRelative(2.0f, -0.9f, 2.0f, -2.0f)
            reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(18.0f, 10.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
            reflectiveCurveToRelative(2.0f, -0.9f, 2.0f, -2.0f)
            reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(12.0f, 10.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
            reflectiveCurveToRelative(2.0f, -0.9f, 2.0f, -2.0f)
            reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
            close()
        }
    }.build()
}

val Icons.Filled.MoreHoriz: ImageVector
    get() = CupertinoMoreHoriz

val Icons.Outlined.MoreHoriz: ImageVector
    get() = CupertinoMoreHoriz
