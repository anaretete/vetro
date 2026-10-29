package eu.kanade.presentation.more.settings.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import java.io.File
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.materialkolor.PaletteStyle
import eu.kanade.core.preference.asState
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.domain.ui.model.AppFont
import eu.kanade.domain.ui.model.AppTheme
import eu.kanade.domain.ui.model.TabletUiMode
import eu.kanade.domain.ui.model.ThemeMode
import eu.kanade.domain.ui.model.setAppCompatDelegateThemeMode
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.presentation.more.settings.screen.appearance.AppCustomThemeColorPickerScreen
import eu.kanade.presentation.more.settings.screen.appearance.AppLanguageScreen
import eu.kanade.presentation.more.settings.widget.AppThemeModePreferenceWidget
import eu.kanade.presentation.more.settings.widget.AppThemePreferenceWidget
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableMap
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.time.LocalDate
import kotlin.math.roundToInt

object SettingsAppearanceScreen : SearchableSettings {
    @Suppress("unused")
    private fun readResolve(): Any = SettingsAppearanceScreen

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = MR.strings.pref_category_appearance

    @Composable
    override fun getPreferences(): List<Preference> {
        val uiPreferences = remember { Injekt.get<UiPreferences>() }

        return listOf(
            getThemeGroup(uiPreferences = uiPreferences),
            // KMK -->
            getMangaInfoThemeGroup(uiPreferences = uiPreferences),
            // KMK <--
            getDisplayGroup(uiPreferences = uiPreferences),
            // SY -->
            getNavbarGroup(uiPreferences = uiPreferences),
            getForkGroup(uiPreferences = uiPreferences),
            // SY <--
        )
    }

