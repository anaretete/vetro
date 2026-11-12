package eu.kanade.presentation.reader.appbars

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreHoriz
import eu.kanade.presentation.components.DropdownMenuItem
import eu.kanade.presentation.components.DropdownMenuSeparator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.shapes.Capsule
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.CupertinoFrostedPopup
import tachiyomi.presentation.core.components.material.GlassMenuAnchor
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.NativeGlassSurface
import tachiyomi.presentation.core.components.material.liquidGlassPopupSurface
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.components.material.supportsNativeGlassMenu
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.math.ceil

@Composable
fun ReaderTopBar(
    mangaTitle: String?,
    chapterTitle: String?,
    navigateUp: () -> Unit,
    bookmarked: Boolean,
    onToggleBookmarked: () -> Unit,
    onOpenInWebView: (() -> Unit)?,
    onOpenInBrowser: (() -> Unit)?,
    onShare: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onClickTopAppBar: (() -> Unit)? = null,
    currentPage: Int = 0,
    totalPages: Int = 0,
    readingModeName: String? = null,
    orientationName: String? = null,
    onPageIndexChange: ((Int) -> Unit)? = null,
    cropEnabled: Boolean = false,
    onClickCropBorder: (() -> Unit)? = null,
    onClickReadingMode: (() -> Unit)? = null,
    onClickOrientation: (() -> Unit)? = null,
    readingMode: ReadingMode? = null,
    onSelectReadingMode: ((ReadingMode) -> Unit)? = null,
    orientation: ReaderOrientation? = null,
    onSelectOrientation: ((ReaderOrientation) -> Unit)? = null,
    onClickChapterList: (() -> Unit)? = null,
    isTopLight: Boolean = true,
    pagesTurnedInSession: Int = 0,
    sessionDurationSeconds: Long = 0L,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val useNativeGlass = false
    val useNativeGlassMenu = isGlass && supportsNativeGlassMenu
    val isDark = isSystemInDarkTheme()

    // Dynamic contrast based on sampled background luminance:
    val targetPrimary = if (isTopLight) Color(0xFF111113) else Color.White
    val targetSecondary = if (isTopLight) Color(0xFF3C3C43).copy(alpha = 0.85f) else Color(0xCCFFFFFF)
    val targetPillBorder = if (isTopLight) Color(0x25000000) else Color(0x35FFFFFF)
    val targetSurfacePillBg = if (isTopLight) Color(0x18000000) else Color(0x28FFFFFF)
    val targetGlassTint = if (isTopLight) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.12f)
    val solidBackingBg = if (isTopLight) Color(0xF4FAFAFC) else Color(0xEA202024)

    val primaryTextColor by animateColorAsState(targetPrimary, animationSpec = tween(250), label = "topBarPrimary")
    val secondaryTextColor by animateColorAsState(targetSecondary, animationSpec = tween(250), label = "topBarSecondary")
    val pillBorderColor by animateColorAsState(targetPillBorder, animationSpec = tween(250), label = "topBarBorder")
    val surfacePillBg by animateColorAsState(targetSurfacePillBg, animationSpec = tween(250), label = "topBarPillBg")
    val glassTintColor by animateColorAsState(targetGlassTint, animationSpec = tween(250), label = "topBarGlassTint")

    val haptic = LocalHapticFeedback.current

    var showOverflow by remember { mutableStateOf(false) }
    val hasOverflow = onOpenInWebView != null || onOpenInBrowser != null || onShare != null

    val webViewLabel = stringResource(MR.strings.action_open_in_web_view)
    val browserLabel = stringResource(MR.strings.action_open_in_browser)
    val shareLabel = stringResource(MR.strings.action_share)
    val overflowGlassItems = remember(onOpenInWebView, onOpenInBrowser, onShare, webViewLabel, browserLabel, shareLabel) {
        buildList {
            onOpenInWebView?.let { action ->
                add(
                    GlassMenuItemRecord(
                        id = "open_in_web_view",
                        title = webViewLabel,
                        onClick = {
                            showOverflow = false
                            action()
                        },
                    ),
                )
            }
            onOpenInBrowser?.let { action ->
                add(
                    GlassMenuItemRecord(
                        id = "open_in_browser",
                        title = browserLabel,
                        onClick = {
                            showOverflow = false
                            action()
                        },
                    ),
                )
            }
            onShare?.let { action ->
                add(
                    GlassMenuItemRecord(
                        id = "share",
                        title = shareLabel,
                        onClick = {
                            showOverflow = false
                            action()
                        },
                    ),
                )
            }
        }
    }

    // Compact Floating Apple Cupertino Top Bar
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
            // Frosted Circular Back Button (44dp)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .then(
                        if (useNativeGlass) {
                            Modifier
                        } else if (isGlass) {
                            Modifier
                                .liquidGlassSurface(
                                    shape = CircleShape,
                                    blurRadius = 3.dp,
                                    refractionHeight = 16.dp,
                                    refractionAmount = 24.dp,
                                    tintColor = glassTintColor,
                                )
                                .border(
                                    width = 0.5.dp,
                                    color = pillBorderColor,
                                    shape = CircleShape,
                                )
                        } else {
                            Modifier
                                .background(
                                    color = solidBackingBg,
                                    shape = CircleShape,
                                )
                                .border(
                                    width = 0.5.dp,
                                    color = pillBorderColor,
                                    shape = CircleShape,
                                )
                        },
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 22.dp),
                        onClick = navigateUp,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (useNativeGlass) {
                    NativeGlassSurface(
                        modifier = Modifier.matchParentSize(),
                        cornerRadius = 22.dp,
                        topCornersOnly = false,
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = stringResource(MR.strings.action_bar_up_description),
                    tint = primaryTextColor,
                )
            }

            // Smoothly Clickable Title Capsule (Tap to expand full top overview)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    if (useNativeGlass) {
                        NativeGlassSurface(
                            modifier = Modifier.matchParentSize(),
                            cornerRadius = 24.dp,
                            topCornersOnly = false,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .then(
                                if (useNativeGlass) {
                                    Modifier
                                } else if (isGlass) {
                                    Modifier
                                        .liquidGlassSurface(
                                            shape = Capsule(),
                                            blurRadius = 3.dp,
                                            refractionHeight = 16.dp,
                                            refractionAmount = 26.dp,
                                            tintColor = glassTintColor,
                                        )
                                        .border(
                                            width = 0.5.dp,
                                            color = pillBorderColor,
                                            shape = Capsule(),
                                        )
                                } else {
                                    Modifier
                                        .background(
                                            color = solidBackingBg,
                                            shape = Capsule(),
                                        )
                                        .border(
                                            width = 0.5.dp,
                                            color = pillBorderColor,
                                            shape = Capsule(),
                                        )
                                },
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onClickTopAppBar?.invoke()
                                },
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (!mangaTitle.isNullOrEmpty()) {
                            Text(
                                text = mangaTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = primaryTextColor,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                            )
                        }
                        if (!chapterTitle.isNullOrEmpty()) {
                            Text(
                                text = chapterTitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = secondaryTextColor,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Right Actions: Bookmark & Overflow (44dp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .then(
                            if (useNativeGlass) {
                                Modifier
                            } else if (isGlass) {
                                Modifier
                                    .liquidGlassSurface(
                                        shape = CircleShape,
                                        blurRadius = 3.dp,
                                        refractionHeight = 16.dp,
                                        refractionAmount = 24.dp,
                                        tintColor = glassTintColor,
                                    )
                                    .border(
                                        width = 0.5.dp,
                                        color = pillBorderColor,
                                        shape = CircleShape,
                                    )
                            } else {
                                Modifier
                                    .background(
                                        color = solidBackingBg,
                                        shape = CircleShape,
                                    )
                                    .border(
                                        width = 0.5.dp,
                                        color = pillBorderColor,
                                        shape = CircleShape,
                                    )
                            },
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 22.dp),
                            onClick = onToggleBookmarked,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (useNativeGlass) {
                        NativeGlassSurface(
                            modifier = Modifier.matchParentSize(),
                            cornerRadius = 22.dp,
                            topCornersOnly = false,
                        )
                    }
                    Icon(
                        imageVector = if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = stringResource(
                            if (bookmarked) MR.strings.action_remove_bookmark else MR.strings.action_bookmark,
                        ),
                        tint = if (bookmarked) MaterialTheme.colorScheme.primary else primaryTextColor,
                    )
                }

                if (hasOverflow) {
                    Box {
                        if (useNativeGlassMenu) {
                            GlassMenuAnchor(
                                expanded = showOverflow,
                                onDismissRequest = { showOverflow = false },
                                items = overflowGlassItems,
                                modifier = Modifier.size(44.dp),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .then(
                                    if (useNativeGlass) {
                                        Modifier
                                    } else if (isGlass) {
                                        Modifier
                                            .liquidGlassSurface(
                                                shape = CircleShape,
                                                blurRadius = 3.dp,
                                                refractionHeight = 16.dp,
                                                refractionAmount = 24.dp,
                                                tintColor = glassTintColor,
                                            )
                                            .border(
                                                width = 0.5.dp,
                                                color = pillBorderColor,
                                                shape = CircleShape,
                                            )
                                    } else {
                                        Modifier
                                            .background(
                                                color = solidBackingBg,
                                                shape = CircleShape,
                                            )
                                            .border(
                                                width = 0.5.dp,
                                                color = pillBorderColor,
                                                shape = CircleShape,
                                            )
                                    },
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, radius = 22.dp),
                                    onClick = { showOverflow = true },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (useNativeGlass) {
                                NativeGlassSurface(
                                    modifier = Modifier.matchParentSize(),
                                    cornerRadius = 22.dp,
                                    topCornersOnly = false,
                                )
                            }
                            Icon(
                                imageVector = Icons.Outlined.MoreHoriz,
                                contentDescription = stringResource(MR.strings.label_more),
                                tint = primaryTextColor,
                            )
                        }

                        if (!useNativeGlassMenu) {
                            CupertinoFrostedPopup(
                                expanded = showOverflow,
                                onDismissRequest = { showOverflow = false },
                            ) {
                                onOpenInWebView?.let {
                                    DropdownMenuItem(
                                        text = { Text(webViewLabel, color = primaryTextColor) },
                                        onClick = {
                                            showOverflow = false
                                            it()
                                        },
                                    )
                                    DropdownMenuSeparator()
                                }
                                onOpenInBrowser?.let {
                                    DropdownMenuItem(
                                        text = { Text(browserLabel, color = primaryTextColor) },
                                        onClick = {
                                            showOverflow = false
                                            it()
                                        },
                                    )
                                    DropdownMenuSeparator()
                                }
                                onShare?.let {
                                    DropdownMenuItem(
                                        text = { Text(shareLabel, color = primaryTextColor) },
                                        onClick = {
                                            showOverflow = false
                                            it()
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
