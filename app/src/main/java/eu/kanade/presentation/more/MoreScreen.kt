package eu.kanade.presentation.more

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.GetApp
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.more.settings.widget.SwitchPreferenceWidget
import eu.kanade.presentation.more.settings.widget.TextPreferenceWidget
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.more.DownloadQueueState
import eu.kanade.tachiyomi.util.system.openInBrowser
import exh.pref.DelegateSourcePreferences
import exh.source.ExhPreferences
import tachiyomi.core.common.Constants
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.TextButton
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.kyant.backdrop.catalog.components.LiquidButton
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle
import tachiyomi.presentation.core.components.material.CupertinoIconBadge
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig

@Composable
fun MoreScreen(
    downloadQueueStateProvider: () -> DownloadQueueState,
    downloadedOnly: Boolean,
    onDownloadedOnlyChange: (Boolean) -> Unit,
    incognitoMode: Boolean,
    onIncognitoModeChange: (Boolean) -> Unit,
    // SY -->
    showNavUpdates: Boolean,
    showNavHistory: Boolean,
    // SY <--
    onClickDownloadQueue: () -> Unit,
    onClickCategories: () -> Unit,
    onClickStats: () -> Unit,
    onClickDataAndStorage: () -> Unit,
    onClickSettings: () -> Unit,
    onClickAbout: () -> Unit,
    onClickBatchAdd: () -> Unit,
    onClickUpdates: () -> Unit,
    onClickHistory: () -> Unit,
    // KMK -->
    onClickLibraryUpdateErrors: () -> Unit,
    // KMK <--
) {
    val uriHandler = LocalUriHandler.current
    // SY -->
    val exhPreferences = remember { Injekt.get<ExhPreferences>() }
    val delegateSourcePreferences = remember { Injekt.get<DelegateSourcePreferences>() }
    // SY <--
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    val containerColor = if (isGlass) {
        if (isDark) Color(0xFF000000) else Color(0xFFF2F2F7)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Scaffold(
        containerColor = containerColor,
    ) { contentPadding ->
        val effectivePadding = if (isGlass) {
            PaddingValues(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding() + 96.dp,
            )
        } else {
            contentPadding
        }

        ScrollbarLazyColumn(
            // KMK: use contentPadding as preferable padding for ScrollbarLazyColumn when not using stickyHeader
            contentPadding = effectivePadding,
        ) {
            item {
                LogoHeader()
            }

            if (isGlass) {
                // Card 1: Downloaded only, Incognito mode, Updates, History, Download queue
                item {
                    val downloadQueueState = downloadQueueStateProvider()
                    val cardDividerColor = if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedRectangle(20.dp, RoundedCornerStyle.Continuous))
                            .background(if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)),
                    ) {
                        Column {
                            SwitchPreferenceWidget(
                                title = stringResource(MR.strings.label_downloaded_only),
                                subtitle = stringResource(MR.strings.downloaded_only_summary),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.Outlined.CloudOff,
                                        backgroundColor = Color(0xFFFF9500),
                                    )
                                },
                                checked = downloadedOnly,
                                onCheckedChanged = onDownloadedOnlyChange,
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 58.dp),
                                thickness = 0.5.dp,
                                color = cardDividerColor,
                            )
                            SwitchPreferenceWidget(
                                title = stringResource(MR.strings.pref_incognito_mode),
                                subtitle = stringResource(MR.strings.pref_incognito_mode_summary),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = rememberAnimatedVectorPainter(
                                            AnimatedImageVector.animatedVectorResource(R.drawable.anim_incognito),
                                            incognitoMode,
                                        ),
                                        backgroundColor = Color(0xFF5856D6),
                                    )
                                },
                                checked = incognitoMode,
                                onCheckedChanged = onIncognitoModeChange,
                            )
                            // SY -->
                            if (!showNavUpdates) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 58.dp),
                                    thickness = 0.5.dp,
                                    color = cardDividerColor,
                                )
                                TextPreferenceWidget(
                                    title = stringResource(MR.strings.label_recent_updates),
                                    iconContent = {
                                        CupertinoIconBadge(
                                            icon = Icons.Outlined.NewReleases,
                                            backgroundColor = Color(0xFF007AFF),
                                        )
                                    },
                                    onPreferenceClick = onClickUpdates,
                                )
                            }
                            if (!showNavHistory) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 58.dp),
                                    thickness = 0.5.dp,
                                    color = cardDividerColor,
                                )
                                TextPreferenceWidget(
                                    title = stringResource(MR.strings.label_recent_manga),
                                    iconContent = {
                                        CupertinoIconBadge(
                                            icon = Icons.Outlined.History,
                                            backgroundColor = Color(0xFFFF9500),
                                        )
                                    },
                                    onPreferenceClick = onClickHistory,
                                )
                            }
                            // SY <--
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 58.dp),
                                thickness = 0.5.dp,
                                color = cardDividerColor,
                            )
                            val downloadQueueStateText = when (downloadQueueState) {
                                DownloadQueueState.Stopped -> null
                                is DownloadQueueState.Paused -> {
                                    val pending = downloadQueueState.pending
                                    if (pending == 0) {
                                        stringResource(MR.strings.paused)
                                    } else {
                                        "${stringResource(MR.strings.paused)} • ${
                                            pluralStringResource(
                                                MR.plurals.download_queue_summary,
                                                count = pending,
                                                pending,
                                            )
                                        }"
                                    }
                                }
                                is DownloadQueueState.Downloading -> {
                                    val pending = downloadQueueState.pending
                                    pluralStringResource(MR.plurals.download_queue_summary, count = pending, pending)
                                }
                            }
                            TextPreferenceWidget(
                                title = stringResource(MR.strings.label_download_queue),
                                subtitle = downloadQueueStateText,
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.Outlined.GetApp,
                                        backgroundColor = Color(0xFF007AFF),
                                    )
                                },
                                onPreferenceClick = onClickDownloadQueue,
                            )
                        }
                    }
                }

                // Card 2: Categories, Stats, Library update errors, Data and storage, Batch Add
                item {
                    val cardDividerColor = if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedRectangle(20.dp, RoundedCornerStyle.Continuous))
                            .background(if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)),
                    ) {
                        Column {
                            TextPreferenceWidget(
                                title = stringResource(MR.strings.categories),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.AutoMirrored.Outlined.Label,
                                        backgroundColor = Color(0xFFFF9500),
                                    )
                                },
                                onPreferenceClick = onClickCategories,
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 58.dp),
                                thickness = 0.5.dp,
                                color = cardDividerColor,
                            )
                            TextPreferenceWidget(
                                title = stringResource(MR.strings.label_stats),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.Outlined.QueryStats,
                                        backgroundColor = Color(0xFF34C759),
                                    )
                                },
                                onPreferenceClick = onClickStats,
                            )
                            // KMK -->
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 58.dp),
                                thickness = 0.5.dp,
                                color = cardDividerColor,
                            )
                            TextPreferenceWidget(
                                title = stringResource(KMR.strings.option_label_library_update_errors),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.Outlined.NewReleases,
                                        backgroundColor = Color(0xFFFF3B30),
                                    )
                                },
                                onPreferenceClick = onClickLibraryUpdateErrors,
                            )
                            // KMK <--
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 58.dp),
                                thickness = 0.5.dp,
                                color = cardDividerColor,
                            )
                            TextPreferenceWidget(
                                title = stringResource(MR.strings.label_data_storage),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.Outlined.Storage,
                                        backgroundColor = Color(0xFF5856D6),
                                    )
                                },
                                onPreferenceClick = onClickDataAndStorage,
                            )
                            // SY -->
                            if (exhPreferences.isHentaiEnabled().get() || delegateSourcePreferences.delegateSources().get()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 58.dp),
                                    thickness = 0.5.dp,
                                    color = cardDividerColor,
                                )
                                TextPreferenceWidget(
                                    title = stringResource(SYMR.strings.eh_batch_add),
                                    iconContent = {
                                        CupertinoIconBadge(
                                            icon = Icons.AutoMirrored.Outlined.PlaylistAdd,
                                            backgroundColor = Color(0xFFAF52DE),
                                        )
                                    },
                                    onPreferenceClick = onClickBatchAdd,
                                )
                            }
                            // SY <--
                        }
                    }
                }

                // Card 3: Settings, About, Help
                item {
                    val cardDividerColor = if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedRectangle(20.dp, RoundedCornerStyle.Continuous))
                            .background(if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)),
                    ) {
                        Column {
                            TextPreferenceWidget(
                                title = stringResource(MR.strings.label_settings),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.Outlined.Settings,
                                        backgroundColor = Color(0xFF8E8E93),
                                    )
                                },
                                onPreferenceClick = onClickSettings,
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 58.dp),
                                thickness = 0.5.dp,
                                color = cardDividerColor,
                            )
                            TextPreferenceWidget(
                                title = stringResource(MR.strings.pref_category_about),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.Outlined.Info,
                                        backgroundColor = Color(0xFF007AFF),
                                    )
                                },
                                onPreferenceClick = onClickAbout,
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 58.dp),
                                thickness = 0.5.dp,
                                color = cardDividerColor,
                            )
                            TextPreferenceWidget(
                                title = stringResource(MR.strings.label_help),
                                iconContent = {
                                    CupertinoIconBadge(
                                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                                        backgroundColor = Color(0xFF32ADE6),
                                    )
                                },
                                onPreferenceClick = { uriHandler.openUri(Constants.URL_HELP) },
                            )
                        }
                    }
                }
            } else {
                item {
                    SwitchPreferenceWidget(
                        title = stringResource(MR.strings.label_downloaded_only),
                        subtitle = stringResource(MR.strings.downloaded_only_summary),
                        icon = Icons.Outlined.CloudOff,
                        checked = downloadedOnly,
                        onCheckedChanged = onDownloadedOnlyChange,
                    )
                }
                item {
                    SwitchPreferenceWidget(
                        title = stringResource(MR.strings.pref_incognito_mode),
                        subtitle = stringResource(MR.strings.pref_incognito_mode_summary),
                        // KMK -->
                        icon = rememberAnimatedVectorPainter(
                            AnimatedImageVector.animatedVectorResource(R.drawable.anim_incognito),
                            incognitoMode,
                        ),
                        // KMK <--
                        checked = incognitoMode,
                        onCheckedChanged = onIncognitoModeChange,
                    )
                }

                item { HorizontalDivider() }

                // SY -->
                if (!showNavUpdates) {
                    item {
                        TextPreferenceWidget(
                            title = stringResource(MR.strings.label_recent_updates),
                            icon = Icons.Outlined.NewReleases,
                            onPreferenceClick = onClickUpdates,
                        )
                    }
                }
                if (!showNavHistory) {
                    item {
                        TextPreferenceWidget(
                            title = stringResource(MR.strings.label_recent_manga),
                            icon = Icons.Outlined.History,
                            onPreferenceClick = onClickHistory,
                        )
                    }
                }
                // SY <--

                item {
                    val downloadQueueState = downloadQueueStateProvider()
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.label_download_queue),
                        subtitle = when (downloadQueueState) {
                            DownloadQueueState.Stopped -> null
                            is DownloadQueueState.Paused -> {
                                val pending = downloadQueueState.pending
                                if (pending == 0) {
                                    stringResource(MR.strings.paused)
                                } else {
                                    "${stringResource(MR.strings.paused)} • ${
                                        pluralStringResource(
                                            MR.plurals.download_queue_summary,
                                            count = pending,
                                            pending,
                                        )
                                    }"
                                }
                            }
                            is DownloadQueueState.Downloading -> {
                                val pending = downloadQueueState.pending
                                pluralStringResource(MR.plurals.download_queue_summary, count = pending, pending)
                            }
                        },
                        icon = Icons.Outlined.GetApp,
                        onPreferenceClick = onClickDownloadQueue,
                    )
                }
                item {
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.categories),
                        icon = Icons.AutoMirrored.Outlined.Label,
                        onPreferenceClick = onClickCategories,
                    )
                }
                item {
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.label_stats),
                        icon = Icons.Outlined.QueryStats,
                        onPreferenceClick = onClickStats,
                    )
                }
                // KMK -->
                item {
                    TextPreferenceWidget(
                        title = stringResource(KMR.strings.option_label_library_update_errors),
                        icon = Icons.Outlined.NewReleases,
                        onPreferenceClick = onClickLibraryUpdateErrors,
                    )
                }
                // KMK <--
                item {
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.label_data_storage),
                        icon = Icons.Outlined.Storage,
                        onPreferenceClick = onClickDataAndStorage,
                    )
                }
                // SY -->
                if (exhPreferences.isHentaiEnabled().get() || delegateSourcePreferences.delegateSources().get()) {
                    item {
                        TextPreferenceWidget(
                            title = stringResource(SYMR.strings.eh_batch_add),
                            icon = Icons.AutoMirrored.Outlined.PlaylistAdd,
                            onPreferenceClick = onClickBatchAdd,
                        )
                    }
                }
                // SY <--

                item { HorizontalDivider() }

                item {
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.label_settings),
                        icon = Icons.Outlined.Settings,
                        onPreferenceClick = onClickSettings,
                    )
                }
                item {
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.pref_category_about),
                        icon = Icons.Outlined.Info,
                        onPreferenceClick = onClickAbout,
                    )
                }
                item {
                    TextPreferenceWidget(
                        title = stringResource(MR.strings.label_help),
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        onPreferenceClick = { uriHandler.openUri(Constants.URL_HELP) },
                    )
                }
            }

            // KMK -->
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Sponsor()
            }
            // KMK <--
        }
    }
}

// KMK -->
@Composable
fun Sponsor() {
    val context = LocalContext.current
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        if (isGlass) {
            LiquidButton(
                onClick = { context.openInBrowser(Constants.SPONSOR) },
                tint = Color(0xFFFF2D55),
                shape = Capsule(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFF2D55),
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = stringResource(KMR.strings.sponsor_me),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isDark) Color.White else Color(0xFF1C1C1E),
                    )
                }
            }
        } else {
            TextButton(
                onClick = { context.openInBrowser(Constants.SPONSOR) },
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small,
                    ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = stringResource(KMR.strings.sponsor_me),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(KMR.strings.sponsor_me),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SponsorPreview() {
    TachiyomiPreviewTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 0.dp,
        ) {
            Sponsor()
        }
    }
}
// KMK <--
