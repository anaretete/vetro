package eu.kanade.presentation.reader.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import eu.kanade.domain.manga.model.readerOrientation
import eu.kanade.domain.manga.model.readingMode
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import eu.kanade.tachiyomi.ui.reader.viewer.webtoon.WebtoonViewer
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.CheckboxItem
import tachiyomi.presentation.core.components.HeadingItem
import tachiyomi.presentation.core.components.SelectItem
import tachiyomi.presentation.core.components.SettingsChipRow
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.components.material.CupertinoGroupedCard
import tachiyomi.presentation.core.components.material.FilterChip
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState
import java.text.NumberFormat

@Composable
internal fun ReadingModePage(screenModel: ReaderSettingsScreenModel) {
    HeadingItem(MR.strings.pref_category_for_this_series)
    val manga by screenModel.mangaFlow.collectAsState()
    val isGlass = LocalLiquidGlassConfig.current.enabled

    val readingMode = remember(manga) { ReadingMode.fromPreference(manga?.readingMode?.toInt()) }
    if (isGlass) {
        val entries = ReadingMode.entries
        val labels = entries.map { stringResource(it.stringRes) }.toTypedArray()
        SelectItem(
            label = stringResource(MR.strings.pref_category_reading_mode),
            options = labels,
            selectedIndex = entries.indexOf(readingMode).coerceAtLeast(0),
            onSelect = { index -> entries.getOrNull(index)?.let { screenModel.onChangeReadingMode(it) } },
        )
    } else {
        SettingsChipRow(MR.strings.pref_category_reading_mode) {
            ReadingMode.entries.map {
                FilterChip(
                    selected = it == readingMode,
                    onClick = { screenModel.onChangeReadingMode(it) },
                    label = { Text(stringResource(it.stringRes)) },
                )
            }
        }
    }

    val orientation = remember(manga) { ReaderOrientation.fromPreference(manga?.readerOrientation?.toInt()) }
    if (isGlass) {
        val entries = ReaderOrientation.entries
        val labels = entries.map { stringResource(it.stringRes) }.toTypedArray()
        SelectItem(
            label = stringResource(MR.strings.rotation_type),
            options = labels,
            selectedIndex = entries.indexOf(orientation).coerceAtLeast(0),
            onSelect = { index -> entries.getOrNull(index)?.let { screenModel.onChangeOrientation(it) } },
        )
    } else {
        SettingsChipRow(MR.strings.rotation_type) {
            ReaderOrientation.entries.map {
                FilterChip(
                    selected = it == orientation,
                    onClick = { screenModel.onChangeOrientation(it) },
                    label = { Text(stringResource(it.stringRes)) },
                )
            }
        }
    }

    val viewer by screenModel.viewerFlow.collectAsState()
    if (viewer is WebtoonViewer) {
        WebtoonViewerSettings(
            screenModel,
            // KMK -->
            readingMode,
            // KMK <--
        )
        // SY -->
        WebtoonWithGapsViewerSettings(screenModel)
        // SY <--
    } else {
        PagerViewerSettings(screenModel)
    }
}

