package eu.kanade.presentation.reader.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences.Companion.ColorFilterMode
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import tachiyomi.core.common.preference.getAndSet
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.CheckboxItem
import tachiyomi.presentation.core.components.SettingsChipRow
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.components.material.CupertinoGroupedCard
import tachiyomi.presentation.core.components.material.CupertinoGroupedDivider
import tachiyomi.presentation.core.components.material.CupertinoGroupedSelectionCard
import tachiyomi.presentation.core.components.material.CupertinoGroupedToggleRow
import tachiyomi.presentation.core.components.material.FilterChip
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState

@Composable
internal fun ColorFilterPage(screenModel: ReaderSettingsScreenModel) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val customBrightness by screenModel.preferences.customBrightness().collectAsState()
    val colorFilter by screenModel.preferences.colorFilter().collectAsState()
    val grayscale by screenModel.preferences.grayscale().collectAsState()
    val invertedColors by screenModel.preferences.invertedColors().collectAsState()

    if (isGlass) {
        CupertinoGroupedCard {
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_custom_brightness),
                checked = customBrightness,
                onCheckedChange = { screenModel.preferences.customBrightness().set(it) },
            )
            if (customBrightness) {
                val customBrightnessValue by screenModel.preferences.customBrightnessValue().collectAsState()
                CupertinoGroupedDivider()
                SliderItem(
                    value = customBrightnessValue,
                    valueRange = -75..100,
                    steps = 0,
                    label = stringResource(MR.strings.pref_custom_brightness),
                    onChange = { screenModel.preferences.customBrightnessValue().set(it) },
                    pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }
        }

        CupertinoGroupedCard {
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_custom_color_filter),
                checked = colorFilter,
                onCheckedChange = { screenModel.preferences.colorFilter().set(it) },
            )
            if (colorFilter) {
                val colorFilterValue by screenModel.preferences.colorFilterValue().collectAsState()
                CupertinoGroupedDivider()
                SliderItem(
                    value = colorFilterValue.red,
                    valueRange = 0..255,
                    steps = 0,
                    label = stringResource(MR.strings.color_filter_r_value),
                    onChange = { newRValue ->
                        screenModel.preferences.colorFilterValue().getAndSet {
                            getColorValue(it, newRValue, RED_MASK, 16)
                        }
                    },
                    pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    accentColor = Color(0xFFFF453A),
                )
                CupertinoGroupedDivider()
                SliderItem(
                    value = colorFilterValue.green,
                    valueRange = 0..255,
                    steps = 0,
                    label = stringResource(MR.strings.color_filter_g_value),
                    onChange = { newGValue ->
                        screenModel.preferences.colorFilterValue().getAndSet {
                            getColorValue(it, newGValue, GREEN_MASK, 8)
                        }
                    },
                    pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    accentColor = Color(0xFF30D158),
                )
                CupertinoGroupedDivider()
                SliderItem(
                    value = colorFilterValue.blue,
                    valueRange = 0..255,
                    steps = 0,
                    label = stringResource(MR.strings.color_filter_b_value),
                    onChange = { newBValue ->
                        screenModel.preferences.colorFilterValue().getAndSet {
                            getColorValue(it, newBValue, BLUE_MASK, 0)
                        }
                    },
                    pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    accentColor = Color(0xFF0A84FF),
                )
                CupertinoGroupedDivider()
                SliderItem(
                    value = colorFilterValue.alpha,
                    valueRange = 0..255,
                    steps = 0,
                    label = stringResource(MR.strings.color_filter_a_value),
                    onChange = { newAValue ->
                        screenModel.preferences.colorFilterValue().getAndSet {
                            getColorValue(it, newAValue, ALPHA_MASK, 24)
                        }
                    },
                    pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    accentColor = Color(0xFF8E8E93),
                )
            }
        }

        if (colorFilter) {
            val colorFilterMode by screenModel.preferences.colorFilterMode().collectAsState()
            CupertinoGroupedSelectionCard(
                title = stringResource(MR.strings.pref_color_filter_mode),
                options = ColorFilterMode.indices.toList(),
                selected = colorFilterMode,
                onSelect = { screenModel.preferences.colorFilterMode().set(it) },
                labelProvider = { stringResource(ColorFilterMode.getOrNull(it)?.first ?: MR.strings.label_default) },
            )
        }

        CupertinoGroupedCard {
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_grayscale),
                checked = grayscale,
                onCheckedChange = { screenModel.preferences.grayscale().set(it) },
            )
            CupertinoGroupedDivider()
            CupertinoGroupedToggleRow(
                label = stringResource(MR.strings.pref_inverted_colors),
                checked = invertedColors,
                onCheckedChange = { screenModel.preferences.invertedColors().set(it) },
            )
        }
    } else {
        CheckboxItem(
            label = stringResource(MR.strings.pref_custom_brightness),
            pref = screenModel.preferences.customBrightness(),
        )

        if (customBrightness) {
            val customBrightnessValue by screenModel.preferences.customBrightnessValue().collectAsState()
            SliderItem(
                value = customBrightnessValue,
                valueRange = -75..100,
                steps = 0,
                label = stringResource(MR.strings.pref_custom_brightness),
                onChange = { screenModel.preferences.customBrightnessValue().set(it) },
                pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
        }

        CheckboxItem(
            label = stringResource(MR.strings.pref_custom_color_filter),
            pref = screenModel.preferences.colorFilter(),
        )
        if (colorFilter) {
            val colorFilterValue by screenModel.preferences.colorFilterValue().collectAsState()
            SliderItem(
                value = colorFilterValue.red,
                valueRange = 0..255,
                steps = 0,
                label = stringResource(MR.strings.color_filter_r_value),
                onChange = { newRValue ->
                    screenModel.preferences.colorFilterValue().getAndSet {
                        getColorValue(it, newRValue, RED_MASK, 16)
                    }
                },
                pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            SliderItem(
                value = colorFilterValue.green,
                valueRange = 0..255,
                steps = 0,
                label = stringResource(MR.strings.color_filter_g_value),
                onChange = { newGValue ->
                    screenModel.preferences.colorFilterValue().getAndSet {
                        getColorValue(it, newGValue, GREEN_MASK, 8)
                    }
                },
                pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            SliderItem(
                value = colorFilterValue.blue,
                valueRange = 0..255,
                steps = 0,
                label = stringResource(MR.strings.color_filter_b_value),
                onChange = { newBValue ->
                    screenModel.preferences.colorFilterValue().getAndSet {
                        getColorValue(it, newBValue, BLUE_MASK, 0)
                    }
                },
                pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            SliderItem(
                value = colorFilterValue.alpha,
                valueRange = 0..255,
                steps = 0,
                label = stringResource(MR.strings.color_filter_a_value),
                onChange = { newAValue ->
                    screenModel.preferences.colorFilterValue().getAndSet {
                        getColorValue(it, newAValue, ALPHA_MASK, 24)
                    }
                },
                pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )

            val colorFilterMode by screenModel.preferences.colorFilterMode().collectAsState()
            SettingsChipRow(MR.strings.pref_color_filter_mode) {
                ColorFilterMode.mapIndexed { index, it ->
                    FilterChip(
                        selected = colorFilterMode == index,
                        onClick = { screenModel.preferences.colorFilterMode().set(index) },
                        label = { Text(stringResource(it.first)) },
                    )
                }
            }
        }

        CheckboxItem(
            label = stringResource(MR.strings.pref_grayscale),
            pref = screenModel.preferences.grayscale(),
        )
        CheckboxItem(
            label = stringResource(MR.strings.pref_inverted_colors),
            pref = screenModel.preferences.invertedColors(),
        )
    }
}

private fun getColorValue(currentColor: Int, color: Int, mask: Long, bitShift: Int): Int {
    return (color shl bitShift) or (currentColor and mask.inv().toInt())
}
private const val ALPHA_MASK: Long = 0xFF000000
private const val RED_MASK: Long = 0x00FF0000
private const val GREEN_MASK: Long = 0x0000FF00
private const val BLUE_MASK: Long = 0x000000FF
