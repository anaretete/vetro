package tachiyomi.presentation.core.components.material

import androidx.annotation.IntRange
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidSlider
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
    backdrop: Backdrop = LocalLayerBackdrop.current ?: rememberLayerBackdrop(),
    accentColor: Color? = null,
) {
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)
    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
    val threshold = if (steps > 0) rangeSpan / (steps + 1) else 0.001f

    if (accentColor != null) {
        LiquidSlider(
            value = { currentValue },
            onValueChange = { newValue ->
                if (enabled) {
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
            backdrop = backdrop,
            modifier = modifier.alpha(if (enabled) 1f else 0.5f),
            accentColor = accentColor,
        )
    } else {
        LiquidSlider(
            value = { currentValue },
            onValueChange = { newValue ->
                if (enabled) {
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
            backdrop = backdrop,
            modifier = modifier.alpha(if (enabled) 1f else 0.5f),
        )
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
    backdrop: Backdrop = LocalLayerBackdrop.current ?: rememberLayerBackdrop(),
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
            backdrop = backdrop,
            accentColor = accentColor,
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
    backdrop: Backdrop = LocalLayerBackdrop.current ?: rememberLayerBackdrop(),
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
        backdrop = backdrop,
        thumb = thumb,
        track = track,
    )
}