@Composable
private fun PagerViewerSettings(screenModel: ReaderSettingsScreenModel) {
    HeadingItem(MR.strings.pager_viewer)
    val isGlass = LocalLiquidGlassConfig.current.enabled

    val navigationModePager by screenModel.preferences.navigationModePager().collectAsState()
    val pagerNavInverted by screenModel.preferences.pagerNavInverted().collectAsState()
    TapZonesItems(
        selected = navigationModePager,
        onSelect = screenModel.preferences.navigationModePager()::set,
        invertMode = pagerNavInverted,
        onSelectInvertMode = screenModel.preferences.pagerNavInverted()::set,
    )

    val imageScaleType by screenModel.preferences.imageScaleType().collectAsState()
    if (isGlass) {
        val labels = ReaderPreferences.ImageScaleType.map { stringResource(it) }.toTypedArray()
        SelectItem(
            label = stringResource(MR.strings.pref_image_scale_type),
            options = labels,
            selectedIndex = (imageScaleType - 1).coerceIn(0, (labels.size - 1).coerceAtLeast(0)),
            onSelect = { index -> screenModel.preferences.imageScaleType().set(index + 1) },
        )
    } else {
        SettingsChipRow(MR.strings.pref_image_scale_type) {
            ReaderPreferences.ImageScaleType.mapIndexed { index, it ->
                FilterChip(
                    selected = imageScaleType == index + 1,
                    onClick = { screenModel.preferences.imageScaleType().set(index + 1) },
                    label = { Text(stringResource(it)) },
                )
            }
        }
    }

    val zoomStart by screenModel.preferences.zoomStart().collectAsState()
    if (isGlass) {
        val labels = ReaderPreferences.ZoomStart.map { stringResource(it) }.toTypedArray()
        SelectItem(
            label = stringResource(MR.strings.pref_zoom_start),
            options = labels,
            selectedIndex = (zoomStart - 1).coerceIn(0, (labels.size - 1).coerceAtLeast(0)),
            onSelect = { index -> screenModel.preferences.zoomStart().set(index + 1) },
        )
    } else {
        SettingsChipRow(MR.strings.pref_zoom_start) {
            ReaderPreferences.ZoomStart.mapIndexed { index, it ->
                FilterChip(
                    selected = zoomStart == index + 1,
                    onClick = { screenModel.preferences.zoomStart().set(index + 1) },
                    label = { Text(stringResource(it)) },
                )
            }
        }
    }

    // SY -->
    val pageLayout by screenModel.preferences.pageLayout().collectAsState()
    if (isGlass) {
        val labels = ReaderPreferences.PageLayouts.map { stringResource(it) }.toTypedArray()
        SelectItem(
            label = stringResource(SYMR.strings.page_layout),
            options = labels,
            selectedIndex = pageLayout.coerceIn(0, (labels.size - 1).coerceAtLeast(0)),
            onSelect = { index -> screenModel.preferences.pageLayout().set(index) },
        )
    } else {
        SettingsChipRow(SYMR.strings.page_layout) {
            ReaderPreferences.PageLayouts.mapIndexed { index, it ->
                FilterChip(
                    selected = pageLayout == index,
                    onClick = { screenModel.preferences.pageLayout().set(index) },
                    label = { Text(stringResource(it)) },
                )
            }
        }
    }
    // SY <--

    val togglesContent: @Composable () -> Unit = {
        // KMK -->
        CheckboxItem(
            label = stringResource(KMR.strings.pref_viewer_nav_smaller_tap_zone),
            pref = screenModel.preferences.smallerTapZone(),
        )
        // KMK <--

        CheckboxItem(
            label = stringResource(MR.strings.pref_crop_borders),
            pref = screenModel.preferences.cropBorders(),
        )

        // KMK -->
        if (imageScaleType in ReaderPreferences.zoomWideImagesAllowedList) {
            // KMK <--
            CheckboxItem(
                label = stringResource(MR.strings.pref_landscape_zoom),
                pref = screenModel.preferences.landscapeZoom(),
            )
        }

        CheckboxItem(
            label = stringResource(MR.strings.pref_navigate_pan),
            pref = screenModel.preferences.navigateToPan(),
        )

        val dualPageSplitPaged by screenModel.preferences.dualPageSplitPaged().collectAsState()
        CheckboxItem(
            label = stringResource(MR.strings.pref_dual_page_split),
            pref = screenModel.preferences.dualPageSplitPaged(),
        )

        if (dualPageSplitPaged) {
            CheckboxItem(
                label = stringResource(MR.strings.pref_dual_page_invert),
                pref = screenModel.preferences.dualPageInvertPaged(),
            )
        }

        val dualPageRotateToFit by screenModel.preferences.dualPageRotateToFit().collectAsState()
        CheckboxItem(
            label = stringResource(MR.strings.pref_page_rotate),
            pref = screenModel.preferences.dualPageRotateToFit(),
        )

        if (dualPageRotateToFit) {
            CheckboxItem(
                label = stringResource(MR.strings.pref_page_rotate_invert),
                pref = screenModel.preferences.dualPageRotateToFitInvert(),
            )
        }

        // SY -->
        CheckboxItem(
            label = stringResource(MR.strings.pref_page_transitions),
            pref = screenModel.preferences.pageTransitionsPager(),
        )

        CheckboxItem(
            label = stringResource(SYMR.strings.invert_double_pages),
            pref = screenModel.preferences.invertDoublePages(),
        )

        // KMK -->
        CheckboxItem(
            label = stringResource(KMR.strings.pref_paged_disable_zoom_in),
            pref = screenModel.preferences.pagedDisableZoomIn(),
        )
        val pagedDisableZoomIn by screenModel.preferences.pagedDisableZoomIn().collectAsState()
        if (!pagedDisableZoomIn) {
            CheckboxItem(
                label = stringResource(MR.strings.pref_double_tap_zoom),
                pref = screenModel.preferences.pagedDoubleTapZoomEnabled(),
            )
        }
        // KMK <--
    }

    if (isGlass) {
        CupertinoGroupedCard {
            togglesContent()
        }
    } else {
        togglesContent()
    }

    val centerMarginType by screenModel.preferences.centerMarginType().collectAsState()
    if (isGlass) {
        val labels = ReaderPreferences.CenterMarginTypes.map { stringResource(it) }.toTypedArray()
        SelectItem(
            label = stringResource(SYMR.strings.pref_center_margin),
            options = labels,
            selectedIndex = centerMarginType.coerceIn(0, (labels.size - 1).coerceAtLeast(0)),
            onSelect = { index -> screenModel.preferences.centerMarginType().set(index) },
        )
    } else {
        SettingsChipRow(SYMR.strings.pref_center_margin) {
            ReaderPreferences.CenterMarginTypes.mapIndexed { index, it ->
                FilterChip(
                    selected = centerMarginType == index,
                    onClick = { screenModel.preferences.centerMarginType().set(index) },
                    label = { Text(stringResource(it)) },
                )
            }
        }
    }
    // SY <--
}