    @Composable
    private fun getThemeGroup(
        uiPreferences: UiPreferences,
    ): Preference.PreferenceGroup {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val themeModePref = uiPreferences.themeMode()
        val themeMode by themeModePref.collectAsState()

        val appThemePref = uiPreferences.appTheme()
        val appTheme by appThemePref.collectAsState()

        val amoledPref = uiPreferences.themeDarkAmoled()
        val amoled by amoledPref.collectAsState()

        val isLiquidGlassEnabled by uiPreferences.liquidGlassEnabled().collectAsState()
        val appFont by uiPreferences.appFont().collectAsState()

        val refractionStrengthPref = uiPreferences.liquidGlassRefractionStrength()
        val refractionStrength by refractionStrengthPref.collectAsState()
        val blurRadiusPref = uiPreferences.liquidGlassBlurRadius()
        val blurRadius by blurRadiusPref.collectAsState()
        val lensHeightPref = uiPreferences.liquidGlassLensHeight()
        val lensHeight by lensHeightPref.collectAsState()
        val tintAlphaPref = uiPreferences.liquidGlassTintAlpha()
        val tintAlpha by tintAlphaPref.collectAsState()

        val fontPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri: Uri? ->
            if (uri != null) {
                try {
                    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, flags)
                } catch (_: Throwable) {}

                try {
                    val fontsDir = File(context.filesDir, "fonts").apply { mkdirs() }
                    val destFile = File(fontsDir, "custom_font.ttf")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    uiPreferences.customFontPath().set(destFile.absolutePath)
                    uiPreferences.appFont().set(AppFont.CUSTOM)
                    context.toast("Custom font loaded successfully!")
                } catch (e: Throwable) {
                    uiPreferences.customFontPath().set(uri.toString())
                    uiPreferences.appFont().set(AppFont.CUSTOM)
                    context.toast("Custom font applied")
                }
            }
        }
        val customFontPath by uiPreferences.customFontPath().collectAsState()
        val activeFontName = remember(customFontPath, appFont) {
            if (appFont == AppFont.CUSTOM && customFontPath.isNotBlank()) {
                File(customFontPath).name.takeIf { it.isNotBlank() } ?: "Custom font active"
            } else {
                null
            }
        }

        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.pref_category_theme),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.CustomPreference(
                    title = stringResource(MR.strings.pref_category_theme),
                ) {
                    AppThemeModePreferenceWidget(
                        value = themeMode,
                        onItemClick = {
                            themeModePref.set(it)
                            setAppCompatDelegateThemeMode(it)
                        },
                    )
                },
                Preference.PreferenceItem.CustomPreference(
                    title = stringResource(MR.strings.pref_app_theme),
                ) {
                    AppThemePreferenceWidget(
                        value = appTheme,
                        amoled = amoled,
                        onItemClick = { appThemePref.set(it) },
                    )
                },
                // KMK -->
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(KMR.strings.pref_custom_color),
                    subtitle = stringResource(KMR.strings.custom_color_description),
                    enabled = appTheme == AppTheme.CUSTOM,
                    onClick = { navigator.push(AppCustomThemeColorPickerScreen()) },
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = uiPreferences.customThemeStyle(),
                    entries = PaletteStyle.entries
                        .associateWith {
                            when (it) {
                                PaletteStyle.TonalSpot ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_tonalspot)
                                PaletteStyle.Neutral ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_neutral)
                                PaletteStyle.Vibrant ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_vibrant)
                                PaletteStyle.Expressive ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_expressive)
                                PaletteStyle.Rainbow ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_rainbow)
                                PaletteStyle.FruitSalad ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_fruitsalad)
                                PaletteStyle.Monochrome ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_monochrome)
                                PaletteStyle.Fidelity ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_fidelity)
                                PaletteStyle.Content ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_content)
                            }
                        }
                        .toImmutableMap(),
                    title = stringResource(KMR.strings.pref_custom_theme_style),
                    enabled = appTheme == AppTheme.CUSTOM,
                    onValueChanged = {
                        (context as? Activity)?.let { ActivityCompat.recreate(it) }
                        true
                    },
                ),
                // KMK <--
                Preference.PreferenceItem.SwitchPreference(
                    preference = amoledPref,
                    title = stringResource(MR.strings.pref_dark_theme_pure_black),
                    enabled = themeMode != ThemeMode.LIGHT,
                    onValueChanged = {
                        (context as? Activity)?.let { ActivityCompat.recreate(it) }
                        true
                    },
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.liquidGlassEnabled(),
                    title = "Liquid Glass UI",
                    subtitle = "Refractive glass surfaces, chromatic aberration, and spring physics",
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.liquidGlassChromaticAberration(),
                    title = "Chromatic Aberration",
                    subtitle = "Simulate optical lens RGB dispersion on edges",
                    enabled = isLiquidGlassEnabled,
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.liquidGlassSpringPhysics(),
                    title = "Spring Physics Morphing",
                    subtitle = "Viscoelastic squish and stretch velocity deformation",
                    enabled = isLiquidGlassEnabled,
                ),
                Preference.PreferenceItem.SliderPreference(
                    value = (refractionStrength * 100).toInt(),
                    valueRange = 10..300,
                    title = "Refraction Index Intensity",
                    valueString = "${(refractionStrength * 100).toInt()}%",
                    enabled = isLiquidGlassEnabled,
                    onValueChanged = { refractionStrengthPref.set(it / 100f) },
                ),
                Preference.PreferenceItem.SliderPreference(
                    value = (blurRadius * 100).toInt(),
                    valueRange = 0..300,
                    title = "Frost & Blur Depth",
                    valueString = "${(blurRadius * 100).toInt()}%",
                    enabled = isLiquidGlassEnabled,
                    onValueChanged = { blurRadiusPref.set(it / 100f) },
                ),
                Preference.PreferenceItem.SliderPreference(
                    value = (lensHeight * 100).toInt(),
                    valueRange = 20..300,
                    title = "Lens Convexity Thickness",
                    valueString = "${(lensHeight * 100).toInt()}%",
                    enabled = isLiquidGlassEnabled,
                    onValueChanged = { lensHeightPref.set(it / 100f) },
                ),
                Preference.PreferenceItem.SliderPreference(
                    value = (tintAlpha * 100).toInt(),
                    valueRange = 0..100,
                    title = "Surface Tint Alpha",
                    valueString = "${(tintAlpha * 100).toInt()}%",
                    enabled = isLiquidGlassEnabled,
                    onValueChanged = { tintAlphaPref.set(it / 100f) },
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.cupertinoHaptics(),
                    title = "Tactile Haptic Feedback",
                    subtitle = "Subtle iOS taptic response on switches, sliders, buttons, and page turns",
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.oledPureBlack(),
                    title = "OLED Pure Black Mode",
                    subtitle = "Turn off pixels completely for true OLED black backgrounds",
                    enabled = themeMode != ThemeMode.LIGHT,
                    onValueChanged = {
                        (context as? Activity)?.let { ActivityCompat.recreate(it) }
                        true
                    },
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = uiPreferences.appFont(),
                    entries = AppFont.entries.associateWith { it.label }.toImmutableMap(),
                    title = "App Font & Typography",
                    subtitle = "Select system font preset or load an external font file",
                ),
                Preference.PreferenceItem.TextPreference(
                    title = "Choose Custom Font File",
                    subtitle = if (activeFontName != null) {
                        "Active font: $activeFontName • Tap to pick another font file"
                    } else {
                        "Tap to browse storage and load any .ttf or .otf font"
                    },
                    onClick = {
                        fontPicker.launch(
                            arrayOf(
                                "font/*",
                                "font/ttf",
                                "font/otf",
                                "application/x-font-ttf",
                                "application/x-font-opentype",
                                "application/octet-stream",
                                "*/*",
                            ),
                        )
                    },
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.floatingNavBar(),
                    title = "Floating Tab Island Bar",
                    subtitle = "Elevated floating navigation capsule across bottom tabs",
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.vividAccent(),
                    title = "Vivid Accent Mode",
                    subtitle = "Boost saturation of primary accents for high-gamut and OLED screens",
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.liquidGlassSpecularGleam(),
                    title = "Liquid Glass Specular Shimmer",
                    subtitle = "Refractive dynamic light gleam across active glass elements",
                    enabled = isLiquidGlassEnabled,
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.compactLibraryGrid(),
                    title = "Compact Library Grid",
                    subtitle = "Display tighter manga card spacing with 1.5x more items per row",
                ),
            ),
        )
    }

    // KMK -->
    @Composable
    private fun getMangaInfoThemeGroup(
        uiPreferences: UiPreferences,
    ): Preference.PreferenceGroup {
        val scope = rememberCoroutineScope()
        val mangaInfoThemeCoverBased by remember {
            Injekt.get<UiPreferences>().themeCoverBased().asState(scope)
        }
        return Preference.PreferenceGroup(
            title = stringResource(KMR.strings.pref_manga_info),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.themeCoverBased(),
                    title = stringResource(KMR.strings.pref_theme_cover_based),
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = uiPreferences.themeCoverBasedStyle(),
                    entries = PaletteStyle.entries
                        .associateWith {
                            when (it) {
                                PaletteStyle.TonalSpot ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_tonalspot)
                                PaletteStyle.Neutral ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_neutral)
                                PaletteStyle.Vibrant ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_vibrant)
                                PaletteStyle.Expressive ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_expressive)
                                PaletteStyle.Rainbow ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_rainbow)
                                PaletteStyle.FruitSalad ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_fruitsalad)
                                PaletteStyle.Monochrome ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_monochrome)
                                PaletteStyle.Fidelity ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_fidelity)
                                PaletteStyle.Content ->
                                    stringResource(KMR.strings.pref_theme_cover_based_style_content)
                            }
                        }
                        .toImmutableMap(),
                    title = stringResource(KMR.strings.pref_theme_cover_based_style),
                    enabled = mangaInfoThemeCoverBased,
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.usePanoramaCoverMangaInfo(),
                    title = stringResource(KMR.strings.pref_panorama_cover),
                    subtitle = stringResource(KMR.strings.pref_panorama_cover_summary),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.topAlignCover(),
                    title = stringResource(KMR.strings.pref_top_align_cover),
                    subtitle = stringResource(KMR.strings.pref_top_align_cover_summary),
                ),
            ),
        )
    }
    // KMK <--

    @Composable
    private fun getDisplayGroup(
        uiPreferences: UiPreferences,
    ): Preference.PreferenceGroup {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val now = remember { LocalDate.now() }

        val dateFormat by uiPreferences.dateFormat().collectAsState()
        val formattedNow = remember(dateFormat) {
            UiPreferences.dateFormat(dateFormat).format(now)
        }

        val currentLanguage = remember {
            AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: ""
        }

        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.pref_category_display),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(MR.strings.pref_app_language) +
                        if (currentLanguage.isNotEmpty() && !currentLanguage.startsWith("en")) " (App Language)" else "",
                    onClick = { navigator.push(AppLanguageScreen()) },
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = uiPreferences.tabletUiMode(),
                    entries = TabletUiMode.entries
                        .associateWith { stringResource(it.titleRes) }
                        .toImmutableMap(),
                    title = stringResource(MR.strings.pref_tablet_ui_mode),
                    onValueChanged = {
                        context.toast(MR.strings.requires_app_restart)
                        true
                    },
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = uiPreferences.dateFormat(),
                    entries = DateFormats
                        .associateWith {
                            val formattedDate = UiPreferences.dateFormat(it).format(now)
                            "${it.ifEmpty { stringResource(MR.strings.label_default) }} ($formattedDate)"
                        }
                        .toImmutableMap(),
                    title = stringResource(MR.strings.pref_date_format),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.relativeTime(),
                    title = stringResource(MR.strings.pref_relative_format),
                    subtitle = stringResource(
                        MR.strings.pref_relative_format_summary,
                        stringResource(MR.strings.relative_time_today),
                        formattedNow,
                    ),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.imagesInDescription(),
                    title = stringResource(MR.strings.pref_display_images_description),
                ),
            ),
        )
    }

    // SY -->
    @Composable
    fun getForkGroup(uiPreferences: UiPreferences): Preference.PreferenceGroup {
        val previewsRowCount by uiPreferences.previewsRowCount().collectAsState()

        return Preference.PreferenceGroup(
            stringResource(SYMR.strings.pref_category_fork),
            preferenceItems = persistentListOf(
                // KMK -->
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.usePanoramaCoverFlow(),
                    title = stringResource(KMR.strings.pref_panorama_cover_flow),
                    subtitle = stringResource(KMR.strings.pref_panorama_cover_flow_summary),
                ),
                // KMK <--
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.expandFilters(),
                    title = stringResource(SYMR.strings.toggle_expand_search_filters),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.recommendsInOverflow(),
                    title = stringResource(SYMR.strings.put_recommends_in_overflow),
                    subtitle = stringResource(SYMR.strings.put_recommends_in_overflow_summary),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.mergeInOverflow(),
                    title = stringResource(SYMR.strings.put_merge_in_overflow),
                    subtitle = stringResource(SYMR.strings.put_merge_in_overflow_summary),
                ),
                Preference.PreferenceItem.SliderPreference(
                    value = previewsRowCount,
                    valueRange = 0..10,
                    title = stringResource(SYMR.strings.pref_previews_row_count),
                    valueString = if (previewsRowCount > 0) {
                        pluralStringResource(
                            SYMR.plurals.row_count,
                            previewsRowCount,
                            previewsRowCount,
                        )
                    } else {
                        stringResource(MR.strings.disabled)
                    },
                    onValueChanged = { uiPreferences.previewsRowCount().set(it) },
                ),
            ),
        )
    }

    @Composable
    fun getNavbarGroup(uiPreferences: UiPreferences): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            stringResource(SYMR.strings.pref_category_navbar),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.showNavUpdates(),
                    title = stringResource(SYMR.strings.pref_hide_updates_button),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.showNavHistory(),
                    title = stringResource(SYMR.strings.pref_hide_history_button),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = uiPreferences.bottomBarLabels(),
                    title = stringResource(SYMR.strings.pref_show_bottom_bar_labels),
                ),
            ),
        )
    }
    // SY <--
}

private val DateFormats = listOf(
    "", // Default
    "MM/dd/yy",
    "dd/MM/yy",
    "yyyy-MM-dd",
    "dd MMM yyyy",
    "MMM dd, yyyy",
)
