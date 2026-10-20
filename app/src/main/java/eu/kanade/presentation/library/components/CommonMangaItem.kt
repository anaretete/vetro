package eu.kanade.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import eu.kanade.presentation.manga.components.MangaCover
import eu.kanade.presentation.manga.components.MangaCoverHide
import eu.kanade.presentation.manga.components.RatioSwitchToPanorama
import exh.debug.DebugToggles
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.BadgeGroup
import tachiyomi.presentation.core.components.material.FilledIconButton
import tachiyomi.presentation.core.components.material.LocalLayerBackdrop
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.selectedBackground
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

object CommonMangaItemDefaults {
    val GridHorizontalSpacer = 4.dp
    val GridVerticalSpacer = 4.dp

    @Suppress("ConstPropertyName")
    const val BrowseFavoriteCoverAlpha = 0.34f
}

private val ContinueReadingButtonSizeSmall = 28.dp
private val ContinueReadingButtonSizeLarge = 32.dp

private val ContinueReadingButtonIconSizeSmall = 16.dp
private val ContinueReadingButtonIconSizeLarge = 20.dp

private val ContinueReadingButtonGridPadding = 6.dp
private val ContinueReadingButtonListSpacing = 8.dp

internal const val GRID_SELECTED_COVER_ALPHA = 0.76f

/**
 * Layout of grid list item with title overlaying the cover.
 * Accepts null [title] for a cover-only view.
 */
@Composable
fun MangaCompactGridItem(
    coverData: MangaCoverModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
    title: String? = null,
    onClickContinueReading: (() -> Unit)? = null,
    coverAlpha: Float = 1f,
    coverBadgeStart: @Composable (RowScope.() -> Unit)? = null,
    coverBadgeEnd: @Composable (RowScope.() -> Unit)? = null,
    // KMK -->
    libraryColored: Boolean = true,
    // KMK <--
) {
    // KMK -->
    val bgColor = coverData.dominantCoverColors?.first?.let { Color(it) }.takeIf { libraryColored }
    val onBgColor = coverData.dominantCoverColors?.second.takeIf { libraryColored }
    // KMK <--
    GridItemSelectable(
        isSelected = isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
    ) {
        MangaGridCover(
            cover = {
                // KMK -->
                if (DebugToggles.HIDE_COVER_IMAGE_ONLY_SHOW_COLOR.enabled) {
                    MangaCoverHide.Book(
                        modifier = Modifier
                            .fillMaxWidth(),
                        bgColor = bgColor ?: MaterialTheme.colorScheme.surface.takeIf { isSelected },
                        tint = onBgColor,
                    )
                } else {
                    // KMK <--
                    MangaCover.Book(
                        modifier = Modifier
                            // KMK -->
                            // .alpha(if (isSelected) GridSelectedCoverAlpha else coverAlpha)
                            // KMK <--
                            .fillMaxWidth(),
                        data = coverData,
                        // KMK -->
                        alpha = if (isSelected) GRID_SELECTED_COVER_ALPHA else coverAlpha,
                        bgColor = bgColor ?: MaterialTheme.colorScheme.surface.takeIf { isSelected },
                        tint = onBgColor,
                        // KMK <--
                    )
                }
            },
            badgesStart = coverBadgeStart,
            badgesEnd = coverBadgeEnd,
            content = {
                if (title != null) {
                    CoverTextOverlay(
                        title = title,
                        onClickContinueReading = onClickContinueReading,
                    )
                } else if (onClickContinueReading != null) {
                    ContinueReadingButton(
                        size = ContinueReadingButtonSizeLarge,
                        iconSize = ContinueReadingButtonIconSizeLarge,
                        onClick = onClickContinueReading,
                        modifier = Modifier
                            .padding(ContinueReadingButtonGridPadding)
                            .align(Alignment.BottomEnd),
                    )
                }
            },
        )
    }
}

/**
 * Title overlay for [MangaCompactGridItem]
 */
@Composable
private fun BoxScope.CoverTextOverlay(
    title: String,
    onClickContinueReading: (() -> Unit)? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val bannerShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)

    val overlayModifier = if (isGlass) {
        Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .liquidGlassSurface(
                shape = bannerShape,
                blurRadius = 12.dp,
                refractionHeight = 16.dp,
                refractionAmount = 24.dp,
                surfaceColor = if (isDark) Color(0x660A0A0C) else Color(0x55FFFFFF),
            )
            .drawBehind {
                drawLine(
                    color = Color.White.copy(alpha = if (isDark) 0.35f else 0.6f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
    } else {
        Modifier
            .clip(bannerShape)
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color(0xAA000000),
                ),
            )
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
    }

    Row(
        modifier = overlayModifier
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GridItemTitle(
            modifier = Modifier.weight(1f),
            title = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black,
                    blurRadius = 4f,
                ),
            ),
            minLines = 1,
        )
        if (onClickContinueReading != null) {
            ContinueReadingButton(
                size = ContinueReadingButtonSizeSmall,
                iconSize = ContinueReadingButtonIconSizeSmall,
                onClick = onClickContinueReading,
                modifier = Modifier.padding(start = ContinueReadingButtonGridPadding),
            )
        }
    }
}

