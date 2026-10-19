package eu.kanade.presentation.reader.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import tachiyomi.presentation.core.components.material.FilterChip
import tachiyomi.presentation.core.components.material.CupertinoGroupedSelectionCard
import tachiyomi.presentation.core.components.material.CupertinoGroupedSegmentedControl
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
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
import tachiyomi.presentation.core.components.SettingsChipRow
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState
import java.text.NumberFormat

@Composable
internal fun ReadingModePage(screenModel: ReaderSettingsScreenModel) {
    HeadingItem(MR.strings.pref_category_for_this_series)
    val manga by screenModel.mangaFlow.collectAsState()

    val readingMode = remember(manga) { ReadingMode.fromPreference(manga?.readingMode?.toInt()) }
    val orientation = remember(manga) { ReaderOrientation.fromPreference(manga?.readerOrientation?.toInt()) }
    val isGlass = LocalLiquidGlassConfig.current.enabled

    if (isGlass) {
        val isDark = isSystemInDarkTheme()
        CupertinoGroupedSelectionCard(
            title = stringResource(MR.strings.pref_category_reading_mode),
            options = ReadingMode.entries,
            selected = readingMode,
            onSelect = { screenModel.onChangeReadingMode(it) },
            labelProvider = { stringResource(it.stringRes) },
            iconProvider = { mode ->
                Icon(
                    imageVector = ImageVector.vectorResource(mode.iconRes),
                    contentDescription = null,
                    tint = if (mode == readingMode) Color(0xFF0A84FF) else (if (isDark) Color.White else Color(0xFF1C1C1E)),
                    modifier = Modifier.size(24.dp),
                )
            },
        )
        CupertinoGroupedSelectionCard(
            title = stringResource(MR.strings.rotation_type),
            options = ReaderOrientation.entries,
            selected = orientation,
            onSelect = { screenModel.onChangeOrientation(it) },
            labelProvider = { stringResource(it.stringRes) },
            iconProvider = { mode ->
                Icon(
                    imageVector = mode.icon,
                    contentDescription = null,
                    tint = if (mode == orientation) Color(0xFFFF9F0A) else (if (isDark) Color.White else Color(0xFF1C1C1E)),
                    modifier = Modifier.size(24.dp),
                )
            },
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

    val navigationModePager by screenModel.preferences.navigationModePager().collectAsState()
    val pagerNavInverted by screenModel.preferences.pagerNavInverted().collectAsState()
    TapZonesItems(
        selected = navigationModePager,
        onSelect = screenModel.preferences.navigationModePager()::set,
        invertMode = pagerNavInverted,
        onSelectInvertMode = screenModel.preferences.pagerNavInverted()::set,
    )

    val isGlass = LocalLiquidGlassConfig.current.enabled

    val imageScaleType by screenModel.preferences.imageScaleType().collectAsState()
    if (isGlass) {
        CupertinoGroupedSelectionCard(
            title = stringResource(MR.strings.pref_image_scale_type),
            options = ReaderPreferences.ImageScaleType.indices.map { it + 1 },
            selected = imageScaleType,
            onSelect = { screenModel.preferences.imageScaleType().set(it) },
            labelProvider = { stringResource(ReaderPreferences.ImageScaleType.getOrElse(it - 1) { MR.strings.scale_type_fit_screen }) },
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
        CupertinoGroupedSegmentedControl(
            title = stringResource(MR.strings.pref_zoom_start),
            options = ReaderPreferences.ZoomStart.map { stringResource(it) },
            selectedIndex = (zoomStart - 1).coerceIn(0, ReaderPreferences.ZoomStart.size - 1),
            onSelect = { screenModel.preferences.zoomStart().set(it + 1) },
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
        CupertinoGroupedSegmentedControl(
            title = stringResource(SYMR.strings.page_layout),
            options = ReaderPreferences.PageLayouts.map { stringResource(it) },
            selectedIndex = pageLayout.coerceIn(0, ReaderPreferences.PageLayouts.size - 1),
            onSelect = { screenModel.preferences.pageLayout().set(it) },
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

    val centerMarginType by screenModel.preferences.centerMarginType().collectAsState()
    if (isGlass) {
        CupertinoGroupedSegmentedControl(
            title = stringResource(SYMR.strings.pref_center_margin),
            options = ReaderPreferences.CenterMarginTypes.map { stringResource(it) },
            selectedIndex = centerMarginType.coerceIn(0, ReaderPreferences.CenterMarginTypes.size - 1),
            onSelect = { screenModel.preferences.centerMarginType().set(it) },
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
            CupertinoGroupedSelectionCard(
                title = stringResource(KMR.strings.pref_webtoon_scale_type),
                options = ReaderPreferences.WebtoonScaleType.entries,
                selected = webtoonScaleType,
                onSelect = { webtoonScaleTypePref.set(it) },
                labelProvider = { stringResource(it.titleRes) },
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

// SY -->
@Composable
private fun WebtoonWithGapsViewerSettings(screenModel: ReaderSettingsScreenModel) {
    HeadingItem(MR.strings.vertical_plus_viewer)

    CheckboxItem(
        label = stringResource(MR.strings.pref_crop_borders),
        pref = screenModel.preferences.cropBordersContinuousVertical(),
    )
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
        CupertinoGroupedSelectionCard(
            title = stringResource(MR.strings.pref_viewer_nav),
            options = ReaderPreferences.TapZones.indices.toList(),
            selected = selected,
            onSelect = onSelect,
            labelProvider = { stringResource(ReaderPreferences.TapZones.getOrElse(it) { MR.strings.label_default }) },
        )
        if (selected != 5) {
            CupertinoGroupedSelectionCard(
                title = stringResource(MR.strings.pref_read_with_tapping_inverted),
                options = ReaderPreferences.TappingInvertMode.entries,
                selected = invertMode,
                onSelect = onSelectInvertMode,
                labelProvider = { stringResource(it.titleRes) },
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