@Composable
private fun WebtoonViewerSettings(
    screenModel: ReaderSettingsScreenModel,
    // KMK -->
    readingMode: ReadingMode,
    // KMK <--
) {
    val numberFormat = remember { NumberFormat.getPercentInstance() }
    val isGlass = LocalLiquidGlassConfig.current.enabled

    HeadingItem(MR.strings.webtoon_viewer)

    val navigationModeWebtoon by screenModel.preferences.navigationModeWebtoon().collectAsState()
    val webtoonNavInverted by screenModel.preferences.webtoonNavInverted().collectAsState()
    TapZonesItems(
        selected = navigationModeWebtoon,
        onSelect = screenModel.preferences.navigationModeWebtoon()::set,
        invertMode = webtoonNavInverted,
        onSelectInvertMode = screenModel.preferences.webtoonNavInverted()::set,
    )

    // KMK -->
    val webtoonScaleTypePref = screenModel.preferences.webtoonScaleType()
    val webtoonScaleType by webtoonScaleTypePref.collectAsState()
    val webtoonSmartScaleLongStripGap = screenModel.preferences.longStripGapSmartScale().get()
    if (readingMode != ReadingMode.CONTINUOUS_VERTICAL || webtoonSmartScaleLongStripGap) {
        if (isGlass) {
            val entries = ReaderPreferences.WebtoonScaleType.entries
            val labels = entries.map { stringResource(it.titleRes) }.toTypedArray()
            SelectItem(
                label = stringResource(KMR.strings.pref_webtoon_scale_type),
                options = labels,
                selectedIndex = entries.indexOf(webtoonScaleType).coerceAtLeast(0),
                onSelect = { index -> entries.getOrNull(index)?.let(webtoonScaleTypePref::set) },
            )
        } else {
            SettingsChipRow(KMR.strings.pref_webtoon_scale_type) {
                ReaderPreferences.WebtoonScaleType.entries.forEach { scaleType ->
                    FilterChip(
                        selected = webtoonScaleType == scaleType,
                        onClick = { webtoonScaleTypePref.set(scaleType) },
                        label = { Text(stringResource(scaleType.titleRes)) },
                    )
                }
            }
        }
    }
    // KMK <--

    val webtoonSidePadding by screenModel.preferences.webtoonSidePadding().collectAsState()
    val webtoonTogglesContent: @Composable () -> Unit = {
        SliderItem(
            value = webtoonSidePadding,
            valueRange = ReaderPreferences.let { it.WEBTOON_PADDING_MIN..it.WEBTOON_PADDING_MAX },
            label = stringResource(MR.strings.pref_webtoon_side_padding),
            valueString = numberFormat.format(webtoonSidePadding / 100f),
            onChange = {
                screenModel.preferences.webtoonSidePadding().set(it)
            },
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )

        // KMK -->
        CheckboxItem(
            label = stringResource(KMR.strings.pref_viewer_nav_smaller_tap_zone),
            pref = screenModel.preferences.smallerTapZone(),
        )
        // KMK <--

        CheckboxItem(
            label = stringResource(MR.strings.pref_crop_borders),
            pref = screenModel.preferences.cropBordersWebtoon(),
        )

        // SY -->
        CheckboxItem(
            label = stringResource(SYMR.strings.pref_smooth_scroll),
            pref = screenModel.preferences.smoothAutoScroll(),
        )

        CheckboxItem(
            label = stringResource(MR.strings.pref_page_transitions),
            pref = screenModel.preferences.pageTransitionsWebtoon(),
        )
        // SY <--

        val dualPageSplitWebtoon by screenModel.preferences.dualPageSplitWebtoon().collectAsState()
        CheckboxItem(
            label = stringResource(MR.strings.pref_dual_page_split),
            pref = screenModel.preferences.dualPageSplitWebtoon(),
        )

        if (dualPageSplitWebtoon) {
            CheckboxItem(
                label = stringResource(MR.strings.pref_dual_page_invert),
                pref = screenModel.preferences.dualPageInvertWebtoon(),
            )
        }

        val dualPageRotateToFitWebtoon by screenModel.preferences.dualPageRotateToFitWebtoon().collectAsState()
        CheckboxItem(
            label = stringResource(MR.strings.pref_page_rotate),
            pref = screenModel.preferences.dualPageRotateToFitWebtoon(),
        )

        if (dualPageRotateToFitWebtoon) {
            CheckboxItem(
                label = stringResource(MR.strings.pref_page_rotate_invert),
                pref = screenModel.preferences.dualPageRotateToFitInvertWebtoon(),
            )
        }

        CheckboxItem(
            label = stringResource(MR.strings.pref_double_tap_zoom),
            pref = screenModel.preferences.webtoonDoubleTapZoomEnabled(),
        )
        // KMK -->
        CheckboxItem(
            label = stringResource(KMR.strings.pref_pinch_to_zoom),
            pref = screenModel.preferences.webtoonPinchToZoomEnabled(),
        )
        // KMK <--
        CheckboxItem(
            label = stringResource(MR.strings.pref_webtoon_disable_zoom_out),
            pref = screenModel.preferences.webtoonDisableZoomOut(),
        )
    }

    if (isGlass) {
        CupertinoGroupedCard {
            webtoonTogglesContent()
        }
    } else {
        webtoonTogglesContent()
    }
}