/**
 * Layout of grid list item with title below the cover.
 */
@Composable
fun MangaComfortableGridItem(
    coverData: MangaCoverModel,
    title: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
    titleMaxLines: Int = 2,
    coverAlpha: Float = 1f,
    coverBadgeStart: @Composable (RowScope.() -> Unit)? = null,
    coverBadgeEnd: @Composable (RowScope.() -> Unit)? = null,
    onClickContinueReading: (() -> Unit)? = null,
    // KMK -->
    libraryColored: Boolean = true,
    coverRatio: MutableFloatState = remember { mutableFloatStateOf(1f) },
    usePanoramaCover: Boolean,
    fitToPanoramaCover: Boolean = false,
    // KMK <--
) {
    // KMK -->
    val coverIsWide = coverRatio.floatValue <= RatioSwitchToPanorama
    val bgColor = coverData.dominantCoverColors?.first?.let { Color(it) }.takeIf { libraryColored }
    val onBgColor = coverData.dominantCoverColors?.second.takeIf { libraryColored }
    // KMK <--
    GridItemSelectable(
        isSelected = isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
    ) {
        Column {
            MangaGridCover(
                cover = {
                    // KMK -->
                    if (DebugToggles.HIDE_COVER_IMAGE_ONLY_SHOW_COLOR.enabled) {
                        MangaCoverHide.Book(
                            modifier = Modifier
                                .fillMaxWidth(),
                            bgColor = bgColor ?: MaterialTheme.colorScheme.surface.takeIf { isSelected },
                            tint = onBgColor,
                        )
                    } else {
                        if (fitToPanoramaCover && usePanoramaCover && coverIsWide) {
                            MangaCover.Panorama(
                                modifier = Modifier
                                    // KMK -->
                                    // .alpha(if (isSelected) GridSelectedCoverAlpha else coverAlpha)
                                    // KMK <--
                                    .fillMaxWidth(),
                                data = coverData,
                                // KMK -->
                                alpha = if (isSelected) GRID_SELECTED_COVER_ALPHA else coverAlpha,
                                bgColor = bgColor ?: MaterialTheme.colorScheme.surface.takeIf { isSelected },
                                tint = onBgColor,
                                onCoverLoaded = { _, result ->
                                    val image = result.result.image
                                    coverRatio.floatValue = image.height.toFloat() / image.width
                                },
                                // KMK <--
                            )
                        } else {
                            // KMK <--
                            MangaCover.Book(
                                modifier = Modifier
                                    // KMK -->
                                    // .alpha(if (isSelected) GridSelectedCoverAlpha else coverAlpha)
                                    // KMK <--
                                    .fillMaxWidth(),
                                data = coverData,
                                // KMK -->
                                alpha = if (isSelected) GRID_SELECTED_COVER_ALPHA else coverAlpha,
                                bgColor = bgColor ?: MaterialTheme.colorScheme.surface.takeIf { isSelected },
                                tint = onBgColor,
                                onCoverLoaded = { _, result ->
                                    val image = result.result.image
                                    coverRatio.floatValue = image.height.toFloat() / image.width
                                },
                                scale = if (usePanoramaCover && coverIsWide) {
                                    ContentScale.Fit
                                } else {
                                    ContentScale.Crop
                                },
                                // KMK <--
                            )
                        }
                    }
                },
                // KMK -->
                ratio = if (fitToPanoramaCover && usePanoramaCover && coverIsWide) {
                    MangaCover.Panorama.ratio
                } else {
                    MangaCover.Book.ratio
                },
                // KMK <--
                badgesStart = coverBadgeStart,
                badgesEnd = coverBadgeEnd,
                content = {
                    if (onClickContinueReading != null) {
                        ContinueReadingButton(
                            size = ContinueReadingButtonSizeLarge,
                            iconSize = ContinueReadingButtonIconSizeLarge,
                            onClick = onClickContinueReading,
                            modifier = Modifier
                                .padding(ContinueReadingButtonGridPadding)
                                .align(Alignment.BottomEnd),
                        )
                    }
                },
            )
            GridItemTitle(
                modifier = Modifier.padding(4.dp),
                title = title,
                style = MaterialTheme.typography.titleSmall,
                minLines = 2,
                maxLines = titleMaxLines,
            )
        }
    }
}

