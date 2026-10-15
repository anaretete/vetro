package tachiyomi.presentation.core.components.material

import androidx.annotation.IntRange
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kyant.shapes.Capsule
import kotlin.math.roundToInt

/**
 * Authentic Apple iOS 26 Cupertino Liquid Glass Slider (UISlider standard).
 *
 * Active track: Vibrant Apple Green (#34C759 light / #30D158 dark).
 * Inactive track: Translucent glass track (#E5E5EA light / #3A3A3C dark).
 * Thumb: 28dp circular pure white disc with drop shadow and AGSL liquid glass refraction.
 */
@Composable
fun CupertinoSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val isDark = isSystemInDarkTheme()
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    val activeTrackColor = if (isDark) Color(0xFF30D158) else Color(0xFF34C759)
    val inactiveTrackColor = if (isDark) Color(0xFF3A3A3C) else Color(0xFFE5E5EA)

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)

    var isDragging by remember { mutableStateOf(false) }
    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "CupertinoSliderThumbScale",
    )

    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
    val fraction = ((value - valueRange.start) / rangeSpan).coerceIn(0f, 1f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        val widthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val thumbRadiusPx = with(density) { 14.dp.toPx() }
        val usableTrackWidthPx = (widthPx - thumbRadiusPx * 2).coerceAtLeast(1f)

        fun snapValue(rawFraction: Float): Float {
            val clampedFraction = rawFraction.coerceIn(0f, 1f)
            return if (steps > 0) {
                val stepCount = steps + 1
                val snappedStep = (clampedFraction * stepCount).roundToInt()
                val snappedFraction = snappedStep.toFloat() / stepCount
                valueRange.start + snappedFraction * rangeSpan
            } else {
                valueRange.start + clampedFraction * rangeSpan
            }
        }

        val gestureModifier = if (enabled) {
            Modifier.pointerInput(valueRange, steps, widthPx) {
                detectTapGestures { tapOffset ->
                    val rawX = if (isLtr) tapOffset.x - thumbRadiusPx else (widthPx - tapOffset.x) - thumbRadiusPx
                    val newFraction = (rawX / usableTrackWidthPx).coerceIn(0f, 1f)
                    val newValue = snapValue(newFraction)
                    currentOnValueChange(newValue)
                    currentOnValueChangeFinished?.invoke()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }.pointerInput(valueRange, steps, widthPx) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        currentOnValueChangeFinished?.invoke()
                    },
                    onDragCancel = {
                        isDragging = false
                        currentOnValueChangeFinished?.invoke()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val deltaX = if (isLtr) dragAmount.x else -dragAmount.x
                        val deltaFraction = deltaX / usableTrackWidthPx
                        val currentFraction = ((value - valueRange.start) / rangeSpan).coerceIn(0f, 1f)
                        val targetFraction = (currentFraction + deltaFraction).coerceIn(0f, 1f)
                        val newValue = snapValue(targetFraction)
                        if (newValue != value) {
                            currentOnValueChange(newValue)
                            if (steps > 0) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    },
                )
            }
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(gestureModifier),
            contentAlignment = Alignment.CenterStart,
        ) {
            // Track background (inactive)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(Capsule())
                    .background(inactiveTrackColor)
                    .liquidGlassSurface(
                        shape = Capsule(),
                        blurRadius = 4.dp,
                        refractionHeight = 6.dp,
                        refractionAmount = 8.dp,
                        isInteractive = false,
                    ),
            )

            // Track progress (active Apple Green)
            val activeWidthFraction = fraction.coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(activeWidthFraction)
                    .height(5.dp)
                    .clip(Capsule())
                    .background(activeTrackColor)
                    .liquidGlassSurface(
                        shape = Capsule(),
                        blurRadius = 4.dp,
                        refractionHeight = 6.dp,
                        refractionAmount = 10.dp,
                        tintColor = activeTrackColor.copy(alpha = 0.35f),
                        isInteractive = false,
                    ),
            )

            // Thumb
            val thumbOffsetPx = if (isLtr) {
                fraction * usableTrackWidthPx
            } else {
                (1f - fraction) * usableTrackWidthPx
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(x = thumbOffsetPx.roundToInt(), y = 0) }
                    .size(28.dp)
                    .shadow(
                        elevation = if (isDragging) 4.5.dp else 2.5.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.25f),
                        spotColor = Color.Black.copy(alpha = 0.35f),
                    )
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
}

/**
 * Universal drop-in Slider replacement with automatic Cupertino Liquid Glass styling.
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (SliderState) -> Unit = { sliderState ->
        SliderDefaults.Track(colors = colors, enabled = enabled, sliderState = sliderState)
    },
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        CupertinoSlider(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
        )
    } else {
        androidx.compose.material3.Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
            colors = colors,
            interactionSource = interactionSource,
            thumb = thumb,
            track = track,
        )
    }
}

@Composable
fun Slider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: IntProgression = 0..1,
    @IntRange(from = 0) steps: Int = with(valueRange) { (last - first) - 1 },
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (SliderState) -> Unit = { sliderState ->
        SliderDefaults.Track(colors = colors, enabled = enabled, sliderState = sliderState)
    },
) {
    Slider(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.roundToInt()) },
        modifier = modifier,
        enabled = enabled,
        valueRange = with(valueRange) { first.toFloat()..last.toFloat() },
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track,
    )
}
