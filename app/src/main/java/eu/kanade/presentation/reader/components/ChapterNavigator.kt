package eu.kanade.presentation.reader.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidIconButton
import com.kyant.shapes.Capsule
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.presentation.util.isTabletUi
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.CupertinoVerticalSlider
import tachiyomi.presentation.core.components.material.FilledIconButton
import tachiyomi.presentation.core.components.material.LocalLayerBackdrop
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.Slider
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.math.roundToInt

@Composable
fun ChapterNavigator(
    isRtl: Boolean,
    isVerticalSlider: Boolean,
    onNextChapter: () -> Unit,
    enabledNext: Boolean,
    onPreviousChapter: () -> Unit,
    enabledPrevious: Boolean,
    currentPage: Int,
    // SY -->
    currentPageText: String,
    // SY <--
    totalPages: Int,
    onPageIndexChange: (Int) -> Unit,
    onPageFloatChange: ((Float) -> Unit)? = null,
    isBottomLight: Boolean = true,
) {
    // SY -->
    if (isVerticalSlider) {
        ChapterNavigatorVert(
            onNextChapter = onNextChapter,
            enabledNext = enabledNext,
            onPreviousChapter = onPreviousChapter,
            enabledPrevious = enabledPrevious,
            currentPage = currentPage,
            currentPageText = currentPageText,
            totalPages = totalPages,
            onPageIndexChange = onPageIndexChange,
            onPageFloatChange = onPageFloatChange,
            isBottomLight = isBottomLight,
        )
        return
    }
    // SY <--
    val isTabletUi = isTabletUi()
    val horizontalPadding = if (isTabletUi) 24.dp else 8.dp
    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val haptic = LocalHapticFeedback.current

    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme
        .surfaceColorAtElevation(3.dp)
        .copy(alpha = if (isDark) 0.9f else 0.95f)
    val buttonColor = if (isGlass) {
        IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    } else {
        IconButtonDefaults.filledIconButtonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    }
    val targetTextColor = if (isBottomLight) Color(0xFF111113) else Color.White
    val targetPillBorder = if (isBottomLight) Color(0x25000000) else Color(0x35FFFFFF)
    val targetPillBg = if (isBottomLight) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.12f)
    val textColor by animateColorAsState(targetTextColor, animationSpec = tween(250), label = "chapNavText")
    val pillBorderColor by animateColorAsState(targetPillBorder, animationSpec = tween(250), label = "chapNavBorder")
    val pillBgColor by animateColorAsState(targetPillBg, animationSpec = tween(250), label = "chapNavBg")
    val skipIconTint = if (isBottomLight) Color(0xFF111113) else Color.White

    // We explicitly handle direction based on the reader viewer rather than the system direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val prevEnabled = if (isRtl) enabledNext else enabledPrevious
            val prevAction = if (isRtl) onNextChapter else onPreviousChapter
            if (isGlass) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .liquidGlassSurface(
                            shape = CircleShape,
                            blurRadius = 3.dp,
                            refractionHeight = 16.dp,
                            refractionAmount = 24.dp,
                            tintColor = pillBgColor,
                        )
                        .clickable(
                            enabled = prevEnabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 22.dp),
                            onClick = prevAction,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SkipPrevious,
                        contentDescription = stringResource(
                            if (isRtl) MR.strings.action_next_chapter else MR.strings.action_previous_chapter,
                        ),
                        tint = if (prevEnabled) skipIconTint else Color.Gray.copy(alpha = 0.6f),
                    )
                }
            } else {
                FilledIconButton(
                    enabled = prevEnabled,
                    onClick = prevAction,
                    colors = buttonColor,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SkipPrevious,
                        contentDescription = stringResource(
                            if (isRtl) MR.strings.action_next_chapter else MR.strings.action_previous_chapter,
                        ),
                    )
                }
            }

            if (totalPages > 1) {
                val parentBackdrop = LocalLayerBackdrop.current
                val sliderPillBackdrop = rememberLayerBackdrop()
                val sliderModifier = if (isGlass) {
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .liquidGlassSurface(
                            shape = Capsule(),
                            blurRadius = 3.dp,
                            refractionHeight = 16.dp,
                            refractionAmount = 26.dp,
                            tintColor = pillBgColor,
                            backdrop = parentBackdrop,
                            exportedBackdrop = sliderPillBackdrop,
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                } else {
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp)
                }
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Row(
                        modifier = sliderModifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(contentAlignment = Alignment.CenterEnd) {
                            Text(
                                // SY -->
                                text = currentPageText,
                                // SY <--
                                // KMK -->
                                color = textColor,
                                // KMK <--
                            )
                            // Taking up full length so the slider doesn't shift when 'currentPage' length changes
                            Text(text = totalPages.toString(), color = Color.Transparent)
                        }

                        val interactionSource = remember { MutableInteractionSource() }
                        val sliderDragged by interactionSource.collectIsDraggedAsState()
                        LaunchedEffect(currentPage) {
                            if (sliderDragged) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                        val safeTotalPages = maxOf(1, totalPages)
                        val safeCurrentPageFloat = (currentPage + 1).toFloat().coerceIn(1f, safeTotalPages.toFloat())
                        CompositionLocalProvider(
                            LocalLayerBackdrop provides if (isGlass) sliderPillBackdrop else parentBackdrop,
                        ) {
                            Slider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                value = safeCurrentPageFloat,
                                valueRange = 1f..safeTotalPages.toFloat(),
                                steps = 0,
                                onValueChange = f@{ nextVal ->
                                    val floatIdx = (nextVal - 1f).coerceIn(0f, (safeTotalPages - 1).toFloat())
                                    if (onPageFloatChange != null) {
                                        onPageFloatChange(floatIdx)
                                    } else {
                                        val newPageIndex = floatIdx.roundToInt().coerceIn(0, safeTotalPages - 1)
                                        if (newPageIndex == currentPage) return@f
                                        onPageIndexChange(newPageIndex)
                                    }
                                },
                                interactionSource = interactionSource,
                            )
                        }

                        Text(
                            text = totalPages.toString(),
                            // KMK -->
                            color = textColor,
                            // KMK <--
                        )
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }

            val nextEnabled = if (isRtl) enabledPrevious else enabledNext
            val nextAction = if (isRtl) onPreviousChapter else onNextChapter
            if (isGlass) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .liquidGlassSurface(
                            shape = CircleShape,
                            blurRadius = 3.dp,
                            refractionHeight = 16.dp,
                            refractionAmount = 24.dp,
                            tintColor = pillBgColor,
                        )
                        .clickable(
                            enabled = nextEnabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 22.dp),
                            onClick = nextAction,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SkipNext,
                        contentDescription = stringResource(
                            if (isRtl) MR.strings.action_previous_chapter else MR.strings.action_next_chapter,
                        ),
                        tint = if (nextEnabled) skipIconTint else Color.Gray.copy(alpha = 0.6f),
                    )
                }
            } else {
                FilledIconButton(
                    enabled = nextEnabled,
                    onClick = nextAction,
                    colors = buttonColor,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SkipNext,
                        contentDescription = stringResource(
                            if (isRtl) MR.strings.action_previous_chapter else MR.strings.action_next_chapter,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterNavigatorVert(
    onNextChapter: () -> Unit,
    enabledNext: Boolean,
    onPreviousChapter: () -> Unit,
    enabledPrevious: Boolean,
    currentPage: Int,
    // SY -->
    currentPageText: String,
    // SY <--
    totalPages: Int,
    onPageIndexChange: (Int) -> Unit,
    onPageFloatChange: ((Float) -> Unit)? = null,
    isBottomLight: Boolean = true,
) {
    val isTabletUi = isTabletUi()
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val verticalPadding = if (isTabletUi) 24.dp else 8.dp
    val topPadding = if (isGlass) 92.dp else verticalPadding
    val bottomPadding = if (isGlass) 108.dp else verticalPadding

    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .systemBarsPadding()
            .padding(top = topPadding, bottom = bottomPadding, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val isDark = isSystemInDarkTheme()
        val backgroundColor = MaterialTheme.colorScheme
            .surfaceColorAtElevation(3.dp)
            .copy(alpha = if (isDark) 0.9f else 0.95f)
        val buttonColor = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (isGlass) Color.Transparent else backgroundColor,
            contentColor = if (isDark) Color.White else Color(0xFF1C1C1E),
            disabledContainerColor = if (isGlass) Color.Transparent else (if (isDark) Color(0x441C1C1E) else Color(0x66FFFFFF)),
            disabledContentColor = Color.Gray,
        )
        val targetTextColor = if (isBottomLight) Color(0xFF111113) else Color.White
        val targetPillBorder = if (isBottomLight) Color(0x25000000) else Color(0x35FFFFFF)
        val targetPillBg = if (isBottomLight) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.12f)
        val textColor by animateColorAsState(targetTextColor, animationSpec = tween(250), label = "chapNavVertText")
        val pillBorderColor by animateColorAsState(targetPillBorder, animationSpec = tween(250), label = "chapNavVertBorder")
        val pillBgColor by animateColorAsState(targetPillBg, animationSpec = tween(250), label = "chapNavVertBg")
        val skipIconTint = if (isBottomLight) Color(0xFF111113) else Color.White

        if (isGlass) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .liquidGlassSurface(
                        shape = CircleShape,
                        blurRadius = 3.dp,
                        refractionHeight = 16.dp,
                        refractionAmount = 24.dp,
                        tintColor = pillBgColor,
                    )
                    .clickable(
                        enabled = enabledPrevious,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 22.dp),
                        onClick = onPreviousChapter,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.SkipPrevious,
                    contentDescription = stringResource(MR.strings.action_previous_chapter),
                    modifier = Modifier.rotate(90f),
                    tint = if (enabledPrevious) skipIconTint else Color.Gray.copy(alpha = 0.6f),
                )
            }
        } else {
            FilledIconButton(
                enabled = enabledPrevious,
                onClick = onPreviousChapter,
                colors = buttonColor,
            ) {
                Icon(
                    imageVector = Icons.Outlined.SkipPrevious,
                    contentDescription = stringResource(MR.strings.action_previous_chapter),
                    modifier = Modifier.rotate(90f),
                )
            }
        }

        if (totalPages > 1) {
            val parentBackdrop = LocalLayerBackdrop.current
            val sliderPillBackdrop = rememberLayerBackdrop()
            val sliderModifier = if (isGlass) {
                Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .liquidGlassSurface(
                        shape = Capsule(),
                        blurRadius = 3.dp,
                        refractionHeight = 16.dp,
                        refractionAmount = 26.dp,
                        tintColor = pillBgColor,
                        backdrop = parentBackdrop,
                        exportedBackdrop = sliderPillBackdrop,
                    )
                    .padding(vertical = 12.dp, horizontal = 6.dp)
            } else {
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(backgroundColor)
                    .padding(vertical = 12.dp)
            }
            Column(
                modifier = sliderModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    // SY -->
                    text = currentPageText,
                    // SY <--
                    // KMK -->
                    color = textColor,
                    // KMK <--
                )

                val safeTotalPages = maxOf(1, totalPages)
                val safeCurrentPageFloat = (currentPage + 1).toFloat().coerceIn(1f, safeTotalPages.toFloat())
                CompositionLocalProvider(
                    LocalLayerBackdrop provides if (isGlass) sliderPillBackdrop else parentBackdrop,
                ) {
                    CupertinoVerticalSlider(
                        value = safeCurrentPageFloat,
                        onValueChange = { nextVal ->
                            val floatIdx = (nextVal - 1f).coerceIn(0f, (safeTotalPages - 1).toFloat())
                            val newPageIndex = floatIdx.roundToInt().coerceIn(0, safeTotalPages - 1)
                            if (onPageFloatChange != null) {
                                onPageFloatChange(floatIdx)
                                if (newPageIndex != currentPage) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            } else if (newPageIndex != currentPage) {
                                onPageIndexChange(newPageIndex)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        valueRange = 1f..safeTotalPages.toFloat(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                    )
                }

                Text(
                    text = totalPages.toString(),
                    // KMK -->
                    color = textColor,
                    // KMK <--
                )
            }
        } else {
            Spacer(Modifier.weight(1f))
        }

        if (isGlass) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .liquidGlassSurface(
                        shape = CircleShape,
                        blurRadius = 3.dp,
                        refractionHeight = 16.dp,
                        refractionAmount = 24.dp,
                        tintColor = pillBgColor,
                    )
                    .clickable(
                        enabled = enabledNext,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 22.dp),
                        onClick = onNextChapter,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.SkipNext,
                    contentDescription = stringResource(MR.strings.action_next_chapter),
                    modifier = Modifier.rotate(90f),
                    tint = if (enabledNext) skipIconTint else Color.Gray.copy(alpha = 0.6f),
                )
            }
        } else {
            FilledIconButton(
                enabled = enabledNext,
                onClick = onNextChapter,
                colors = buttonColor,
            ) {
                Icon(
                    imageVector = Icons.Outlined.SkipNext,
                    contentDescription = stringResource(MR.strings.action_next_chapter),
                    modifier = Modifier.rotate(90f),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChapterNavigatorPreview() {
    var currentPage by remember { mutableIntStateOf(1) }
    TachiyomiPreviewTheme {
        ChapterNavigator(
            isRtl = false,
            onNextChapter = {},
            enabledNext = true,
            onPreviousChapter = {},
            enabledPrevious = true,
            currentPage = currentPage,
            totalPages = 10,
            onPageIndexChange = { currentPage = (it + 1) },
            // SY -->
            currentPageText = "1",
            isVerticalSlider = false,
            // SY <--
        )
        // KMK -->
        ChapterNavigator(
            isRtl = false,
            onNextChapter = {},
            enabledNext = true,
            onPreviousChapter = {},
            enabledPrevious = true,
            currentPage = currentPage,
            totalPages = 10,
            onPageIndexChange = { currentPage = (it + 1) },
            currentPageText = "1",
            isVerticalSlider = true,
        )
        // KMK <--
    }
}
