package tachiyomi.presentation.core.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import tachiyomi.presentation.core.components.material.CupertinoGrabber
import tachiyomi.presentation.core.components.material.LocalLayerBackdrop
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import kotlin.math.roundToInt

@Composable
fun AdaptiveSheet(
    isTabletUi: Boolean,
    enableSwipeDismiss: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val sheetBg = if (isDark) Color(0xFF16161A).copy(alpha = 0.85f) else Color(0xFFF2F2F6).copy(alpha = 0.88f)
    val sheetBackdrop = rememberCanvasBackdrop {
        val baseColor = if (isDark) Color(0xFF121214) else Color(0xFFEBEBF0)
        val highlight = if (isDark) Color(0xFF28282C) else Color(0xFFFFFFFF)
        drawRect(
            androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(highlight.copy(alpha = 0.90f), baseColor.copy(alpha = 0.85f)),
            ),
        )
    }

    if (isTabletUi) {
        var targetAlpha by remember { mutableFloatStateOf(0f) }
        val alpha by animateFloatAsState(
            targetValue = targetAlpha,
            animationSpec = sheetAnimationSpec,
            label = "alpha",
        )
        val internalOnDismissRequest: () -> Unit = {
            scope.launch {
                targetAlpha = 0f
                onDismissRequest()
            }
        }
        val tabletShape = RoundedCornerShape(24.dp)
        val tabletModifier: Modifier = if (isGlass) {
            Modifier
                .requiredWidthIn(max = 460.dp)
                .heightIn(max = 560.dp)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = {},
                )
                .systemBarsPadding()
                .padding(vertical = 16.dp)
                .liquidGlassSurface(
                    shape = tabletShape,
                    blurRadius = 30.dp,
                    refractionHeight = 24.dp,
                    refractionAmount = 32.dp,
                    tintColor = sheetBg,
                )
                .then(modifier)
        } else {
            Modifier
                .requiredWidthIn(max = 460.dp)
                .heightIn(max = 560.dp)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = {},
                )
                .systemBarsPadding()
                .padding(vertical = 16.dp)
                .then(modifier)
        }

        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = internalOnDismissRequest,
                )
                .fillMaxSize()
                .alpha(alpha),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalLayerBackdrop provides sheetBackdrop) {
                Surface(
                    modifier = tabletModifier,
                    shape = tabletShape,
                    color = if (isGlass) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh,
                    content = {
                        BackHandler(
                            enabled = remember { derivedStateOf { alpha > 0f } }.value,
                            onBack = internalOnDismissRequest,
                        )
                        content()
                    },
                )
            }
        }

        LaunchedEffect(Unit) {
            targetAlpha = 1f
        }
    } else {
        val anchoredDraggableState = rememberSaveable(saver = AnchoredDraggableState.Saver()) {
            AnchoredDraggableState(initialValue = 1)
        }
        val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
            state = anchoredDraggableState,
            positionalThreshold = { _: Float -> with(density) { 56.dp.toPx() } },
            animationSpec = sheetAnimationSpec,
        )
        var isAnchored by remember { mutableStateOf(false) }
        val internalOnDismissRequest: () -> Unit = {
            if (anchoredDraggableState.settledValue == 0) {
                scope.launch {
                    try {
                        anchoredDraggableState.animateTo(1)
                    } catch (_: Throwable) {
                        onDismissRequest()
                    }
                }
            } else {
                onDismissRequest()
            }
        }
        val bottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = internalOnDismissRequest,
                )
                .fillMaxSize()
                .onSizeChanged {
                    if (it.height > 0) {
                        val anchors = DraggableAnchors {
                            0 at 0f
                            1 at it.height.toFloat()
                        }
                        anchoredDraggableState.updateAnchors(anchors)
                        isAnchored = true
                    }
                },
            contentAlignment = Alignment.BottomCenter,
        ) {
            val phoneBaseModifier = Modifier
                .widthIn(max = 460.dp)
                .heightIn(max = 560.dp)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = {},
                )
                .then(
                    if (enableSwipeDismiss) {
                        Modifier.nestedScroll(
                            remember(anchoredDraggableState) {
                                anchoredDraggableState.preUpPostDownNestedScrollConnection {
                                    scope.launch {
                                        try {
                                            anchoredDraggableState.settle(sheetAnimationSpec)
                                        } catch (_: Throwable) {
                                        }
                                    }
                                }
                            },
                        )
                    } else {
                        Modifier
                    },
                )
                .then(modifier)
                .offset {
                    val rawOffset = try {
                        anchoredDraggableState.offset
                    } catch (_: Throwable) {
                        0f
                    }
                    IntOffset(
                        0,
                        rawOffset
                            .takeIf { it.isFinite() }
                            ?.roundToInt()
                            ?: 0,
                    )
                }
                .anchoredDraggable(
                    state = anchoredDraggableState,
                    orientation = Orientation.Vertical,
                    enabled = enableSwipeDismiss,
                    flingBehavior = flingBehavior,
                )
                .navigationBarsPadding()
                .statusBarsPadding()

            val phoneModifier: Modifier = if (isGlass) {
                phoneBaseModifier.liquidGlassSurface(
                    shape = bottomSheetShape,
                    blurRadius = 30.dp,
                    refractionHeight = 24.dp,
                    refractionAmount = 32.dp,
                    tintColor = sheetBg,
                )
            } else {
                phoneBaseModifier
            }

            CompositionLocalProvider(LocalLayerBackdrop provides sheetBackdrop) {
                Surface(
                    modifier = phoneModifier,
                    shape = bottomSheetShape,
                    color = if (isGlass) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh,
                    content = {
                        BackHandler(
                            enabled = anchoredDraggableState.targetValue == 0,
                            onBack = internalOnDismissRequest,
                        )
                        if (isGlass) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                CupertinoGrabber()
                                content()
                            }
                        } else {
                            content()
                        }
                    },
                )
            }

            LaunchedEffect(isAnchored) {
                if (isAnchored) {
                    try {
                        anchoredDraggableState.animateTo(0)
                    } catch (_: Throwable) {
                    }
                }
            }

            LaunchedEffect(anchoredDraggableState) {
                snapshotFlow { anchoredDraggableState.settledValue }
                    .drop(1)
                    .filter { it == 1 }
                    .collectLatest {
                        onDismissRequest()
                    }
            }
        }
    }
}

private fun <T> AnchoredDraggableState<T>.preUpPostDownNestedScrollConnection(
    onFling: (velocity: Float) -> Unit,
) = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.UserInput) {
            dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        return if (source == NestedScrollSource.UserInput) {
            dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = try {
            offset
        } catch (_: Throwable) {
            0f
        }
        val minPos = try {
            anchors.minPosition()
        } catch (_: Throwable) {
            0f
        }
        return if (toFling < 0 && currentOffset > minPos) {
            onFling(toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        onFling(available.toFloat())
        return if (targetValue != settledValue) {
            available
        } else {
            Velocity.Zero
        }
    }

    private fun Float.toOffset(): Offset = Offset(0f, this)

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() = this.y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float = this.y
}

private val sheetAnimationSpec = tween<Float>(durationMillis = 350)
