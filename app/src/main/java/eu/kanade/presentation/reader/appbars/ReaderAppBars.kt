package eu.kanade.presentation.reader.appbars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kyant.shapes.Capsule
import eu.kanade.presentation.reader.components.ChapterNavigator
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import eu.kanade.tachiyomi.ui.reader.viewer.Viewer
import eu.kanade.tachiyomi.ui.reader.viewer.pager.R2LPagerViewer
import kotlinx.collections.immutable.ImmutableSet
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

private val readerBarsSlideAnimationSpec = tween<IntOffset>(200)
private val readerBarsFadeAnimationSpec = tween<Float>(150)

// SY -->
enum class NavBarType {
    VerticalRight,
    VerticalLeft,
    Bottom,
}
// SY <--

@Composable
fun ReaderAppBars(
    visible: Boolean,

    mangaTitle: String?,
    chapterTitle: String?,
    navigateUp: () -> Unit,
    onClickTopAppBar: () -> Unit,
    bookmarked: Boolean,
    onToggleBookmarked: () -> Unit,
    onOpenInWebView: (() -> Unit)?,
    onOpenInBrowser: (() -> Unit)?,
    onShare: (() -> Unit)?,

    viewer: Viewer?,
    onNextChapter: () -> Unit,
    enabledNext: Boolean,
    onPreviousChapter: () -> Unit,
    enabledPrevious: Boolean,
    currentPage: Int,
    totalPages: Int,
    onPageIndexChange: (Int) -> Unit,
    onPageFloatChange: ((Float) -> Unit)? = null,

    readingMode: ReadingMode,
    onClickReadingMode: () -> Unit,
    orientation: ReaderOrientation,
    onClickOrientation: () -> Unit,
    cropEnabled: Boolean,
    onClickCropBorder: () -> Unit,
    onClickSettings: () -> Unit,
    // SY -->
    isExhToolsVisible: Boolean,
    onSetExhUtilsVisibility: (Boolean) -> Unit,
    isAutoScroll: Boolean,
    isAutoScrollEnabled: Boolean,
    onToggleAutoscroll: (Boolean) -> Unit,
    autoScrollFrequency: String,
    onSetAutoScrollFrequency: (String) -> Unit,
    onClickAutoScrollHelp: () -> Unit,
    onClickRetryAll: () -> Unit,
    onClickRetryAllHelp: () -> Unit,
    onClickBoostPage: () -> Unit,
    onClickBoostPageHelp: () -> Unit,
    navBarType: NavBarType,
    currentPageText: String,
    enabledButtons: ImmutableSet<String>,
    currentReadingMode: ReadingMode,
    dualPageSplitEnabled: Boolean,
    doublePages: Boolean,
    onClickChapterList: () -> Unit,
    onClickPageLayout: () -> Unit,
    onClickShiftPage: () -> Unit,
    isTopLight: Boolean = true,
    isBottomLight: Boolean = true,
    showBreakReminder: Boolean = false,
    onDismissBreakReminder: () -> Unit = {},
    onSnoozeBreakReminder: () -> Unit = {},
    pagesTurnedInSession: Int = 0,
    sessionDurationSeconds: Long = 0L,
    // SY <--
) {
    val isRtl = viewer is R2LPagerViewer
    val backgroundColor = MaterialTheme.colorScheme
        .surfaceColorAtElevation(3.dp)
        .copy(alpha = if (isSystemInDarkTheme()) 0.9f else 0.95f)

    val isGlass = LocalLiquidGlassConfig.current.enabled
    val topBarModifier = if (isGlass) {
        Modifier
            .statusBarsPadding()
            .displayCutoutPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    } else {
        Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClickTopAppBar)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { -it }, animationSpec = readerBarsSlideAnimationSpec) +
                fadeIn(animationSpec = readerBarsFadeAnimationSpec),
            exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = readerBarsSlideAnimationSpec) +
                fadeOut(animationSpec = readerBarsFadeAnimationSpec),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            ReaderTopBar(
                modifier = topBarModifier,
                mangaTitle = mangaTitle,
                chapterTitle = chapterTitle,
                navigateUp = navigateUp,
                bookmarked = bookmarked,
                onToggleBookmarked = onToggleBookmarked,
                // SY -->
                onOpenInWebView = onOpenInWebView,
                onOpenInBrowser = onOpenInBrowser,
                onShare = onShare,
                // SY <--
                onClickTopAppBar = onClickTopAppBar,
                currentPage = currentPage,
                totalPages = totalPages,
                readingModeName = stringResource(currentReadingMode.stringRes),
                orientationName = stringResource(orientation.stringRes),
                onPageIndexChange = onPageIndexChange,
                cropEnabled = cropEnabled,
                onClickCropBorder = onClickCropBorder,
                onClickReadingMode = onClickReadingMode,
                onClickOrientation = onClickOrientation,
                onClickChapterList = onClickChapterList,
                isTopLight = isTopLight,
                pagesTurnedInSession = pagesTurnedInSession,
                sessionDurationSeconds = sessionDurationSeconds,
            )
        }

        // Smart Eye Rest Break Reminder Floating Banner
        AnimatedVisibility(
            visible = showBreakReminder,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            val isDark = isSystemInDarkTheme()
            val bannerBg = if (isGlass) {
                if (isDark) Color(0xDC202024) else Color(0xF4FAFAFC)
            } else {
                if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
            }
            val textColor = if (isDark) Color.White else Color(0xFF111113)

            Row(
                modifier = Modifier
                    .clip(Capsule())
                    .then(
                        if (isGlass) {
                            Modifier.liquidGlassSurface(
                                shape = Capsule(),
                                blurRadius = 14.dp,
                                surfaceColor = bannerBg,
                            )
                        } else {
                            Modifier.background(bannerBg)
                        },
                    )
                    .border(
                        0.5.dp,
                        if (isDark) Color(0x35FFFFFF) else Color(0x25000000),
                        Capsule(),
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "👀 Eye Break",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    ),
                )
                Text(
                    text = "Rest eyes for 20s",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = textColor.copy(alpha = 0.8f),
                    ),
                )
                Box(
                    modifier = Modifier
                        .clip(Capsule())
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onDismissBreakReminder() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    val primary = MaterialTheme.colorScheme.primary
                    val lum = 0.299f * primary.red + 0.587f * primary.green + 0.114f * primary.blue
                    val btnCol = if (lum > 0.45f) Color(0xFF111113) else Color.White
                    Text(
                        text = "Dismiss",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = btnCol,
                        ),
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(Capsule())
                        .background(if (isDark) Color(0x25FFFFFF) else Color(0x15000000))
                        .clickable { onSnoozeBreakReminder() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "10m",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                        ),
                    )
                }
            }
        }

        // KMK -->
        when (navBarType) {
            NavBarType.VerticalLeft -> {
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = readerBarsSlideAnimationSpec,
                    ) +
                        fadeIn(animationSpec = readerBarsFadeAnimationSpec),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = readerBarsSlideAnimationSpec,
                    ) +
                        fadeOut(animationSpec = readerBarsFadeAnimationSpec),
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    ChapterNavigator(
                        isRtl = isRtl,
                        onNextChapter = onNextChapter,
                        enabledNext = enabledNext,
                        onPreviousChapter = onPreviousChapter,
                        enabledPrevious = enabledPrevious,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        onPageIndexChange = onPageIndexChange,
                        onPageFloatChange = onPageFloatChange,
                        // SY -->
                        isVerticalSlider = true,
                        currentPageText = currentPageText,
                        isBottomLight = isBottomLight,
                        // SY <--
                    )
                }
            }

            NavBarType.VerticalRight -> {
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = readerBarsSlideAnimationSpec,
                    ) +
                        fadeIn(animationSpec = readerBarsFadeAnimationSpec),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = readerBarsSlideAnimationSpec,
                    ) +
                        fadeOut(animationSpec = readerBarsFadeAnimationSpec),
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    ChapterNavigator(
                        isRtl = isRtl,
                        onNextChapter = onNextChapter,
                        enabledNext = enabledNext,
                        onPreviousChapter = onPreviousChapter,
                        enabledPrevious = enabledPrevious,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        onPageIndexChange = onPageIndexChange,
                        onPageFloatChange = onPageFloatChange,
                        // SY -->
                        isVerticalSlider = true,
                        currentPageText = currentPageText,
                        isBottomLight = isBottomLight,
                        // SY <--
                    )
                }
            }
            // KMK <--
            else -> {}
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = readerBarsSlideAnimationSpec) +
                fadeIn(animationSpec = readerBarsFadeAnimationSpec),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = readerBarsSlideAnimationSpec) +
                fadeOut(animationSpec = readerBarsFadeAnimationSpec),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small)) {
                // SY -->
                if (navBarType == NavBarType.Bottom) {
                    // SY <--
                    ChapterNavigator(
                        isRtl = isRtl,
                        onNextChapter = onNextChapter,
                        enabledNext = enabledNext,
                        onPreviousChapter = onPreviousChapter,
                        enabledPrevious = enabledPrevious,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        onPageIndexChange = onPageIndexChange,
                        onPageFloatChange = onPageFloatChange,
                        // SY -->
                        isVerticalSlider = false,
                        currentPageText = currentPageText,
                        isBottomLight = isBottomLight,
                        // SY <--
                    )
                }
                val bottomBarModifier = if (isGlass) {
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(60.dp)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(horizontal = MaterialTheme.padding.small)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                }
                ReaderBottomBar(
                    modifier = bottomBarModifier,
                    readingMode = readingMode,
                    onClickReadingMode = onClickReadingMode,
                    orientation = orientation,
                    onClickOrientation = onClickOrientation,
                    cropEnabled = cropEnabled,
                    onClickCropBorder = onClickCropBorder,
                    onClickSettings = onClickSettings,
                    // SY -->
                    enabledButtons = enabledButtons,
                    currentReadingMode = currentReadingMode,
                    dualPageSplitEnabled = dualPageSplitEnabled,
                    doublePages = doublePages,
                    onClickChapterList = onClickChapterList,
                    onClickWebView = onOpenInWebView,
                    onClickBrowser = onOpenInBrowser,
                    onClickShare = onShare,
                    onClickPageLayout = onClickPageLayout,
                    onClickShiftPage = onClickShiftPage,
                    isBottomLight = isBottomLight,
                    // SY <--
                )
            }
        }
    }
}