/**
 * Common cover layout to add contents to be drawn on top of the cover.
 */
@Composable
private fun MangaGridCover(
    modifier: Modifier = Modifier,
    cover: @Composable BoxScope.() -> Unit = {},
    // KMK -->
    ratio: Float = MangaCover.Book.ratio,
    // KMK <--
    badgesStart: (@Composable RowScope.() -> Unit)? = null,
    badgesEnd: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable (BoxScope.() -> Unit)? = null,
) {
    val coverBackdrop = rememberLayerBackdrop()
    val isGlass = LocalLiquidGlassConfig.current.enabled
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isGlass) {
                    Modifier.border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(16.dp),
                    )
                } else {
                    Modifier
                },
            ),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(coverBackdrop),
        ) {
            cover()
        }
        CompositionLocalProvider(LocalLayerBackdrop provides coverBackdrop) {
            content?.invoke(this)
            if (badgesStart != null) {
                BadgeGroup(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopStart),
                    content = badgesStart,
                )
            }

            if (badgesEnd != null) {
                BadgeGroup(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd),
                    content = badgesEnd,
                )
            }
        }
    }
}

@Composable
private fun GridItemTitle(
    title: String,
    style: TextStyle,
    minLines: Int,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
) {
    Text(
        modifier = modifier,
        text = title,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        minLines = minLines,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = style,
    )
}

/**
 * Wrapper for grid items to handle selection state, click and long click.
 */
@Composable
private fun GridItemSelectable(
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape)
                } else if (isGlass) {
                    Modifier.border(0.5.dp, Color.White.copy(alpha = 0.12f), shape)
                } else {
                    Modifier
                },
            )
            .padding(4.dp),
    ) {
        val contentColor = if (isSelected) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            LocalContentColor.current
        }
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

/**
 * @see GridItemSelectable
 */
private fun Modifier.selectedOutline(
    isSelected: Boolean,
    color: Color,
) = drawBehind { if (isSelected) drawRect(color = color) }

/**
 * Layout of list item.
 */
@Composable
fun MangaListItem(
    coverData: MangaCoverModel,
    title: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    badge: @Composable (RowScope.() -> Unit),
    isSelected: Boolean = false,
    coverAlpha: Float = 1f,
    onClickContinueReading: (() -> Unit)? = null,
    // KMK -->
    libraryColored: Boolean = true,
    // KMK <--
) {
    // KMK -->
    val bgColor = coverData.dominantCoverColors?.first?.let { Color(it) }.takeIf { libraryColored }
    val onBgColor = coverData.dominantCoverColors?.second.takeIf { libraryColored }
    // KMK <--
    Row(
        modifier = Modifier
            .selectedBackground(isSelected)
            .height(56.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // KMK -->
        if (DebugToggles.HIDE_COVER_IMAGE_ONLY_SHOW_COLOR.enabled) {
            MangaCoverHide.Square(
                modifier = Modifier
                    .fillMaxHeight(),
                bgColor = bgColor ?: MaterialTheme.colorScheme.surface.takeIf { isSelected },
                tint = onBgColor,
            )
        } else {
            // KMK <--
            MangaCover.Square(
                modifier = Modifier
                    // KMK -->
                    // .alpha(coverAlpha)
                    // KMK <--
                    .fillMaxHeight(),
                data = coverData,
                // KMK -->
                alpha = coverAlpha,
                bgColor = bgColor ?: MaterialTheme.colorScheme.surface.takeIf { isSelected },
                tint = onBgColor,
                size = MangaCover.Size.Big,
                // KMK <--
            )
        }
        Text(
            text = title,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
        )
        BadgeGroup(content = badge)
        if (onClickContinueReading != null) {
            ContinueReadingButton(
                size = ContinueReadingButtonSizeSmall,
                iconSize = ContinueReadingButtonIconSizeSmall,
                onClick = onClickContinueReading,
                modifier = Modifier.padding(start = ContinueReadingButtonListSpacing),
            )
        }
    }
}

@Composable
private fun ContinueReadingButton(
    size: Dp,
    iconSize: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    Box(modifier = modifier) {
        FilledIconButton(
            onClick = onClick,
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isGlass) Color.Transparent else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                contentColor = if (isGlass) Color.White else contentColorFor(MaterialTheme.colorScheme.primaryContainer),
            ),
            modifier = Modifier
                .size(size)
                .liquidGlassSurface(
                    shape = CircleShape,
                    blurRadius = 8.dp,
                    refractionHeight = 10.dp,
                    refractionAmount = 16.dp,
                    tintColor = MaterialTheme.colorScheme.primary,
                ),
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(MR.strings.action_resume),
                modifier = Modifier.size(iconSize),
            )
        }
    }
}
