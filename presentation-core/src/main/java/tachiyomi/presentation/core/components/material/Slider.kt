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
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
    val isDark = isSystemInDarkTheme()
    val effectiveAccent = accentColor ?: (if (isDark) Color(0xFF0A84FF) else Color(0xFF007AFF))
    val trackColor = if (!isDark) Color(0xFF787878).copy(alpha = 0.2f) else Color(0xFF787880).copy(alpha = 0.36f)
    val density = LocalDensity.current
    val thumbSize = 28.dp
    val thumbWidthPx = with(density) { thumbSize.toPx() }
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)

    var isDragging by remember { mutableStateOf(false) }
    val thumbScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isDragging) 1.12f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow,
        ),
        label = "CupertinoSliderThumbScale",
    )

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

        // Full-width touch gesture layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(enabled, trackWidth, isLtr, valueRange, steps) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(
                        onPress = {
                            isDragging = true
                            tryAwaitRelease()
                            isDragging = false
                        },
                        onTap = { position ->
                            updateFromPosition(position.x)
                            onValueChangeFinished?.invoke()
                        },
                    )
                }
                .pointerInput(enabled, trackWidth, isLtr, valueRange, steps) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            updateFromPosition(offset.x)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            updateFromPosition(change.position.x)
                        },
                        onDragEnd = {
                            isDragging = false
                            onValueChangeFinished?.invoke()
                        },
                        onDragCancel = {
                            isDragging = false
                            onValueChangeFinished?.invoke()
                        },
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
                    .background(effectiveAccent)
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

        // Thumb: 28dp with liquid glass optics
        val thumbModifier = if (isGlass) {
            Modifier
                .offset(x = with(density) { (maxTravel * (if (isLtr) progress else 1f - progress)).toDp() })
                .size(thumbSize)
                .graphicsLayer {
                    scaleX = thumbScale
                    scaleY = thumbScale
                }
                .shadow(elevation = if (isDragging) 6.dp else 3.dp, shape = CircleShape)
                .liquidGlassSurface(
                    shape = CircleShape,
                    blurRadius = 8.dp,
                    refractionHeight = 12.dp,
                    refractionAmount = 16.dp,
                    tintColor = Color.White.copy(alpha = 0.9f),
                )
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.92f))
        } else {
            Modifier
                .offset(x = with(density) { (maxTravel * (if (isLtr) progress else 1f - progress)).toDp() })
                .size(thumbSize)
                .graphicsLayer {
                    scaleX = thumbScale
                    scaleY = thumbScale
                }
                .shadow(elevation = if (isDragging) 6.dp else 3.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        }

        Box(thumbModifier)
    }
}

/**
 * Authentic Apple iOS Solid UISlider (used when Liquid Glass is disabled).
 * Features standard 6dp track, 28dp solid white thumb with shadow, 1:1 touch tracking.
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
    accentColor: Color = if (!isSystemInDarkTheme()) Color(0xFF007AFF) else Color(0xFF0A84FF),
) {
    val isLightTheme = !isSystemInDarkTheme()
    val trackColor = if (isLightTheme) Color(0xFF787878).copy(alpha = 0.2f) else Color(0xFF787880).copy(alpha = 0.36f)
    val density = LocalDensity.current
    val thumbSize = 28.dp
    val thumbWidthPx = with(density) { thumbSize.toPx() }
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

        // Circular Thumb (28dp)
        Box(
            Modifier
                .offset(x = with(density) { (maxTravel * (if (isLtr) progress else 1f - progress)).toDp() })
                .size(thumbSize)
                .shadow(elevation = 3.dp, shape = CircleShape)
                .clip(CircleShape)
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
    @IntRange(from = 0) steps: Int = with(valueRange) { (last - first) - 1 },
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
