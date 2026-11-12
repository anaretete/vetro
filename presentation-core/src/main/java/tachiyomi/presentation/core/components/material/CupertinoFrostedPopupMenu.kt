package tachiyomi.presentation.core.components.material

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/**
 * Enables hardware window-level blur behind on Android 12+ (API 31+).
 */
@Composable
fun EnableWindowBlurBehind(radiusDp: Dp = 28.dp) {
    val useNativeGlass = LocalLiquidGlassConfig.current.enabled && supportsNativeGlassMenu
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val view = LocalView.current
        val density = LocalDensity.current
        DisposableEffect(view, useNativeGlass) {
            val root = view.rootView
            val params = root.layoutParams as? WindowManager.LayoutParams
            if (params != null) {
                if (useNativeGlass) {
                    params.flags = params.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
                    params.blurBehindRadius = 0
                    if ((params.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND) != 0 && params.dimAmount > 0f) {
                        params.dimAmount = 0f
                    }
                } else {
                    params.flags = params.flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                    params.blurBehindRadius = with(density) { radiusDp.roundToPx() }
                }
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
 * Frosted liquid glass popup for menus whose rows are arbitrary composables.
 *
 * Mirrors the rit3zh/expo-glass-custom-menu unfold: the glass starts as the bounds of the anchor (the
 * layout this popup is declared in, e.g. a `Box` wrapping a `⋯` button), widens, then drops down to
 * full size while the rows fade in, and folds back into the anchor on dismiss. Like the native menu it
 * aligns to the anchor's leading or trailing edge depending on which half of the screen the anchor is
 * in, starts at the anchor's top edge, and flips upward when there is not enough room below.
 *
 * [offset] is kept for source compatibility and ignored: placement is always derived from the anchor.
 */
@Composable
fun CupertinoFrostedPopup(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") offset: DpOffset = DpOffset(0.dp, 4.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val unfoldX = remember { Animatable(0f) }
    val unfoldY = remember { Animatable(0f) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        if (expanded) {
            visible = true
            coroutineScope {
                launch { unfoldX.animateTo(1f, UnfoldWidthSpring) }
                launch { unfoldY.animateTo(1f, UnfoldHeightSpring) }
            }
        } else if (visible) {
            coroutineScope {
                launch { unfoldY.animateTo(0f, FoldSpring) }
                launch { unfoldX.animateTo(0f, FoldSpring) }
            }
            visible = false
        }
    }

    if (!expanded && !visible) return

    val isDark = isSystemInDarkTheme()
    val density = LocalDensity.current
    val popupTextColor = if (isDark) Color(0xFFF5F5F7) else Color(0xFF1C1C1E)
    val surfaceColor = if (isDark) Color(0x9922222A) else Color(0xCCFAFBFD)
    val borderColor = if (isDark) Color(0x35FFFFFF) else Color(0x1F000000)
    val cornerPx = with(density) { 20.dp.toPx() }
    val borderPx = with(density) { 0.75.dp.toPx() }
    val geometry = remember { UnfoldGeometry() }
    val positionProvider = remember(density) {
        GlassPopupPositionProvider(with(density) { 16.dp.roundToPx() }, geometry)
    }
    var popupSize by remember { mutableStateOf(IntSize.Zero) }
    val useNativeGlass = supportsNativeGlassMenu

    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        val currentFrame = if (popupSize.width > 0 && popupSize.height > 0) {
            geometry.frame(unfoldX.value, unfoldY.value, Size(popupSize.width.toFloat(), popupSize.height.toFloat()))
        } else {
            null
        }
        val currentRadius = if (currentFrame != null) {
            geometry.radius(unfoldY.value, currentFrame, cornerPx)
        } else {
            cornerPx
        }
        Box(
            modifier = modifier
                .widthIn(min = 210.dp, max = 290.dp)
                .width(IntrinsicSize.Max)
                .onSizeChanged { popupSize = it },
        ) {
            if (useNativeGlass) {
                NativeGlassSurface(
                    modifier = Modifier.matchParentSize(),
                    cornerRadius = 20.dp,
                    topCornersOnly = false,
                    materialProgress = unfoldY.value,
                    customFrame = currentFrame,
                    customCornerRadiusPx = currentRadius,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val frame = geometry.frame(unfoldX.value, unfoldY.value, size)
                        shape = UnfoldShape(frame, geometry.radius(unfoldY.value, frame, cornerPx))
                        clip = true
                    }
                    .then(
                        if (!useNativeGlass) {
                            Modifier.drawWithContent {
                                val frame = geometry.frame(unfoldX.value, unfoldY.value, size)
                                val radius = geometry.radius(unfoldY.value, frame, cornerPx)
                                drawRect(surfaceColor)
                                drawContent()
                                if (frame.width > borderPx && frame.height > borderPx) {
                                    drawRoundRect(
                                        color = borderColor,
                                        topLeft = Offset(frame.left + borderPx / 2f, frame.top + borderPx / 2f),
                                        size = Size(frame.width - borderPx, frame.height - borderPx),
                                        cornerRadius = CornerRadius(radius),
                                        style = Stroke(width = borderPx),
                                    )
                                }
                            }
                        } else {
                            Modifier
                        },
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 5.dp)
                    .graphicsLayer {
                        alpha = ((unfoldY.value - 0.35f) / 0.65f).coerceIn(0f, 1f)
                    },
            ) {
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
}

private val UnfoldWidthSpring = spring<Float>(dampingRatio = 0.82f, stiffness = 520f)
private val UnfoldHeightSpring = spring<Float>(dampingRatio = 0.86f, stiffness = 300f)
private val FoldSpring = spring<Float>(dampingRatio = 1f, stiffness = 900f)

/** Anchor bounds in popup-local pixels, written during positioning and read while drawing. */
private class UnfoldGeometry {
    var source: Rect = Rect.Zero

    fun frame(progressX: Float, progressY: Float, size: Size): Rect {
        val w = size.width
        val h = size.height
        val left = source.left.coerceIn(0f, w)
        val right = source.right.coerceIn(left, w)
        val top = source.top.coerceIn(0f, h)
        val bottom = source.bottom.coerceIn(top, h)
        val x = progressX.coerceIn(0f, 1f)
        val y = progressY.coerceIn(0f, 1f)
        return Rect(
            left = lerp(left, 0f, x),
            top = lerp(top, 0f, y),
            right = lerp(right, w, x),
            bottom = lerp(bottom, h, y),
        )
    }

    fun radius(progress: Float, frame: Rect, endRadius: Float): Float {
        val capsule = min(frame.width, frame.height) / 2f
        return min(lerp(capsule, endRadius, progress.coerceIn(0f, 1f)), capsule)
    }
}

private class UnfoldShape(private val frame: Rect, private val radius: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Rounded(RoundRect(frame, CornerRadius(radius)))
}

/** Same placement rules as GlassMenuSession in rit3zh/expo-glass-custom-menu. */
private class GlassPopupPositionProvider(
    private val marginPx: Int,
    private val geometry: UnfoldGeometry,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val width = popupContentSize.width
        val height = popupContentSize.height
        val opensFromEnd = anchorBounds.center.x > windowSize.width / 2
        val maxX = max(marginPx, windowSize.width - width - marginPx)
        val x = (if (opensFromEnd) anchorBounds.right - width else anchorBounds.left).coerceIn(marginPx, maxX)
        val roomBelow = windowSize.height - anchorBounds.top
        val roomAbove = anchorBounds.bottom
        val opensAbove = roomBelow < height && roomAbove > roomBelow
        val maxY = max(marginPx, windowSize.height - height - marginPx)
        val y = (if (opensAbove) anchorBounds.bottom - height else anchorBounds.top).coerceIn(marginPx, maxY)
        geometry.source = Rect(
            left = (anchorBounds.left - x).toFloat(),
            top = (anchorBounds.top - y).toFloat(),
            right = (anchorBounds.right - x).toFloat(),
            bottom = (anchorBounds.bottom - y).toFloat(),
        )
        return IntOffset(x, y)
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
 * A single-choice liquid glass picker powered by rit3zh/expo-glass-custom-menu.
 *
 * [modifier] positions the invisible anchor the menu unfolds from. Put this inside a `Box` that wraps
 * the control that opens it (e.g. the `›` chevron) and pass `Modifier.matchParentSize()`, declaring it
 * before the control so the control keeps receiving touches.
 */
@Composable
fun <T> CupertinoFrostedPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    entries: Map<out T, String>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") offset: DpOffset = DpOffset(0.dp, 4.dp),
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val currentOnSelect by rememberUpdatedState(onSelect)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    if (isGlass && supportsNativeGlassMenu) {
        val items = remember(entries, selectedValue) {
            entries.map { (key, label) ->
                GlassMenuItemRecord(
                    id = key.toString(),
                    title = label,
                    checked = if (key == selectedValue) true else null,
                    onClick = {
                        currentOnSelect(key)
                        currentOnDismissRequest()
                    },
                )
            }
        }
        GlassMenuAnchor(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            items = items,
            modifier = modifier,
        )
    } else {
        CupertinoFrostedPopup(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
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
                    } else {
                        null
                    },
                )
                if (index < entryList.lastIndex) {
                    CupertinoGlassMenuSeparator()
                }
            }
        }
    }
}
