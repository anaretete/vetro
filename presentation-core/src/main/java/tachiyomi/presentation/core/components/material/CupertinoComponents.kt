package tachiyomi.presentation.core.components.material

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
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
    val dialogBg = if (isGlass) {
        if (isDark) Color(0xFF252528).copy(alpha = 0.70f) else Color(0xFFFFFFFF).copy(alpha = 0.85f)
    } else {
        if (isDark) Color(0xFF252528) else Color(0xFFFFFFFF)
    }

    val dialogModifier = if (isGlass) {
        modifier
            .widthIn(min = 270.dp, max = 320.dp)
            .liquidGlassSurface(
                shape = RoundedCornerShape(20.dp),
                blurRadius = 24.dp,
                refractionHeight = 24.dp,
                refractionAmount = 32.dp,
                tintColor = dialogBg,
            )
    } else {
        modifier
            .widthIn(min = 270.dp, max = 320.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(dialogBg)
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
            .background(color),
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
                .clip(CircleShape)
                .background(tint),
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
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isGlass) {
        if (isDark) Color(0xFF222226).copy(alpha = 0.75f) else Color(0xFFF9F9F9).copy(alpha = 0.85f)
    } else {
        if (isDark) Color(0xFF222226) else Color(0xFFF9F9F9)
    }

    val cardModifier = if (isGlass) {
        modifier
            .fillMaxWidth()
            .liquidGlassSurface(
                shape = RoundedCornerShape(20.dp),
                blurRadius = 22.dp,
                refractionHeight = 22.dp,
                refractionAmount = 30.dp,
                tintColor = cardBg,
            )
    } else {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
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
    val pillBg = if (isGlass) {
        if (isDark) Color(0xFF222226).copy(alpha = 0.80f) else Color(0xFFFFFFFF).copy(alpha = 0.90f)
    } else {
        if (isDark) Color(0xFF222226) else Color(0xFFFFFFFF)
    }

    val pillModifier = if (isGlass) {
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .liquidGlassSurface(
                shape = RoundedCornerShape(20.dp),
                blurRadius = 20.dp,
                refractionHeight = 20.dp,
                refractionAmount = 28.dp,
                tintColor = pillBg,
            )
    } else {
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(pillBg)
    }

    Box(
        modifier = pillModifier
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
    val isDark = isSystemInDarkTheme()
    val trackBg = if (isDark) Color(0x33767680) else Color(0x1F767680)
    val thumbBg = if (isDark) Color(0xFF636366) else Color.White
    val textSelected = if (isDark) Color.White else Color(0xFF000000)
    val textUnselected = if (isDark) Color(0xFF8E8E93) else Color(0xFF636366)

    BoxWithConstraints(
        modifier = modifier
            .height(36.dp)
            .clip(Capsule())
            .background(trackBg)
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

        // Sliding thumb pill
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
                                            Color(0xFFFF3B30)
                                        } else {
                                            (if (isDark) Color.White else Color.Black).copy(alpha = 0.15f)
                                        },
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                            ) {
                                Text(
                                    text = "$badgeCount",
                                    color = if (selected) Color.White else textUnselected,
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
    val cardBg = if (isGlass) {
        if (isDark) Color(0x661C1C1E) else Color(0x77FFFFFF)
    } else {
        if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    }

    val cardModifier = if (isGlass) {
        Modifier
            .fillMaxWidth()
            .liquidGlassSurface(
                shape = RoundedRectangle(18.dp, RoundedCornerStyle.Continuous),
                blurRadius = 16.dp,
                refractionHeight = 16.dp,
                refractionAmount = 24.dp,
                tintColor = cardBg,
            )
    } else {
        Modifier
            .fillMaxWidth()
            .clip(RoundedRectangle(18.dp, RoundedCornerStyle.Continuous))
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
                    color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6C6C70),
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
                        .height(52.dp)
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
                        CupertinoCheckmark(checked = true, tint = Color(0xFF0A84FF))
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
    val cardBg = if (isGlass) {
        if (isDark) Color(0x661C1C1E) else Color(0x77FFFFFF)
    } else {
        if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    }

    val cardModifier = if (isGlass) {
        Modifier
            .fillMaxWidth()
            .liquidGlassSurface(
                shape = RoundedRectangle(18.dp, RoundedCornerStyle.Continuous),
                blurRadius = 16.dp,
                refractionHeight = 16.dp,
                refractionAmount = 24.dp,
                tintColor = cardBg,
            )
    } else {
        Modifier
            .fillMaxWidth()
            .clip(RoundedRectangle(18.dp, RoundedCornerStyle.Continuous))
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
                    color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6C6C70),
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
                        color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6C6C70),
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
                color = if (isDark) Color(0xFF8E8E93) else Color(0xFF6C6C70),
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
