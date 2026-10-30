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
import androidx.compose.material3.DropdownMenuItem
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
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.CupertinoFrostedPopup
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassPopupSurface
import tachiyomi.presentation.core.components.material.liquidGlassSurface
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
    onClickChapterList: (() -> Unit)? = null,
    isTopLight: Boolean = true,
    pagesTurnedInSession: Int = 0,
    sessionDurationSeconds: Long = 0L,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    // Dynamic contrast based on sampled background luminance:
    val targetPrimary = if (isTopLight) Color(0xFF111113) else Color.White
    val targetSecondary = if (isTopLight) Color(0xFF3C3C43).copy(alpha = 0.85f) else Color(0xCCFFFFFF)
    val targetPillBorder = if (isTopLight) Color(0x25000000) else Color(0x35FFFFFF)
    val targetSurfacePillBg = if (isTopLight) Color(0x18000000) else Color(0x28FFFFFF)
    val targetGlassTint = if (isTopLight) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.12f)
    val targetOverviewSurface = if (isTopLight) Color(0xEEFFFFFF) else Color(0xD81C1C20)
    val solidBackingBg = if (isTopLight) Color(0xF4FAFAFC) else Color(0xEA202024)

    val primaryTextColor by animateColorAsState(targetPrimary, animationSpec = tween(250), label = "topBarPrimary")
    val secondaryTextColor by animateColorAsState(targetSecondary, animationSpec = tween(250), label = "topBarSecondary")
    val pillBorderColor by animateColorAsState(targetPillBorder, animationSpec = tween(250), label = "topBarBorder")
    val surfacePillBg by animateColorAsState(targetSurfacePillBg, animationSpec = tween(250), label = "topBarPillBg")
    val glassTintColor by animateColorAsState(targetGlassTint, animationSpec = tween(250), label = "topBarGlassTint")
    val overviewSurfaceColor by animateColorAsState(targetOverviewSurface, animationSpec = tween(250), label = "topBarOverviewSurface")

    val activePillBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
    val activePillText = MaterialTheme.colorScheme.primary
    val haptic = LocalHapticFeedback.current

    val readerPreferences = remember { Injekt.get<ReaderPreferences>() }
    val readingRulerPref = remember { readerPreferences.readerReadingRuler() }
    val readingRulerEnabled by readingRulerPref.collectAsState()
    val eyeCareFilterPref = remember { readerPreferences.readerEyeCareFilter() }
    val eyeCareFilter by eyeCareFilterPref.collectAsState()
    val invertColorsPref = remember { readerPreferences.readerInvertColors() }
    val invertColors by invertColorsPref.collectAsState()
    var showOverflow by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    val hasOverflow = onOpenInWebView != null || onOpenInBrowser != null || onShare != null

    if (isExpanded) {
        // Full-Width Apple Cupertino Overview Sheet (occupies top 1/3 to 1/2 of screen)
        Box(
            modifier = modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    )
                    .then(
                        if (isGlass) {
                            Modifier.liquidGlassPopupSurface(
                                shape = RoundedCornerShape(28.dp),
                                surfaceColor = if (isTopLight) Color(0x55FFFFFF) else Color(0x3816161A),
                            )
                        } else {
                            Modifier
                                .background(
                                    color = overviewSurfaceColor,
                                    shape = RoundedCornerShape(28.dp),
                                )
                                .border(
                                    width = 0.5.dp,
                                    color = pillBorderColor,
                                    shape = RoundedCornerShape(28.dp),
                                )
                        },
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                    // Top drag grabber handle
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 4.5.dp)
                            .clip(Capsule())
                            .background(secondaryTextColor.copy(alpha = 0.35f))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isExpanded = false
                            },
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Header row: Back button, Title & Chapter, Bookmark, Collapse, Overflow
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Back button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(surfacePillBg)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, radius = 19.dp),
                                    onClick = navigateUp,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                contentDescription = stringResource(MR.strings.action_bar_up_description),
                                tint = primaryTextColor,
                                modifier = Modifier.size(20.dp),
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onClickTopAppBar?.invoke()
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (!mangaTitle.isNullOrEmpty()) {
                                Text(
                                    text = mangaTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = primaryTextColor,
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                )
                            }
                            if (!chapterTitle.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = chapterTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = secondaryTextColor,
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Bookmark toggle
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (bookmarked) activePillBg else surfacePillBg)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, radius = 19.dp),
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onToggleBookmarked()
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = stringResource(
                                    if (bookmarked) MR.strings.action_remove_bookmark else MR.strings.action_bookmark,
                                ),
                                tint = if (bookmarked) activePillText else primaryTextColor,
                                modifier = Modifier.size(19.dp),
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Collapse overview button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(surfacePillBg)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, radius = 19.dp),
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        isExpanded = false
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowUp,
                                contentDescription = "Collapse overview",
                                tint = primaryTextColor,
                                modifier = Modifier.size(20.dp),
                            )
                        }

                        if (hasOverflow) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(surfacePillBg)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple(bounded = true, radius = 19.dp),
                                            onClick = { showOverflow = true },
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.MoreHoriz,
                                        contentDescription = stringResource(MR.strings.label_more),
                                        tint = primaryTextColor,
                                        modifier = Modifier.size(19.dp),
                                    )
                                }

                                CupertinoFrostedPopup(
                                    expanded = showOverflow,
                                    onDismissRequest = { showOverflow = false },
                                ) {
                                    onOpenInWebView?.let {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(MR.strings.action_open_in_web_view), color = primaryTextColor) },
                                            colors = androidx.compose.material3.MenuDefaults.itemColors(
                                                textColor = primaryTextColor,
                                                leadingIconColor = primaryTextColor,
                                                trailingIconColor = primaryTextColor,
                                            ),
                                            onClick = {
                                                showOverflow = false
                                                it()
                                            },
                                        )
                                    }
                                    onOpenInBrowser?.let {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(MR.strings.action_open_in_browser), color = primaryTextColor) },
                                            colors = androidx.compose.material3.MenuDefaults.itemColors(
                                                textColor = primaryTextColor,
                                                leadingIconColor = primaryTextColor,
                                                trailingIconColor = primaryTextColor,
                                            ),
                                            onClick = {
                                                showOverflow = false
                                                it()
                                            },
                                        )
                                    }
                                    onShare?.let {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(MR.strings.action_share), color = primaryTextColor) },
                                            colors = androidx.compose.material3.MenuDefaults.itemColors(
                                                textColor = primaryTextColor,
                                                leadingIconColor = primaryTextColor,
                                                trailingIconColor = primaryTextColor,
                                            ),
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

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = pillBorderColor,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Reading session metadata badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (totalPages > 0) {
                            val percent = (currentPage * 100 / totalPages.coerceAtLeast(1))
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(surfacePillBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "📄 $currentPage / $totalPages ($percent%)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = primaryTextColor,
                                    ),
                                )
                            }
                        }

                        if (totalPages > 0 && currentPage > 0) {
                            val remainingPages = (totalPages - currentPage).coerceAtLeast(0)
                            val ppm = if (sessionDurationSeconds >= 20L && pagesTurnedInSession > 0) {
                                (pagesTurnedInSession * 60f) / sessionDurationSeconds
                            } else {
                                2.2f
                            }
                            val etaMin = ceil(remainingPages / ppm).toInt()
                            val paceText = if (sessionDurationSeconds >= 30L && pagesTurnedInSession > 0) {
                                "⚡ %.1f p/m • ~%dm left".format(ppm, etaMin)
                            } else if (etaMin > 0) {
                                "⚡ ~${etaMin}m left"
                            } else {
                                "⚡ Final page"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(surfacePillBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = paceText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = activePillText,
                                    ),
                                )
                            }
                        }

                        if (!readingModeName.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(surfacePillBg)
                                    .clickable(enabled = onClickReadingMode != null) {
                                        onClickReadingMode?.invoke()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "📖 $readingModeName",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        color = primaryTextColor,
                                    ),
                                )
                            }
                        }

                        if (!orientationName.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(surfacePillBg)
                                    .clickable(enabled = onClickOrientation != null) {
                                        onClickOrientation?.invoke()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "🔄 $orientationName",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        color = primaryTextColor,
                                    ),
                                )
                            }
                        }
                    }

                    // Interactive chapter page scrubber slider
                    if (totalPages > 1 && onPageIndexChange != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "$currentPage",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = primaryTextColor,
                                    fontSize = 11.sp,
                                ),
                            )
                            val safeTotalPages = maxOf(1, totalPages)
                            val safeCurrentPageFloat = currentPage.toFloat().coerceIn(1f, safeTotalPages.toFloat())
                            Slider(
                                value = safeCurrentPageFloat,
                                onValueChange = { nextVal ->
                                    val targetPage = nextVal.toInt().coerceIn(1, safeTotalPages)
                                    if (targetPage != currentPage) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onPageIndexChange(targetPage)
                                    }
                                },
                                valueRange = 1f..safeTotalPages.toFloat(),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                            )
                            Text(
                                text = "$totalPages",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryTextColor,
                                    fontSize = 11.sp,
                                ),
                            )
                        }
                    }

                    // Quick action pills row
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Eye Care Mode Cycler
                        val eyeComfortLabel = when (eyeCareFilter) {
                            1 -> "Sepia"
                            2 -> "Night"
                            3 -> "OLED"
                            else -> "Eye Care: Off"
                        }
                        Box(
                            modifier = Modifier
                                .clip(Capsule())
                                .background(if (eyeCareFilter > 0) activePillBg else surfacePillBg)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    eyeCareFilterPref.set((eyeCareFilter + 1) % 4)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "🌙 $eyeComfortLabel",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (eyeCareFilter > 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (eyeCareFilter > 0) activePillText else primaryTextColor,
                                ),
                            )
                        }

                        // Reading Ruler Toggle
                        Box(
                            modifier = Modifier
                                .clip(Capsule())
                                .background(if (readingRulerEnabled) activePillBg else surfacePillBg)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    readingRulerPref.set(!readingRulerEnabled)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = if (readingRulerEnabled) "📏 Ruler: On" else "📏 Ruler",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (readingRulerEnabled) FontWeight.Bold else FontWeight.Medium,
                                    color = if (readingRulerEnabled) activePillText else primaryTextColor,
                                ),
                            )
                        }

                        // Invert Colors Toggle
                        Box(
                            modifier = Modifier
                                .clip(Capsule())
                                .background(if (invertColors) activePillBg else surfacePillBg)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    invertColorsPref.set(!invertColors)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = if (invertColors) "🌓 Invert: On" else "🌓 Invert",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (invertColors) FontWeight.Bold else FontWeight.Medium,
                                    color = if (invertColors) activePillText else primaryTextColor,
                                ),
                            )
                        }

                        // Crop Borders Toggle
                        if (onClickCropBorder != null) {
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(if (cropEnabled) activePillBg else surfacePillBg)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onClickCropBorder()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = if (cropEnabled) "✂️ Crop: On" else "✂️ Crop",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (cropEnabled) FontWeight.Bold else FontWeight.Medium,
                                        color = if (cropEnabled) activePillText else primaryTextColor,
                                    ),
                                )
                            }
                        }

                        // Chapter List Drawer
                        if (onClickChapterList != null) {
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(surfacePillBg)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onClickChapterList()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = "📑 Chapters",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = primaryTextColor,
                                    ),
                                )
                            }
                        }

                        // Series Details
                        if (onClickTopAppBar != null) {
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(surfacePillBg)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onClickTopAppBar()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = "📖 Details",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = primaryTextColor,
                                    ),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom grabber handle
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 4.5.dp)
                            .clip(Capsule())
                            .background(secondaryTextColor.copy(alpha = 0.35f))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isExpanded = false
                            },
                    )
                }
            }
        } else {
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
                            if (isGlass) {
                                Modifier.liquidGlassSurface(
                                    shape = CircleShape,
                                    blurRadius = 3.dp,
                                    refractionHeight = 16.dp,
                                    refractionAmount = 24.dp,
                                    tintColor = glassTintColor,
                                )
                            } else {
                                Modifier.background(
                                    color = solidBackingBg,
                                    shape = CircleShape,
                                )
                            },
                        )
                        .border(
                            width = 0.5.dp,
                            color = pillBorderColor,
                            shape = CircleShape,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 22.dp),
                            onClick = navigateUp,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
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
                    Column(
                        modifier = Modifier
                            .then(
                                if (isGlass) {
                                    Modifier.liquidGlassSurface(
                                        shape = Capsule(),
                                        blurRadius = 3.dp,
                                        refractionHeight = 16.dp,
                                        refractionAmount = 26.dp,
                                        tintColor = glassTintColor,
                                    )
                                } else {
                                    Modifier.background(
                                        color = solidBackingBg,
                                        shape = Capsule(),
                                    )
                                },
                            )
                            .border(
                                width = 0.5.dp,
                                color = pillBorderColor,
                                shape = Capsule(),
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    isExpanded = true
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

                // Right Actions: Bookmark & Overflow (44dp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .then(
                                if (isGlass) {
                                    Modifier.liquidGlassSurface(
                                        shape = CircleShape,
                                        blurRadius = 3.dp,
                                        refractionHeight = 16.dp,
                                        refractionAmount = 24.dp,
                                        tintColor = glassTintColor,
                                    )
                                } else {
                                    Modifier.background(
                                        color = solidBackingBg,
                                        shape = CircleShape,
                                    )
                                },
                            )
                            .border(
                                width = 0.5.dp,
                                color = pillBorderColor,
                                shape = CircleShape,
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, radius = 22.dp),
                                onClick = onToggleBookmarked,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
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
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .then(
                                        if (isGlass) {
                                            Modifier.liquidGlassSurface(
                                                shape = CircleShape,
                                                blurRadius = 3.dp,
                                                refractionHeight = 16.dp,
                                                refractionAmount = 24.dp,
                                                tintColor = glassTintColor,
                                            )
                                        } else {
                                            Modifier.background(
                                                color = solidBackingBg,
                                                shape = CircleShape,
                                            )
                                        },
                                    )
                                    .border(
                                        width = 0.5.dp,
                                        color = pillBorderColor,
                                        shape = CircleShape,
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = true, radius = 22.dp),
                                        onClick = { showOverflow = true },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreHoriz,
                                    contentDescription = stringResource(MR.strings.label_more),
                                    tint = primaryTextColor,
                                )
                            }

                            CupertinoFrostedPopup(
                                expanded = showOverflow,
                                onDismissRequest = { showOverflow = false },
                            ) {
                                onOpenInWebView?.let {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(MR.strings.action_open_in_web_view), color = primaryTextColor) },
                                        colors = androidx.compose.material3.MenuDefaults.itemColors(
                                            textColor = primaryTextColor,
                                            leadingIconColor = primaryTextColor,
                                            trailingIconColor = primaryTextColor,
                                        ),
                                        onClick = {
                                            showOverflow = false
                                            it()
                                        },
                                    )
                                }
                                onOpenInBrowser?.let {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(MR.strings.action_open_in_browser), color = primaryTextColor) },
                                        colors = androidx.compose.material3.MenuDefaults.itemColors(
                                            textColor = primaryTextColor,
                                            leadingIconColor = primaryTextColor,
                                            trailingIconColor = primaryTextColor,
                                        ),
                                        onClick = {
                                            showOverflow = false
                                            it()
                                        },
                                    )
                                }
                                onShare?.let {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(MR.strings.action_share), color = primaryTextColor) },
                                        colors = androidx.compose.material3.MenuDefaults.itemColors(
                                            textColor = primaryTextColor,
                                            leadingIconColor = primaryTextColor,
                                            trailingIconColor = primaryTextColor,
                                        ),
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