// SY -->
@Composable
private fun WebtoonWithGapsViewerSettings(screenModel: ReaderSettingsScreenModel) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    HeadingItem(MR.strings.vertical_plus_viewer)

    if (isGlass) {
        CupertinoGroupedCard {
            CheckboxItem(
                label = stringResource(MR.strings.pref_crop_borders),
                pref = screenModel.preferences.cropBordersContinuousVertical(),
            )
        }
    } else {
        CheckboxItem(
            label = stringResource(MR.strings.pref_crop_borders),
            pref = screenModel.preferences.cropBordersContinuousVertical(),
        )
    }
}
// SY <--

@Composable
private fun TapZonesItems(
    selected: Int,
    onSelect: (Int) -> Unit,
    invertMode: ReaderPreferences.TappingInvertMode,
    onSelectInvertMode: (ReaderPreferences.TappingInvertMode) -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        val navLabels = ReaderPreferences.TapZones.map { stringResource(it) }.toTypedArray()
        SelectItem(
            label = stringResource(MR.strings.pref_viewer_nav),
            options = navLabels,
            selectedIndex = selected.coerceIn(0, (navLabels.size - 1).coerceAtLeast(0)),
            onSelect = onSelect,
        )
        if (selected != 5) {
            val invertEntries = ReaderPreferences.TappingInvertMode.entries
            val invertLabels = invertEntries.map { stringResource(it.titleRes) }.toTypedArray()
            SelectItem(
                label = stringResource(MR.strings.pref_read_with_tapping_inverted),
                options = invertLabels,
                selectedIndex = invertEntries.indexOf(invertMode).coerceAtLeast(0),
                onSelect = { index -> invertEntries.getOrNull(index)?.let(onSelectInvertMode) },
            )
        }
    } else {
        SettingsChipRow(MR.strings.pref_viewer_nav) {
            ReaderPreferences.TapZones.mapIndexed { index, it ->
                FilterChip(
                    selected = selected == index,
                    onClick = { onSelect(index) },
                    label = { Text(stringResource(it)) },
                )
            }
        }

        if (selected != 5) {
            SettingsChipRow(MR.strings.pref_read_with_tapping_inverted) {
                ReaderPreferences.TappingInvertMode.entries.map {
                    FilterChip(
                        selected = it == invertMode,
                        onClick = { onSelectInvertMode(it) },
                        label = { Text(stringResource(it.titleRes)) },
                    )
                }
            }
        }
    }
}
