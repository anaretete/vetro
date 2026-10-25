package tachiyomi.presentation.core.components.material

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberBackdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.utils.DampedDragAnimation
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule

/**
 * Authentic Apple iOS 26 Vertical Slider for Reader scrub bar.
 * Replaces rotated horizontal sliders with a native vertical Y gesture handler.
 */
@Composable
fun CupertinoVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    invertDirection: Boolean = false, // false = top (start) to bottom (end)
    onValueChangeFinished: (() -> Unit)? = null,
    backdrop: Backdrop? = null,
) {
    val isLightTheme = !isSystemInDarkTheme()
    val isDark = !isLightTheme
    val activeBackdrop = backdrop ?: LocalLayerBackdrop.current ?: rememberCanvasBackdrop {
        val baseColor = if (isDark) Color(0xFF16161A) else Color(0xFFF2F2F6)
        drawRect(baseColor)
    }
    val accentColor =
        if (isLightTheme) {
            Color(0xFF0088FF)
        } else {
            Color(0xFF0091FF)
        }
    val trackColor =
        if (isLightTheme) {
            Color(0xFF787878).copy(alpha = 0.2f)
        } else {
            Color(0xFF787880).copy(alpha = 0.36f)
        }

    val trackBackdrop = rememberLayerBackdrop()
    val density = LocalDensity.current
    val thumbWidth = 24.dp
    val thumbHeight = 40.dp
    val thumbHeightPx = with(density) { thumbHeight.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .defaultMinSize(minWidth = 44.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val trackHeight = if (constraints.hasBoundedHeight) constraints.maxHeight.toFloat() else with(density) { 300.dp.toPx() }
        val maxTravel = (trackHeight - thumbHeightPx).coerceAtLeast(0f)

        val animationScope = rememberCoroutineScope()
        var didDrag by remember { mutableStateOf(false) }
        var currentDragValue by remember { mutableFloatStateOf(value) }
        val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
        val threshold = (rangeSpan * 0.0001f).coerceAtLeast(0.0001f)

        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = value,
                valueRange = valueRange,
                visibilityThreshold = threshold,
                initialScale = 1f,
                pressedScale = 1.5f,
                onDragStarted = {
                    didDrag = false
                    currentDragValue = value
                },
                onDragStopped = {
                    if (didDrag) {
                        onValueChange(currentDragValue)
                    }
                    didDrag = false
                },
                onDrag = { _, _ -> },
            )
        }

        LaunchedEffect(value) {
            if (!didDrag && kotlin.math.abs(currentDragValue - value) > 0.6f) {
                currentDragValue = value
                if (dampedDragAnimation.targetValue != value) {
                    dampedDragAnimation.updateValue(value)
                }
            }
        }

        val updateFromY: (Float) -> Unit = { posY ->
            val fraction = if (maxTravel > 0f) {
                ((posY - thumbHeightPx / 2f) / maxTravel).coerceIn(0f, 1f)
            } else {
                0f
            }
            val targetValue = if (!invertDirection) {
                valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)
            } else {
                valueRange.endInclusive - fraction * (valueRange.endInclusive - valueRange.start)
            }.coerceIn(valueRange)
            currentDragValue = targetValue
            dampedDragAnimation.updateValue(targetValue)
            onValueChange(targetValue)
        }

        // Gesture handler over the 44dp wide column
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(trackHeight, valueRange, invertDirection) {
                    detectTapGestures(
                        onPress = {
                            dampedDragAnimation.press()
                            tryAwaitRelease()
                            dampedDragAnimation.release()
                        },
                        onTap = { position ->
                            updateFromY(position.y)
                            onValueChange(currentDragValue)
                            onValueChangeFinished?.invoke()
                        },
                    )
                }
                .pointerInput(trackHeight, valueRange, invertDirection) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            didDrag = true
                            dampedDragAnimation.press()
                            updateFromY(offset.y)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            updateFromY(change.position.y)
                        },
                        onDragEnd = {
                            dampedDragAnimation.release()
                            if (didDrag) {
                                onValueChange(currentDragValue)
                                onValueChangeFinished?.invoke()
                            }
                            didDrag = false
                        },
                        onDragCancel = {
                            dampedDragAnimation.release()
                            onValueChangeFinished?.invoke()
                            didDrag = false
                        },
                    )
                },
        )

        // Vertical Track (centered)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .layerBackdrop(trackBackdrop),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                Modifier
                    .clip(Capsule())
                    .background(trackColor)
                    .width(6.dp)
                    .fillMaxHeight(),
            )

            Box(
                Modifier
                    .clip(Capsule())
                    .background(accentColor)
                    .width(6.dp)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val progress = dampedDragAnimation.progress.coerceIn(0f, 1f)
                        val height = (thumbHeightPx / 2f + maxTravel * progress).fastRoundToInt().coerceIn(0, constraints.maxHeight)
                        layout(placeable.width, height) {
                            placeable.place(0, 0)
                        }
                    },
            )
        }

        // 24x40dp Capsule Thumb (vertical equivalent of LiquidSlider's 40x24dp thumb)
        Box(
            Modifier
                .graphicsLayer {
                    val progress = dampedDragAnimation.progress.coerceIn(0f, 1f)
                    translationY = maxTravel * progress
                }
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(
                        activeBackdrop,
                        rememberBackdrop(trackBackdrop) { drawBackdrop ->
                            val progress = dampedDragAnimation.pressProgress
                            val scaleX = lerp(0.001f, 1f, progress).coerceAtLeast(0.001f)
                            val scaleY = lerp(2f / 3f, 1f, progress).coerceAtLeast(0.001f)
                            scale(scaleX, scaleY) {
                                drawBackdrop()
                            }
                        },
                    ),
                    shape = { Capsule() },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        blur(8f.dp.toPx() * (1f - progress))
                        if (progress > 0.001f) {
                            lens(
                                (10f.dp.toPx() * progress).coerceAtLeast(0.1f),
                                (14f.dp.toPx() * progress).coerceAtLeast(0.1f),
                                chromaticAberration = true,
                            )
                        }
                    },
                    highlight = {
                        val progress = dampedDragAnimation.pressProgress
                        Highlight.Ambient.copy(
                            width = Highlight.Ambient.width / 1.5f,
                            blurRadius = Highlight.Ambient.blurRadius / 1.5f,
                            alpha = progress,
                        )
                    },
                    shadow = {
                        Shadow(
                            radius = 4f.dp,
                            color = Color.Black.copy(alpha = 0.05f),
                        )
                    },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(
                            radius = 4f.dp * progress,
                            alpha = progress,
                        )
                    },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(Color.White.copy(alpha = 1f - progress))
                    },
                )
                .size(thumbWidth, thumbHeight),
        )
    }
}
