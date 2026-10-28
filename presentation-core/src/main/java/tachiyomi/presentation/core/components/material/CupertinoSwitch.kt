package tachiyomi.presentation.core.components.material

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidToggle
import com.kyant.shapes.Capsule

/**
 * Authentic Apple iOS Cupertino Switch.
 * Dispatches to LiquidToggle when Liquid Glass is enabled, and SolidCupertinoSwitch when disabled.
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop? = null,
    customActiveColor: Color? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val currentChecked by rememberUpdatedState(checked)
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val haptics = rememberCupertinoHaptics()

    val isDark = isSystemInDarkTheme()
    val cupertinoGreen = if (isDark) Color(0xFF30D158) else Color(0xFF34C759)
    val isCupertinoTheme = MaterialTheme.colorScheme.primary == Color(0xFF007AFF) || MaterialTheme.colorScheme.primary == Color(0xFF0A84FF)
    val activeColor = customActiveColor ?: if (isCupertinoTheme) cupertinoGreen else MaterialTheme.colorScheme.primary

    if (isGlass) {
        val fallbackBackdrop = rememberLayerBackdrop()
        val effectiveBackdrop = backdrop ?: LocalLayerBackdrop.current ?: fallbackBackdrop

        LiquidToggle(
            selected = { currentChecked },
            onSelect = { newValue ->
                if (enabled) {
                    haptics.toggle()
                    currentOnCheckedChange?.invoke(newValue)
                }
            },
            backdrop = effectiveBackdrop,
            modifier = modifier.alpha(if (enabled) 1f else 0.38f),
            customAccentColor = activeColor,
        )
    } else {
        SolidCupertinoSwitch(
            checked = currentChecked,
            onCheckedChange = { newValue ->
                if (enabled) {
                    haptics.toggle()
                    currentOnCheckedChange?.invoke(newValue)
                }
            },
            modifier = modifier,
            enabled = enabled,
            customActiveColor = activeColor,
        )
    }
}

/**
 * Pixel-perfect Apple iOS Solid Switch (51x31dp track, 27dp thumb).
 * Used when Liquid Glass is disabled for a clean, non-glass Cupertino experience.
 */
@Composable
fun SolidCupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    customActiveColor: Color? = null,
) {
    val isDark = isSystemInDarkTheme()
    val cupertinoGreen = if (isDark) Color(0xFF30D158) else Color(0xFF34C759)
    val isCupertinoTheme = MaterialTheme.colorScheme.primary == Color(0xFF007AFF) || MaterialTheme.colorScheme.primary == Color(0xFF0A84FF)
    val activeColor = customActiveColor ?: if (isCupertinoTheme) cupertinoGreen else MaterialTheme.colorScheme.primary
    val inactiveColor = if (isDark) Color(0xFF39393D) else Color(0xFFE9E9EA)

    val trackColor by animateColorAsState(
        targetValue = if (checked) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 200),
        label = "switchTrackColor",
    )

    var isPressed by remember { mutableStateOf(false) }

    // Dimensions: 51dp width x 31dp height.
    // Thumb: 27dp diameter, 2dp padding on left and right.
    // Travel: 51 - 27 - 2 - 2 = 20dp.
    val targetOffset = if (checked) 22.dp else 2.dp
    val thumbOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioLowBouncy,
        ),
        label = "switchThumbOffset",
    )

    val thumbWidth by animateDpAsState(
        targetValue = if (isPressed) 31.dp else 27.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "switchThumbWidth",
    )

    Box(
        modifier = modifier
            .size(51.dp, 31.dp)
            .alpha(if (enabled) 1f else 0.38f)
            .semantics { role = Role.Switch }
            .clip(Capsule())
            .background(trackColor)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onCheckedChange?.invoke(!checked)
                    },
                )
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectHorizontalDragGestures(
                    onDragStart = { isPressed = true },
                    onDragEnd = { isPressed = false },
                    onDragCancel = { isPressed = false },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount > 8f && !checked) {
                            onCheckedChange?.invoke(true)
                        } else if (dragAmount < -8f && checked) {
                            onCheckedChange?.invoke(false)
                        }
                    },
                )
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(width = thumbWidth, height = 27.dp)
                .shadow(elevation = 2.5.dp, shape = Capsule())
                .clip(Capsule())
                .background(Color.White),
        )
    }
}

/**
 * Universal drop-in replacement for Material 3 Switch with automatic Apple Cupertino styling.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    backdrop: Backdrop? = null,
) {
    CupertinoSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        backdrop = backdrop,
    )
}
