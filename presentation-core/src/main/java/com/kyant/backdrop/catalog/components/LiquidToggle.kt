package com.kyant.backdrop.catalog.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule

/**
 * Authentic Apple iOS Cupertino Liquid Toggle Switch (UISwitch standard).
 *
 * Dimensions: 51dp x 31dp stadium capsule pill.
 * Active state: Vibrant Apple Emerald Green (Dark: #30D158 / Light: #34C759).
 * Inactive state: Translucent Apple Gray (Dark: #39393D / Light: #E9E9EB).
 * Thumb: 27dp circular pure white disc with subtle drop shadow and liquid glass refraction.
 */
@Composable
fun LiquidToggle(
    selected: () -> Boolean,
    onSelect: (Boolean) -> Unit,
    backdrop: Backdrop? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val isDark = isSystemInDarkTheme()
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    val currentSelected = selected()
    val currentOnSelect by rememberUpdatedState(onSelect)

    val activeColor = if (isDark) Color(0xFF30D158) else Color(0xFF34C759)
    val inactiveColor = if (isDark) Color(0xFF39393D) else Color(0xFFE9E9EB)

    val trackColor by animateColorAsState(
        targetValue = if (currentSelected) activeColor else inactiveColor,
        animationSpec = spring(stiffness = 500f),
        label = "LiquidToggleTrackColor",
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isDragging by remember { mutableStateOf(false) }

    // Standard iOS dimensions
    val switchWidthDp = 51.dp
    val switchHeightDp = 31.dp
    val thumbBaseSizeDp = 27.dp
    val paddingDp = 2.dp

    // Subtle Apple thumb stretch when pressed or dragging
    val thumbWidthDp by animateDpAsState(
        targetValue = if (isPressed || isDragging) 31.dp else thumbBaseSizeDp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 600f),
        label = "LiquidToggleThumbWidth",
    )

    val minOffsetDp = paddingDp
    val maxOffsetDp = switchWidthDp - thumbWidthDp - paddingDp
    val travelDistanceDp = switchWidthDp - thumbBaseSizeDp - (paddingDp * 2)

    val baseTargetOffsetDp = if (currentSelected) 22.dp else minOffsetDp
    val animatedBaseOffsetDp by animateDpAsState(
        targetValue = baseTargetOffsetDp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 500f),
        label = "LiquidToggleThumbOffset",
    )

    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    val maxDragPx = with(density) { travelDistanceDp.toPx() }

    val draggableState = rememberDraggableState { delta ->
        val adjustedDelta = if (isLtr) delta else -delta
        dragOffsetPx = (dragOffsetPx + adjustedDelta).coerceIn(
            if (currentSelected) -maxDragPx else 0f,
            if (currentSelected) 0f else maxDragPx,
        )
    }

    val dragOffsetDp = with(density) { dragOffsetPx.toDp() }
    val effectiveOffsetDp = (if (isDragging) (if (currentSelected) 22.dp else minOffsetDp) + dragOffsetDp else animatedBaseOffsetDp)
        .coerceIn(minOffsetDp, maxOffsetDp)

    Box(
        modifier = modifier
            .size(width = switchWidthDp, height = switchHeightDp)
            .semantics { role = Role.Switch }
            .clip(Capsule())
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    val next = !currentSelected
                    currentOnSelect(next)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
            )
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = enabled,
                onDragStarted = { isDragging = true },
                onDragStopped = {
                    val shouldToggle = if (currentSelected) {
                        dragOffsetPx < -maxDragPx / 2
                    } else {
                        dragOffsetPx > maxDragPx / 2
                    }
                    if (shouldToggle) {
                        currentOnSelect(!currentSelected)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    dragOffsetPx = 0f
                    isDragging = false
                },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        val thumbModifier = Modifier
            .offset {
                IntOffset(
                    x = with(density) {
                        if (isLtr) effectiveOffsetDp.roundToPx() else -effectiveOffsetDp.roundToPx()
                    },
                    y = 0,
                )
            }
            .shadow(
                elevation = 2.dp,
                shape = Capsule(),
                ambientColor = Color.Black.copy(alpha = 0.14f),
                spotColor = Color.Black.copy(alpha = 0.22f),
            )
            .size(width = thumbWidthDp, height = thumbBaseSizeDp)
            .clip(Capsule())

        if (backdrop != null) {
            Box(
                thumbModifier
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { Capsule() },
                        effects = {
                            blur(4f.dp.toPx())
                            lens(3f.dp.toPx(), 6f.dp.toPx(), chromaticAberration = true)
                        },
                        highlight = { Highlight.Ambient.copy(alpha = 0.45f) },
                        shadow = { Shadow(radius = 2f.dp, color = Color.Black.copy(0.12f)) },
                        onDrawSurface = { drawRect(Color.White.copy(alpha = 0.90f)) },
                    ),
            )
        } else {
            Box(
                thumbModifier.background(Color.White),
            )
        }
    }
}
