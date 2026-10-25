package tachiyomi.presentation.core.components.material

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidSlider
import com.kyant.shapes.Capsule
import kotlin.math.roundToInt

/**
 * Authentic Apple iOS 26 Cupertino Liquid Glass Slider (UISlider standard).
 * Powered by Kyant0's authentic catalog LiquidSlider with DampedDragAnimation physics.
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
    backdrop: Backdrop? = null,
    accentColor: Color? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (!isGlass) {
        SolidCupertinoSlider(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
            accentColor = accentColor ?: MaterialTheme.colorScheme.primary,
        )
        return
    }

    val isDark = isSystemInDarkTheme()
    val fallbackBackdrop = rememberLayerBackdrop()
    val activeBackdrop = backdrop ?: LocalLayerBackdrop.current ?: rememberCanvasBackdrop {
        val baseColor = if (isDark) Color(0xFF16161A) else Color(0xFFF2F2F6)
        drawRect(baseColor)
    }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)
    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
    val threshold = (rangeSpan * 0.0001f).coerceAtLeast(0.0001f)

    var internalValue by remember { mutableFloatStateOf(value) }
    LaunchedEffect(value) {
        if (steps > 0) {
            val stepCount = steps + 1
            val currentStep = (((internalValue - valueRange.start) / rangeSpan).coerceIn(0f, 1f) * stepCount).roundToInt()
            val incomingStep = (((value - valueRange.start) / rangeSpan).coerceIn(0f, 1f) * stepCount).roundToInt()
            if (currentStep != incomingStep) {
                internalValue = value
            }
        } else if (kotlin.math.abs(internalValue - value) > maxOf(threshold * 10f, if (rangeSpan >= 1f) 0.6f else 0f)) {
            internalValue = value
        }
    }

    LiquidSlider(
        value = { internalValue },
        onValueChange = { newValue ->
            if (enabled) {
                internalValue = newValue
                val clamped = if (steps > 0) {
                    val stepCount = steps + 1
                    val fraction = ((newValue - valueRange.start) / rangeSpan).coerceIn(0f, 1f)
                    val step = (fraction * stepCount).roundToInt()
                    valueRange.start + (step.toFloat() / stepCount) * rangeSpan
                } else {
                    newValue
                }
                currentOnValueChange(clamped)
                currentOnValueChangeFinished?.invoke()
            }
        },
        valueRange = valueRange,
        visibilityThreshold = threshold,
        backdrop = activeBackdrop,
        modifier = modifier.alpha(if (enabled) 1f else 0.5f),
        customAccentColor = accentColor,
    )
}

/**
 * Authentic Apple iOS Solid UISlider (used when Liquid Glass is disabled).
 * Features standard 6dp track, 40x24dp solid white capsule thumb with shadow, 1:1 touch tracking.
 */
@Composable
fun SolidCupertinoSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    accentColor: Color = if (!isSystemInDarkTheme()) Color(0xFF0088FF) else Color(0xFF0091FF),
) {
    val isLightTheme = !isSystemInDarkTheme()
    val trackColor = if (isLightTheme) Color(0xFF787878).copy(alpha = 0.2f) else Color(0xFF787880).copy(alpha = 0.36f)
    val density = LocalDensity.current
    val thumbWidth = 40.dp
    val thumbHeight = 24.dp
    val thumbWidthPx = with(density) { thumbWidth.toPx() }
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp)
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.CenterStart,
    ) {
        val trackWidth = constraints.maxWidth.toFloat()
        val maxTravel = (trackWidth - thumbWidthPx).coerceAtLeast(0f)
        val progress = ((value - valueRange.start) / rangeSpan).coerceIn(0f, 1f)

        val updateFromPosition: (Float) -> Unit = { posX ->
            val fraction = if (maxTravel > 0f) {
                ((posX - thumbWidthPx / 2f) / maxTravel).coerceIn(0f, 1f)
            } else {
                0f
            }
            val effectiveFraction = if (isLtr) fraction else 1f - fraction
            val targetValue = if (steps > 0) {
                val stepCount = steps + 1
                val step = (effectiveFraction * stepCount).roundToInt()
                valueRange.start + (step.toFloat() / stepCount) * rangeSpan
            } else {
                valueRange.start + effectiveFraction * rangeSpan
            }.coerceIn(valueRange)
            onValueChange(targetValue)
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(enabled, trackWidth, isLtr, valueRange) {
                    if (!enabled) return@pointerInput
                    detectTapGestures { position ->
                        updateFromPosition(position.x)
                        onValueChangeFinished?.invoke()
                    }
                }
                .pointerInput(enabled, trackWidth, isLtr, valueRange) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDrag = { change, _ ->
                            change.consume()
                            updateFromPosition(change.position.x)
                        },
                        onDragEnd = { onValueChangeFinished?.invoke() },
                        onDragCancel = { onValueChangeFinished?.invoke() },
                    )
                },
        )

        // Track (unfilled and filled)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
        ) {
            Box(
                Modifier
                    .clip(Capsule())
                    .background(trackColor)
                    .height(6.dp)
                    .fillMaxWidth(),
            )

            Box(
                Modifier
                    .clip(Capsule())
                    .background(accentColor)
                    .height(6.dp)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val width = (thumbWidthPx / 2f + maxTravel * progress).fastRoundToInt().coerceIn(0, constraints.maxWidth)
                        layout(width, placeable.height) {
                            placeable.place(0, 0)
                        }
                    },
            )
        }

        // Capsule Thumb (40x24dp)
        Box(
            Modifier
                .offset(x = with(density) { (maxTravel * (if (isLtr) progress else 1f - progress)).toDp() })
                .size(thumbWidth, thumbHeight)
                .shadow(elevation = 3.dp, shape = Capsule())
                .clip(Capsule())
                .background(Color.White),
        )
    }
}

/**
 * Universal drop-in Slider replacement with automatic Cupertino styling.
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
    backdrop: Backdrop? = null,
    accentColor: Color? = null,
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
    CupertinoSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        backdrop = backdrop,
        accentColor = accentColor,
    )
}

@Composable
fun Slider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: IntProgression = 0..1,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backdrop: Backdrop? = null,
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
    var floatValue by remember { mutableFloatStateOf(value.toFloat()) }
    var isDragging by remember { mutableStateOf(false) }
    LaunchedEffect(value) {
        if (!isDragging && floatValue.roundToInt() != value) {
            floatValue = value.toFloat()
        }
    }
    Slider(
        value = floatValue,
        onValueChange = { newFloat ->
            isDragging = true
            floatValue = newFloat
            val newInt = newFloat.roundToInt().coerceIn(valueRange.first, valueRange.last)
            onValueChange(newInt)
        },
        modifier = modifier,
        enabled = enabled,
        valueRange = with(valueRange) { first.toFloat()..last.toFloat() },
        steps = steps,
        onValueChangeFinished = {
            isDragging = false
            floatValue = value.toFloat()
            onValueChangeFinished?.invoke()
        },
        colors = colors,
        interactionSource = interactionSource,
        backdrop = backdrop,
        thumb = thumb,
        track = track,
    )
}
