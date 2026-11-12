package tachiyomi.presentation.core.components.material

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

/**
 * Enables hardware window-level blur behind on Android 12+ (API 31+).
 */
@Composable
fun EnableWindowBlurBehind(radiusDp: Dp = 28.dp) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val view = LocalView.current
        val density = LocalDensity.current
        DisposableEffect(view) {
            val root = view.rootView
            val params = root.layoutParams as? WindowManager.LayoutParams
            if (params != null) {
                params.flags = params.flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                params.blurBehindRadius = with(density) { radiusDp.roundToPx() }
                val wm = view.context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                try {
                    wm?.updateViewLayout(root, params)
                } catch (_: Throwable) {
                }
            }
            onDispose {}
        }
    }
}

/**
 * An authentic Apple iOS 26 frosted liquid glass popup menu container based on rit3zh/expo-glass-custom-menu.
 */
@Composable
fun CupertinoFrostedPopup(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 4.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(18.dp)
    val popupTextColor = if (isDark) Color(0xFFF5F5F7) else Color(0xFF1C1C1E)
    val surfaceColor = if (isDark) Color(0xD9141418) else Color(0xEBFAFBFD)
    val borderColor = if (isDark) Color(0x35FFFFFF) else Color(0x1F000000)

    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = shape),
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            offset = offset,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
            modifier = modifier
                .widthIn(min = 210.dp, max = 290.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = shape,
                    ambientColor = if (isDark) Color(0x66000000) else Color(0x22000000),
                    spotColor = if (isDark) Color(0x88000000) else Color(0x33000000),
                )
                .border(
                    width = 0.75.dp,
                    color = borderColor,
                    shape = shape,
                )
                .background(
                    color = surfaceColor,
                    shape = shape,
                )
                .padding(vertical = 5.dp),
            shape = shape,
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
        ) {
            EnableWindowBlurBehind(radiusDp = 28.dp)
            CompositionLocalProvider(
                LocalContentColor provides popupTextColor,
            ) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.bodyLarge.copy(
                        color = popupTextColor,
                    ),
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * Authentic Apple iOS Liquid Glass Menu Item row as defined by rit3zh/expo-glass-custom-menu.
 */
@Composable
fun CupertinoGlassMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    destructive: Boolean = false,
    selected: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isDark = isSystemInDarkTheme()
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptics = rememberCupertinoHaptics()

    val pressedBgColor = if (isPressed) {
        if (isDark) Color(0x28FFFFFF) else Color(0x14000000)
    } else if (selected) {
        if (isDark) Color(0x18FFFFFF) else Color(0x0C000000)
    } else {
        Color.Transparent
    }

    val defaultTextColor = if (destructive) {
        if (isDark) Color(0xFFFF453A) else Color(0xFFFF3B30)
    } else if (selected) {
        MaterialTheme.colorScheme.primary
    } else if (isDark) {
        Color(0xFFF5F5F7)
    } else {
        Color(0xFF1C1C1E)
    }

    val effectiveTextColor = if (enabled) defaultTextColor else defaultTextColor.copy(alpha = 0.38f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(pressedBgColor)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
            ) {
                haptics.selection()
                onClick()
            }
            .padding(contentPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (leadingIcon != null) {
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides effectiveTextColor,
                        ) {
                            leadingIcon()
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides effectiveTextColor,
                    ) {
                        ProvideTextStyle(
                            value = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 15.5.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = effectiveTextColor,
                                letterSpacing = (-0.2).sp,
                            ),
                        ) {
                            text()
                        }
                    }
                    if (subtitle != null) {
                        CompositionLocalProvider(
                            LocalContentColor provides Color(0xFF8E8E93),
                        ) {
                            ProvideTextStyle(
                                value = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF8E8E93),
                                    lineHeight = 15.sp,
                                ),
                            ) {
                                subtitle()
                            }
                        }
                    }
                }
            }

            if (trailingIcon != null) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides if (destructive) effectiveTextColor else Color(0xFF8E8E93),
                    ) {
                        trailingIcon()
                    }
                }
            }
        }
    }
}

/**
 * Hairline divider between glass menu rows (rit3zh/expo-glass-custom-menu).
 */
@Composable
fun CupertinoGlassMenuSeparator(
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    HorizontalDivider(
        thickness = 0.5.dp,
        color = if (isDark) Color(0x1FFFFFFF) else Color(0x14000000),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
    )
}

/**
 * A frosted liquid glass popup with selectable options powered by rit3zh/expo-glass-custom-menu.
 */
@Composable
fun <T> CupertinoFrostedPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    entries: Map<out T, String>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 4.dp),
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    if (isGlass && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val items = remember(entries, selectedValue) {
            entries.map { (key, label) ->
                expo.modules.glassmenuandroid.records.GlassMenuItemRecord(
                    id = key.toString(),
                    title = label,
                    checked = if (key == selectedValue) true else null,
                    onClick = {
                        onSelect(key)
                        onDismissRequest()
                    },
                )
            }
        }

        var anchorView by remember { androidx.compose.runtime.mutableStateOf<expo.modules.glassmenuandroid.GlassMenuAndroidView?>(null) }

        androidx.compose.ui.viewinterop.AndroidView(
            modifier = modifier.size(32.dp),
            factory = { context ->
                expo.modules.glassmenuandroid.GlassMenuAndroidView(context).apply {
                    this.colorScheme = if (isDark) "dark" else "light"
                    this.items = items
                    this.onMenuOpenChange = { map ->
                        if (map["open"] == false) {
                            onDismissRequest()
                        }
                    }
                    anchorView = this
                }
            },
            update = { view ->
                view.colorScheme = if (isDark) "dark" else "light"
                view.items = items
                anchorView = view
            },
        )

        LaunchedEffect(expanded, anchorView) {
            val anchor = anchorView ?: return@LaunchedEffect
            if (expanded) {
                anchor.post { anchor.open() }
            } else {
                anchor.close()
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                anchorView?.close()
            }
        }
    } else {
        CupertinoFrostedPopup(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            offset = offset,
            modifier = modifier,
        ) {
            val entryList = remember(entries) { entries.toList() }
            entryList.forEachIndexed { index, (key, label) ->
                val isSelected = key == selectedValue
                CupertinoGlassMenuItem(
                    text = { Text(text = label) },
                    onClick = {
                        onSelect(key)
                        onDismissRequest()
                    },
                    selected = isSelected,
                    trailingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    } else null,
                )
                if (index < entryList.lastIndex) {
                    CupertinoGlassMenuSeparator()
                }
            }
        }
    }
}
