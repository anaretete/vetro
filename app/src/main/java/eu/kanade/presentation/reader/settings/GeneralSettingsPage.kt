package eu.kanade.presentation.reader.settings

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import eu.kanade.tachiyomi.util.system.hasDisplayCutout
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.CheckboxItem
import tachiyomi.presentation.core.components.SettingsChipRow
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.components.material.CupertinoGroupedCard
import tachiyomi.presentation.core.components.material.CupertinoGroupedDivider
import tachiyomi.presentation.core.components.material.CupertinoGroupedSegmentedControl
import tachiyomi.presentation.core.components.material.CupertinoGroupedToggleRow
import tachiyomi.presentation.core.components.material.FilterChip
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState

private val themes = listOf(
    MR.strings.black_background to 1,
    MR.strings.gray_background to 2,
    MR.strings.white_background to 0,
    MR.strings.automatic_background to 3,
)

private val flashColors = listOf(
    MR.strings.pref_flash_style_black to ReaderPreferences.FlashColor.BLACK,
    MR.strings.pref_flash_style_white to ReaderPreferences.FlashColor.WHITE,
    MR.strings.pref_flash_style_white_black to ReaderPreferences.FlashColor.WHITE_BLACK,
)

@Composable
internal fun GeneralPage(screenModel: ReaderSettingsScreenModel) {
    val readerTheme by screenModel.preferences.readerTheme().collectAsState()

    val flashPageState by screenModel.preferences.flashOnPageChange().collectAsState()

    val flashMillisPref = screenModel.preferences.flashDurationMillis()
    val flashMillis by flashMillisPref.collectAsState()

    val flashIntervalPref = screenModel.preferences.flashPageInterval()
    val flashInterval by flashIntervalPref.collectAsState()

    val flashColorPref = screenModel.preferences.flashColor()
    val flashColor by flashColorPref.collectAsState()

    val isGlass = LocalLiquidGlassConfig.current.enabled

    if (isGlass) {
        val themeTitles = themes.map { stringResource(it.first) }
        val currentThemeIndex = themes.indexOfFirst { it.second == readerTheme }.coerceAtLeast(0)
        CupertinoGroupedSegmentedControl(
            title = stringResource(MR.strings.pref_reader_theme),
            options = themeTitles,
            selectedIndex = currentThemeIndex,
            onSelect = { index ->
                screenModel.preferences.readerTheme().set(themes[index].second)
            },
        )

        val showPageNumber by screenModel.preferences.showPageNumber().collectAsState()
        val isFullscreen by screenModel.preferences.fullscreen().collectAsState()
        val drawUnderCutout by screenModel.preferences.drawUnderCutout().collectAsState()
        val keepScreenOn by screenModel.preferences.keepScreenOn().collectAsState()
        val hasCutout = LocalActivity.current?.hasDisplayCutout() == true

        CupertinoGroupedCard(title = stringResource(MR.strings.pref_category_display)) {
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_show_page_number),
                checked = showPageNumber,
                onCheckedChange = { screenModel.preferences.showPageNumber().set(it) },
            )
            CupertinoGroupedDivider()
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_fullscreen),
                checked = isFullscreen,
                onCheckedChange = { screenModel.preferences.fullscreen().set(it) },
            )
            if (hasCutout && isFullscreen) {
                CupertinoGroupedDivider()
                CupertinoGroupedToggleRow(
                    label = stringResource(MR.strings.pref_cutout_short),
                    checked = drawUnderCutout,
                    onCheckedChange = { screenModel.preferences.drawUnderCutout().set(it) },
                )
            }
            CupertinoGroupedDivider()
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_keep_screen_on),
                checked = keepScreenOn,
                onCheckedChange = { screenModel.preferences.keepScreenOn().set(it) },
            )
        }

        val readWithLongTap by screenModel.preferences.readWithLongTap().collectAsState()
        val alwaysShowChapterTransition by screenModel.preferences.alwaysShowChapterTransition().collectAsState()
        val useAutoWebtoon by screenModel.preferences.useAutoWebtoon().collectAsState()
        val forceHorizontalSeekbar by screenModel.preferences.forceHorizontalSeekbar().collectAsState()
        val landscapeVerticalSeekbar by screenModel.preferences.landscapeVerticalSeekbar().collectAsState()
        val leftVerticalSeekbar by screenModel.preferences.leftVerticalSeekbar().collectAsState()

        CupertinoGroupedCard(title = stringResource(MR.strings.pref_category_reading)) {
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_read_with_long_tap),
                checked = readWithLongTap,
                onCheckedChange = { screenModel.preferences.readWithLongTap().set(it) },
            )
            CupertinoGroupedDivider()
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_always_show_chapter_transition),
                checked = alwaysShowChapterTransition,
                onCheckedChange = { screenModel.preferences.alwaysShowChapterTransition().set(it) },
            )
            CupertinoGroupedDivider()
            CupertinoGroupedToggleRow(
                label = stringResource(SYMR.strings.auto_webtoon_mode),
                checked = useAutoWebtoon,
                onCheckedChange = { screenModel.preferences.useAutoWebtoon().set(it) },
            )
            CupertinoGroupedDivider()
            CupertinoGroupedToggleRow(
                label = stringResource(SYMR.strings.pref_force_horz_seekbar),
                checked = forceHorizontalSeekbar,
                onCheckedChange = { screenModel.preferences.forceHorizontalSeekbar().set(it) },
            )
            if (!forceHorizontalSeekbar) {
                CupertinoGroupedDivider()
                CupertinoGroupedToggleRow(
                    label = stringResource(SYMR.strings.pref_show_vert_seekbar_landscape),
                    checked = landscapeVerticalSeekbar,
                    onCheckedChange = { screenModel.preferences.landscapeVerticalSeekbar().set(it) },
                )
                CupertinoGroupedDivider()
                CupertinoGroupedToggleRow(
                    label = stringResource(SYMR.strings.pref_left_handed_vertical_seekbar),
                    checked = leftVerticalSeekbar,
                    onCheckedChange = { screenModel.preferences.leftVerticalSeekbar().set(it) },
                )
            }
        }

        CupertinoGroupedCard(title = stringResource(MR.strings.pref_flash_page)) {
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_flash_page),
                checked = flashPageState,
                onCheckedChange = { screenModel.preferences.flashOnPageChange().set(it) },
            )
            if (flashPageState) {
                CupertinoGroupedDivider()
                SliderItem(
                    value = flashMillis / ReaderPreferences.MILLI_CONVERSION,
                    valueRange = 1..15,
                    label = stringResource(MR.strings.pref_flash_duration),
                    valueString = stringResource(MR.strings.pref_flash_duration_summary, flashMillis),
                    onChange = { flashMillisPref.set(it * ReaderPreferences.MILLI_CONVERSION) },
                    pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                CupertinoGroupedDivider()
                SliderItem(
                    value = flashInterval,
                    valueRange = 1..10,
                    label = stringResource(MR.strings.pref_flash_page_interval),
                    valueString = pluralStringResource(MR.plurals.pref_pages, flashInterval, flashInterval),
                    onChange = { flashIntervalPref.set(it) },
                    pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                CupertinoGroupedDivider()
                val flashColorTitles = flashColors.map { stringResource(it.first) }
                val currentFlashIndex = flashColors.indexOfFirst { it.second == flashColor }.coerceAtLeast(0)
                CupertinoGroupedSegmentedControl(
                    title = stringResource(MR.strings.pref_flash_with),
                    options = flashColorTitles,
                    selectedIndex = currentFlashIndex,
                    onSelect = { index ->
                        flashColorPref.set(flashColors[index].second)
                    },
                )
            }
        }
    } else {
        SettingsChipRow(MR.strings.pref_reader_theme) {
            themes.map { (labelRes, value) ->
                FilterChip(
                    selected = readerTheme == value,
                    onClick = { screenModel.preferences.readerTheme().set(value) },
                    label = { Text(stringResource(labelRes)) },
                )
            }
        }

        CheckboxItem(
            label = stringResource(MR.strings.pref_show_page_number),
            pref = screenModel.preferences.showPageNumber(),
        )

        // SY -->
        val forceHorizontalSeekbar by screenModel.preferences.forceHorizontalSeekbar().collectAsState()
        CheckboxItem(
            label = stringResource(SYMR.strings.pref_force_horz_seekbar),
            pref = screenModel.preferences.forceHorizontalSeekbar(),
        )

        if (!forceHorizontalSeekbar) {
            CheckboxItem(
                label = stringResource(SYMR.strings.pref_show_vert_seekbar_landscape),
                pref = screenModel.preferences.landscapeVerticalSeekbar(),
            )

            CheckboxItem(
                label = stringResource(SYMR.strings.pref_left_handed_vertical_seekbar),
                pref = screenModel.preferences.leftVerticalSeekbar(),
            )
        }
        // SY <--

        CheckboxItem(
            label = stringResource(MR.strings.pref_fullscreen),
            pref = screenModel.preferences.fullscreen(),
        )

        val isFullscreen by screenModel.preferences.fullscreen().collectAsState()
        if (LocalActivity.current?.hasDisplayCutout() == true && isFullscreen) {
            CheckboxItem(
                label = stringResource(MR.strings.pref_cutout_short),
                pref = screenModel.preferences.drawUnderCutout(),
            )
        }

        CheckboxItem(
            label = stringResource(MR.strings.pref_keep_screen_on),
            pref = screenModel.preferences.keepScreenOn(),
        )

        CheckboxItem(
            label = stringResource(MR.strings.pref_read_with_long_tap),
            pref = screenModel.preferences.readWithLongTap(),
        )

        CheckboxItem(
            label = stringResource(MR.strings.pref_always_show_chapter_transition),
            pref = screenModel.preferences.alwaysShowChapterTransition(),
        )

        CheckboxItem(
            label = stringResource(MR.strings.pref_flash_page),
            pref = screenModel.preferences.flashOnPageChange(),
        )

        if (flashPageState) {
            SliderItem(
                value = flashMillis / ReaderPreferences.MILLI_CONVERSION,
                valueRange = 1..15,
                label = stringResource(MR.strings.pref_flash_duration),
                valueString = stringResource(MR.strings.pref_flash_duration_summary, flashMillis),
                onChange = { flashMillisPref.set(it * ReaderPreferences.MILLI_CONVERSION) },
                pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            SliderItem(
                value = flashInterval,
                valueRange = 1..10,
                label = stringResource(MR.strings.pref_flash_page_interval),
                valueString = pluralStringResource(MR.plurals.pref_pages, flashInterval, flashInterval),
                onChange = {
                    flashIntervalPref.set(it)
                },
                pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            SettingsChipRow(MR.strings.pref_flash_with) {
                flashColors.map { (labelRes, value) ->
                    FilterChip(
                        selected = flashColor == value,
                        onClick = { flashColorPref.set(value) },
                        label = { Text(stringResource(labelRes)) },
                    )
                }
            }
        }

        // SY -->
        CheckboxItem(
            label = stringResource(SYMR.strings.auto_webtoon_mode),
            pref = screenModel.preferences.useAutoWebtoon(),
        )
        // SY <--
    }
}
