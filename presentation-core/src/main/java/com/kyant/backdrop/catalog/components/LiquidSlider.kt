package com.kyant.backdrop.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberBackdrop
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

@Composable
fun LiquidSlider(
    value: () -> Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    visibilityThreshold: Float = 0.001f,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
) {
    val isLightTheme = !isSystemInDarkTheme()
    val accentColor =
        if (isLightTheme) Color(0xFF007AFF)
        else Color(0xFF0A84FF)
    val trackColor =
        if (isLightTheme) Color(0xFF787878).copy(alpha = 0.2f)
        else Color(0xFF787880).copy(alpha = 0.36f)

    val trackBackdrop = rememberLayerBackdrop()
    val density = LocalDensity.current
    val thumbSize = 28.dp
    val thumbWidthPx = with(density) { thumbSize.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        val trackWidth = constraints.maxWidth.toFloat()
        val maxTravel = (trackWidth - thumbWidthPx).coerceAtLeast(0f)

        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()
        var didDrag by remember { mutableStateOf(false) }
        var currentDragValue by remember { mutableFloatStateOf(value()) }

        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = value(),
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.04f,
                onDragStarted = {
                    didDrag = false
                    currentDragValue = value()
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

        LaunchedEffect(value()) {
            if (!didDrag) {
                val next = value()
                currentDragValue = next
                if (dampedDragAnimation.targetValue != next) {
                    dampedDragAnimation.updateValue(next)
                }
            }
        }

        val updateFromPosition: (Float) -> Unit = { posX ->
            val fraction = if (maxTravel > 0f) {
                ((posX - thumbWidthPx / 2f) / maxTravel).coerceIn(0f, 1f)
            } else 0f
            val targetValue = if (isLtr) {
                valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)
            } else {
                valueRange.endInclusive - fraction * (valueRange.endInclusive - valueRange.start)
            }.coerceIn(valueRange)
            currentDragValue = targetValue
            dampedDragAnimation.updateValue(targetValue)
            onValueChange(targetValue)
        }

        // Gesture handling container over the entire 44dp height
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(trackWidth, isLtr, valueRange) {
                    detectTapGestures(
                        onPress = {
                            dampedDragAnimation.press()
                            tryAwaitRelease()
                            dampedDragAnimation.release()
                        },
                        onTap = { position ->
                            updateFromPosition(position.x)
                            onValueChange(currentDragValue)
                        },
                    )
                }
                .pointerInput(trackWidth, isLtr, valueRange) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            didDrag = true
                            dampedDragAnimation.press()
                            updateFromPosition(offset.x)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            updateFromPosition(change.position.x)
                        },
                        onDragEnd = {
                            dampedDragAnimation.release()
                            if (didDrag) {
                                onValueChange(currentDragValue)
                            }
                            didDrag = false
                        },
                        onDragCancel = {
                            dampedDragAnimation.release()
                            didDrag = false
                        },
                    )
                },
        )

        // Track (unfilled and filled)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .layerBackdrop(trackBackdrop),
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
                        val progress = dampedDragAnimation.progress.coerceIn(0f, 1f)
                        val width = (thumbWidthPx / 2f + maxTravel * progress).fastRoundToInt().coerceIn(0, constraints.maxWidth)
                        layout(width, placeable.height) {
                            placeable.place(0, 0)
                        }
                    },
            )
        }

        // Apple UISlider Circular Thumb (28.dp x 28.dp)
        Box(
            Modifier
                .graphicsLayer {
                    val progress = dampedDragAnimation.progress.coerceIn(0f, 1f)
                    translationX = if (isLtr) maxTravel * progress else -maxTravel * progress
                }
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(
                        backdrop,
                        rememberBackdrop(trackBackdrop) { drawBackdrop ->
                            val progress = dampedDragAnimation.pressProgress
                            val scaleX = lerp(2f / 3f, 1f, progress)
                            val scaleY = lerp(0f, 1f, progress)
                            scale(scaleX, scaleY) {
                                drawBackdrop()
                            }
                        },
                    ),
                    shape = { Capsule() },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        blur(6f.dp.toPx() * (1f - progress))
                        lens(
                            6f.dp.toPx() * progress,
                            10f.dp.toPx() * progress,
                            chromaticAberration = true,
                        )
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
                            radius = 4.dp,
                            color = Color.Black.copy(alpha = 0.28f),
                        )
                    },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(
                            radius = 3.dp * progress,
                            alpha = progress,
                        )
                    },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(Color.White.copy(alpha = 1f - progress * 0.15f))
                    },
                )
                .size(thumbSize),
        )
    }
}
