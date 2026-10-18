package com.kyant.backdrop.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
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
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LiquidSlider(
    value: () -> Float,
    onValueChange: (Float) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    visibilityThreshold: Float = 0.001f,
    accentColor: Color = if (!isSystemInDarkTheme()) Color(0xFF0088FF) else Color(0xFF0091FF),
) {
    val isLightTheme = !isSystemInDarkTheme()
    val trackColor =
        if (isLightTheme) Color(0xFF787878).copy(0.2f)
        else Color(0xFF787880).copy(0.36f)

    val trackBackdrop = rememberLayerBackdrop()

    BoxWithConstraints(
        modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        val trackWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else 1000

        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()
        var didDrag by remember { mutableStateOf(false) }
        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = value(),
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.5f,
                onDragStarted = {},
                onDragStopped = {
                    if (didDrag) {
                        onValueChange(targetValue)
                    }
                },
                onDrag = { _, dragAmount ->
                    if (!didDrag) {
                        didDrag = dragAmount.x != 0f
                    }
                    val safeTrackWidth = trackWidth.coerceAtLeast(1)
                    val delta = (valueRange.endInclusive - valueRange.start) * (dragAmount.x / safeTrackWidth)
                    onValueChange(
                        if (isLtr) (targetValue + delta).coerceIn(valueRange)
                        else (targetValue - delta).coerceIn(valueRange)
                    )
                }
            )
        }
        LaunchedEffect(dampedDragAnimation) {
            snapshotFlow { value() }
                .collectLatest { value ->
                    if (dampedDragAnimation.targetValue != value) {
                        dampedDragAnimation.updateValue(value)
                    }
                }
        }

        Box(Modifier.layerBackdrop(trackBackdrop)) {
            Box(
                Modifier
                    .clip(Capsule())
                    .background(trackColor)
                    .pointerInput(animationScope) {
                        detectTapGestures { position ->
                            val safeTrackWidth = trackWidth.coerceAtLeast(1)
                            val delta = (valueRange.endInclusive - valueRange.start) * (position.x / safeTrackWidth)
                            val targetValue =
                                (if (isLtr) valueRange.start + delta
                                else valueRange.endInclusive - delta)
                                    .coerceIn(valueRange)
                            dampedDragAnimation.animateToValue(targetValue)
                            onValueChange(targetValue)
                        }
                    }
                    .height(6f.dp)
                    .fillMaxWidth()
            )

            Box(
                Modifier
                    .clip(Capsule())
                    .background(accentColor)
                    .height(6f.dp)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val safeMaxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else placeable.width
                        val progress = dampedDragAnimation.progress.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 0f
                        val width = (safeMaxWidth * progress).fastRoundToInt().coerceIn(0, safeMaxWidth)
                        layout(width, placeable.height) {
                            placeable.place(0, 0)
                        }
                    }
            )
        }

        Box(
            Modifier
                .graphicsLayer {
                    val progress = dampedDragAnimation.progress.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 0f
                    translationX =
                        (-size.width / 2f + trackWidth * progress)
                            .fastCoerceIn(-size.width / 4f, (trackWidth - size.width * 3f / 4f).coerceAtLeast(0f)) * if (isLtr) 1f else -1f
                }
                .then(dampedDragAnimation.modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(
                        backdrop,
                        rememberBackdrop(trackBackdrop) { drawBackdrop ->
                            val progress = dampedDragAnimation.pressProgress
                            val scaleX = lerp(2f / 3f, 1f, progress)
                            val scaleY = lerp(0.001f, 1f, progress).coerceAtLeast(0.001f)
                            scale(scaleX, scaleY) {
                                drawBackdrop()
                            }
                        }
                    ),
                    shape = { Capsule() },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        if (progress > 0.001f) {
                            blur(8f.dp.toPx() * (1f - progress))
                            lens(
                                (10f.dp.toPx() * progress).coerceAtLeast(0.1f),
                                (14f.dp.toPx() * progress).coerceAtLeast(0.1f),
                                chromaticAberration = true
                            )
                        }
                    },
                    highlight = {
                        val progress = dampedDragAnimation.pressProgress
                        Highlight.Ambient.copy(
                            width = Highlight.Ambient.width / 1.5f,
                            blurRadius = Highlight.Ambient.blurRadius / 1.5f,
                            alpha = progress
                        )
                    },
                    shadow = {
                        Shadow(
                            radius = 4f.dp,
                            color = Color.Black.copy(alpha = 0.05f)
                        )
                    },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(
                            radius = 4f.dp * progress,
                            alpha = progress
                        )
                    },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                        val velocity = dampedDragAnimation.velocity / 10f
                        val denomX = (1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)).coerceAtLeast(0.01f)
                        val denomY = (1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)).coerceAtLeast(0.01f)
                        scaleX = (scaleX / denomX).takeIf { it.isFinite() && it > 0.001f } ?: 1f
                        scaleY = (scaleY * denomY).takeIf { it.isFinite() && it > 0.001f } ?: 1f
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(Color.White.copy(alpha = (1f - progress).coerceIn(0f, 1f)))
                    }
                )
                .size(40f.dp, 24f.dp)
        )
    }
}
