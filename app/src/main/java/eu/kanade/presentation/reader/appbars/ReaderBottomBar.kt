package eu.kanade.presentation.reader.appbars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.reader.setting.ReaderBottomButton
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import kotlinx.collections.immutable.ImmutableSet
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ReaderBottomBar(
    // SY -->
    enabledButtons: ImmutableSet<String>,
    // SY <--
    readingMode: ReadingMode,
    onClickReadingMode: () -> Unit,
    orientation: ReaderOrientation,
    onClickOrientation: () -> Unit,
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
    // SY <--
    modifier: Modifier = Modifier,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    Row(
        modifier = modifier,
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
            )
        }

        if (ReaderBottomButton.WebView.isIn(enabledButtons) && onClickWebView != null) {
            ReaderBottomBarItem(
                onClick = onClickWebView,
                contentDescription = stringResource(MR.strings.action_open_in_web_view),
                icon = Icons.Outlined.Public,
                isDark = isDark,
                isGlass = isGlass,
            )
        }

        if (ReaderBottomButton.Browser.isIn(enabledButtons) && onClickBrowser != null) {
            ReaderBottomBarItem(
                onClick = onClickBrowser,
                contentDescription = stringResource(MR.strings.action_open_in_browser),
                icon = Icons.Outlined.Explore,
                isDark = isDark,
                isGlass = isGlass,
            )
        }

        if (ReaderBottomButton.Share.isIn(enabledButtons) && onClickShare != null) {
            ReaderBottomBarItem(
                onClick = onClickShare,
                contentDescription = stringResource(MR.strings.action_share),
                icon = Icons.Outlined.Share,
                isDark = isDark,
                isGlass = isGlass,
            )
        }

        if (ReaderBottomButton.ReadingMode.isIn(enabledButtons)) {
            ReaderBottomBarItem(
                onClick = onClickReadingMode,
                contentDescription = stringResource(MR.strings.viewer),
                icon = painterResource(readingMode.iconRes),
                isActive = readingMode != ReadingMode.DEFAULT,
                activeColor = Color(0xFF0A84FF),
                isDark = isDark,
                isGlass = isGlass,
            )
        }

        if (ReaderBottomButton.Rotation.isIn(enabledButtons)) {
            ReaderBottomBarItem(
                onClick = onClickOrientation,
                contentDescription = stringResource(MR.strings.pref_rotation_type),
                icon = orientation.icon,
                isActive = orientation != ReaderOrientation.DEFAULT,
                activeColor = Color(0xFFFF9F0A),
                isDark = isDark,
                isGlass = isGlass,
            )
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
                activeColor = Color(0xFF0A84FF),
                isDark = isDark,
                isGlass = isGlass,
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
            )
        }

        if (doublePages) {
            ReaderBottomBarItem(
                onClick = onClickShiftPage,
                contentDescription = stringResource(SYMR.strings.shift_double_pages),
                icon = painterResource(R.drawable.ic_page_next_outline_24dp),
                isActive = true,
                activeColor = Color(0xFF0A84FF),
                isDark = isDark,
                isGlass = isGlass,
            )
        }

        ReaderBottomBarItem(
            onClick = onClickSettings,
            contentDescription = stringResource(MR.strings.action_settings),
            icon = Icons.Outlined.Settings,
            isDark = isDark,
            isGlass = isGlass,
        )
        // SY <--
    }
}

@Composable
private fun ReaderBottomBarItem(
    onClick: () -> Unit,
    contentDescription: String?,
    icon: Any,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    activeColor: Color = Color(0xFF0A84FF),
    isDark: Boolean = isSystemInDarkTheme(),
    isGlass: Boolean = true,
) {
    val defaultColor = if (isGlass) {
        if (isDark) Color.White else Color(0xFF1C1C1E)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
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
