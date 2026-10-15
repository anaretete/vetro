package tachiyomi.presentation.core.components.material

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kyant.shapes.Capsule
import kotlin.math.roundToInt

/**
 * Authentic Apple iOS 26 Cupertino Liquid Glass Switch (UISwitch standard).
 *
 * Dimensions: 51dp × 31dp stadium pill track.
 * Active (checked): Vibrant Apple Green (#34C759 light / #30D158 dark).
 * Inactive (unchecked): Translucent slate/glass (#E9E9EA light / #39393D dark).
 * Thumb: 27dp circular pure white disc with drop shadow and AGSL liquid glass refraction.
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isDark = isSystemInDarkTheme()
    val haptic = LocalHapticFeedback.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr

    val activeTrackColor = if (isDark) Color(0xFF30D158) else Color(0xFF34C759)
    val inactiveTrackColor = if (isDark) Color(0xFF39393D) else Color(0xFFE5E5EA)

    val trackColor by animateColorAsState(
        targetValue = if (checked) activeTrackColor else inactiveTrackColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "CupertinoSwitchTrackColor",
    )

    val thumbTargetOffset = if (checked) 22.dp else 2.dp
    val thumbOffset by animateDpAsState(
        targetValue = thumbTargetOffset,
        animationSpec = spring(
            dampingRatio = 0.78f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "CupertinoSwitchThumbOffset",
    )

    val density = LocalDensity.current
    val maxDragDistancePx = with(density) { 20.dp.toPx() }
    var dragDeltaPx by remember { mutableFloatStateOf(0f) }

    val draggableState = rememberDraggableState { delta ->
        val adjustedDelta = if (isLtr) delta else -delta
        dragDeltaPx = (dragDeltaPx + adjustedDelta).coerceIn(
            if (checked) -maxDragDistancePx else 0f,
            if (checked) 0f else maxDragDistancePx,
        )
    }

    Box(
        modifier = modifier
            .size(width = 51.dp, height = 31.dp)
            .semantics { role = Role.Switch }
            .clip(Capsule())
            .background(trackColor)
            .liquidGlassSurface(
                shape = Capsule(),
                blurRadius = 6.dp,
                refractionHeight = 8.dp,
                refractionAmount = 14.dp,
                tintColor = if (checked) activeTrackColor.copy(alpha = 0.35f) else Color.Transparent,
                isInteractive = false,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    onCheckedChange?.invoke(!checked)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
            )
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = enabled,
                onDragStopped = {
                    val shouldToggle = if (checked) {
                        dragDeltaPx < -maxDragDistancePx / 2
                    } else {
                        dragDeltaPx > maxDragDistancePx / 2
                    }
                    if (shouldToggle) {
                        onCheckedChange?.invoke(!checked)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    dragDeltaPx = 0f
                },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        val totalOffsetDp = thumbOffset + with(density) { dragDeltaPx.toDp() }
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = with(density) {
                            if (isLtr) totalOffsetDp.roundToPx() else -totalOffsetDp.roundToPx()
                        },
                        y = 0,
                    )
                }
                .shadow(
                    elevation = 2.5.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.35f),
                )
                .size(27.dp)
                .clip(CircleShape)
                .background(Color.White)
                .liquidGlassSurface(
                    shape = CircleShape,
                    blurRadius = 4.dp,
                    refractionHeight = 6.dp,
                    refractionAmount = 10.dp,
                    isInteractive = false,
                ),
        )
    }
}

/**
 * Global drop-in replacement for Material 3 Switch with automatic Cupertino Liquid Glass styling.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        CupertinoSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
            interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        )
    } else {
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            thumbContent = thumbContent,
            enabled = enabled,
            colors = colors,
            interactionSource = interactionSource,
        )
    }
}
