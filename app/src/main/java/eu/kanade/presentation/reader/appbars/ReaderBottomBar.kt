package eu.kanade.presentation.reader.appbars

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kyant.shapes.Capsule
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.reader.setting.ReaderBottomButton
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord
import kotlinx.collections.immutable.ImmutableSet
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.material.GlassMenuAnchor
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.NativeGlassSurface
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.components.material.supportsNativeGlassMenu
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ReaderBottomBar(
    // SY -->
    enabledButtons: ImmutableSet<String>,
    // SY <--
    readingMode: ReadingMode,
    onClickReadingMode: () -> Unit,
    onSelectReadingMode: ((ReadingMode) -> Unit)? = null,
    orientation: ReaderOrientation,
    onClickOrientation: () -> Unit,
    onSelectOrientation: ((ReaderOrientation) -> Unit)? = null,
    cropEnabled: Boolean,
    onClickCropBorder: () -> Unit,
    onClickSettings: () -> Unit,
    // SY -->
    currentReadingMode: ReadingMode,
    dualPageSplitEnabled: Boolean,
    doublePages: Boolean,
    onClickChapterList: () -> Unit,
    onClickWebView: (() -> Unit)?,
    onClickBrowser: (() -> Unit)?,
    onClickShare: (() -> Unit)?,
    onClickPageLayout: () -> Unit,
    onClickShiftPage: () -> Unit,
    isBottomLight: Boolean = true,
    // SY <--
    modifier: Modifier = Modifier,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val useNativeGlass = isGlass && supportsNativeGlassMenu
    val isDark = isSystemInDarkTheme()
    val targetContainerBorder = if (isGlass) {
        if (isDark) Color(0x35FFFFFF) else Color(0x1F000000)
    } else {
        if (isBottomLight) Color(0x25000000) else Color(0x35FFFFFF)
    }
    val targetContainerTint = if (isDark) Color(0xFF0B0B0D).copy(alpha = 0.68f) else Color(0xFFFAFBFD).copy(alpha = 0.62f)
    val containerBorder by animateColorAsState(targetContainerBorder, animationSpec = tween(250), label = "bottomBarBorder")
    val containerTint by animateColorAsState(targetContainerTint, animationSpec = tween(250), label = "bottomBarTint")

    var showReadingModeMenu by remember { mutableStateOf(false) }
    var showOrientationMenu by remember { mutableStateOf(false) }

    val readingModeGlassItems = if (onSelectReadingMode != null) {
        ReadingMode.entries.map { mode ->
            val label = stringResource(mode.stringRes)
            GlassMenuItemRecord(
                id = mode.name,
                title = label,
                checked = if (mode == readingMode) true else null,
                onClick = {
                    showReadingModeMenu = false
                    onSelectReadingMode(mode)
                },
            )
        }
    } else {
        emptyList()
    }

    val orientationGlassItems = if (onSelectOrientation != null) {
        ReaderOrientation.entries.map { mode ->
            val label = stringResource(mode.stringRes)
            GlassMenuItemRecord(
                id = mode.name,
                title = label,
                checked = if (mode == orientation) true else null,
                onClick = {
                    showOrientationMenu = false
                    onSelectOrientation(mode)
                },
            )
        }
    } else {
        emptyList()
    }

    val effectiveModifier = if (useNativeGlass) {
        modifier
    } else if (isGlass) {
        modifier
            .liquidGlassSurface(
                shape = Capsule(),
                blurRadius = 24.dp,
                refractionHeight = 16.dp,
                refractionAmount = 27.dp,
                tintColor = containerTint,
                surfaceColor = Color.Unspecified,
            )
            .border(
                width = 0.5.dp,
                color = containerBorder,
                shape = Capsule(),
            )
    } else {
        modifier
            .background(
                color = if (isBottomLight) Color(0xF4FAFAFC) else Color(0xEA202024),
                shape = Capsule(),
            )
            .border(
                width = 0.5.dp,
                color = containerBorder,
                shape = Capsule(),
            )
    }

    Box(modifier = effectiveModifier) {
        if (useNativeGlass) {
            NativeGlassSurface(
                modifier = Modifier.matchParentSize(),
                cornerRadius = 30.dp,
                topCornersOnly = false,
            )
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // SY -->
            if (ReaderBottomButton.ViewChapters.isIn(enabledButtons)) {
                ReaderBottomBarItem(
                    onClick = onClickChapterList,
                    contentDescription = stringResource(MR.strings.chapters),
                    icon = Icons.Outlined.FormatListNumbered,
                    isDark = isDark,
                    isGlass = isGlass,
                    isBottomLight = isBottomLight,
                )
            }

            if (ReaderBottomButton.WebView.isIn(enabledButtons) && onClickWebView != null) {
                ReaderBottomBarItem(
                    onClick = onClickWebView,
                    contentDescription = stringResource(MR.strings.action_open_in_web_view),
                    icon = Icons.Outlined.Public,
                    isDark = isDark,
                    isGlass = isGlass,
                    isBottomLight = isBottomLight,
                )
            }

            if (ReaderBottomButton.Browser.isIn(enabledButtons) && onClickBrowser != null) {
                ReaderBottomBarItem(
                    onClick = onClickBrowser,
                    contentDescription = stringResource(MR.strings.action_open_in_browser),
                    icon = Icons.Outlined.Explore,
                    isDark = isDark,
                    isGlass = isGlass,
                    isBottomLight = isBottomLight,
                )
            }

            if (ReaderBottomButton.Share.isIn(enabledButtons) && onClickShare != null) {
                ReaderBottomBarItem(
                    onClick = onClickShare,
                    contentDescription = stringResource(MR.strings.action_share),
                    icon = Icons.Outlined.Share,
                    isDark = isDark,
                    isGlass = isGlass,
                    isBottomLight = isBottomLight,
                )
            }

            if (ReaderBottomButton.ReadingMode.isIn(enabledButtons)) {
                Box {
                    if (useNativeGlass && readingModeGlassItems.isNotEmpty()) {
                        GlassMenuAnchor(
                            expanded = showReadingModeMenu,
                            onDismissRequest = { showReadingModeMenu = false },
                            items = readingModeGlassItems,
                            modifier = Modifier.size(44.dp),
                        )
                    }
                    ReaderBottomBarItem(
                        onClick = {
                            if (useNativeGlass && readingModeGlassItems.isNotEmpty()) {
                                showReadingModeMenu = true
                            } else {
                                onClickReadingMode()
                            }
                        },
                        contentDescription = stringResource(MR.strings.viewer),
                        icon = painterResource(readingMode.iconRes),
                        isActive = readingMode != ReadingMode.DEFAULT,
                        activeColor = MaterialTheme.colorScheme.primary,
                        isDark = isDark,
                        isGlass = isGlass,
                        isBottomLight = isBottomLight,
                    )
                }
            }

            if (ReaderBottomButton.Rotation.isIn(enabledButtons)) {
                Box {
                    if (useNativeGlass && orientationGlassItems.isNotEmpty()) {
                        GlassMenuAnchor(
                            expanded = showOrientationMenu,
                            onDismissRequest = { showOrientationMenu = false },
                            items = orientationGlassItems,
                            modifier = Modifier.size(44.dp),
                        )
                    }
                    ReaderBottomBarItem(
                        onClick = {
                            if (useNativeGlass && orientationGlassItems.isNotEmpty()) {
                                showOrientationMenu = true
                            } else {
                                onClickOrientation()
                            }
                        },
                        contentDescription = stringResource(MR.strings.pref_rotation_type),
                        icon = orientation.icon,
                        isActive = orientation != ReaderOrientation.DEFAULT,
                        activeColor = MaterialTheme.colorScheme.primary,
                        isDark = isDark,
                        isGlass = isGlass,
                        isBottomLight = isBottomLight,
                    )
                }
            }

            val cropBorders = when (currentReadingMode) {
                ReadingMode.WEBTOON -> ReaderBottomButton.CropBordersWebtoon
                ReadingMode.CONTINUOUS_VERTICAL -> ReaderBottomButton.CropBordersContinuesVertical
                else -> ReaderBottomButton.CropBordersPager
            }
            if (cropBorders.isIn(enabledButtons)) {
                ReaderBottomBarItem(
                    onClick = onClickCropBorder,
                    contentDescription = stringResource(MR.strings.pref_crop_borders),
                    icon = painterResource(
                        if (cropEnabled) R.drawable.ic_crop_24dp else R.drawable.ic_crop_off_24dp,
                    ),
                    isActive = cropEnabled,
                    activeColor = MaterialTheme.colorScheme.primary,
                    isDark = isDark,
                    isGlass = isGlass,
                    isBottomLight = isBottomLight,
                )
            }

            if (
                !dualPageSplitEnabled &&
                ReaderBottomButton.PageLayout.isIn(enabledButtons) &&
                ReadingMode.isPagerType(currentReadingMode.flagValue)
            ) {
                ReaderBottomBarItem(
                    onClick = onClickPageLayout,
                    contentDescription = stringResource(SYMR.strings.page_layout),
                    icon = painterResource(R.drawable.ic_book_open_variant_24dp),
                    isDark = isDark,
                    isGlass = isGlass,
                    isBottomLight = isBottomLight,
                )
            }

            if (doublePages) {
                ReaderBottomBarItem(
                    onClick = onClickShiftPage,
                    contentDescription = stringResource(SYMR.strings.shift_double_pages),
                    icon = painterResource(R.drawable.ic_page_next_outline_24dp),
                    isActive = true,
                    activeColor = MaterialTheme.colorScheme.primary,
                    isDark = isDark,
                    isGlass = isGlass,
                    isBottomLight = isBottomLight,
                )
            }

            ReaderBottomBarItem(
                onClick = onClickSettings,
                contentDescription = stringResource(MR.strings.action_settings),
                icon = Icons.Outlined.Settings,
                isDark = isDark,
                isGlass = isGlass,
                isBottomLight = isBottomLight,
            )
            // SY <--
        }
    }
}

@Composable
private fun ReaderBottomBarItem(
    onClick: () -> Unit,
    contentDescription: String?,
    icon: Any,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    isDark: Boolean = isSystemInDarkTheme(),
    isGlass: Boolean = true,
    isBottomLight: Boolean = true,
) {
    val targetDefault = if (isGlass) {
        if (isDark) Color(0xF5FFFFFF) else Color(0xEB000000)
    } else {
        if (isBottomLight) Color(0xFF111113) else Color.White
    }
    val defaultColor by animateColorAsState(targetDefault, animationSpec = tween(250), label = "bottomItemColor")
    val tint = if (isActive) activeColor else defaultColor

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 22.dp),
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        when (icon) {
            is ImageVector -> Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(24.dp),
            )
            is Painter -> Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
