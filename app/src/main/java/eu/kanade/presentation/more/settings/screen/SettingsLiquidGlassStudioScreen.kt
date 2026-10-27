package eu.kanade.presentation.more.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Sparkles
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.catalog.components.LiquidButton
import com.kyant.backdrop.catalog.components.LiquidSlider
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.presentation.more.settings.screen.SearchableSettings
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.LiquidGlassElementType
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.util.collectAsState
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.math.roundToInt

object SettingsLiquidGlassStudioScreen : SearchableSettings {
    @Suppress("unused")
    private fun readResolve(): Any = SettingsLiquidGlassStudioScreen

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = MR.strings.pref_category_appearance

    @Composable
    override fun getPreferences(): List<Preference> {
        val uiPreferences = remember { Injekt.get<UiPreferences>() }
        val scope = rememberCoroutineScope()

        val isGlassEnabled by uiPreferences.liquidGlassEnabled().collectAsState()

        // Global
        val chromaticAberration by uiPreferences.liquidGlassChromaticAberration().collectAsState()
        val springPhysics by uiPreferences.liquidGlassSpringPhysics().collectAsState()

        // Buttons
        val btnRefraction by uiPreferences.liquidGlassButtonRefraction().collectAsState()
        val btnBlur by uiPreferences.liquidGlassButtonBlur().collectAsState()
        val btnTint by uiPreferences.liquidGlassButtonTintAlpha().collectAsState()

        // Popouts
        val popoutRefraction by uiPreferences.liquidGlassPopoutRefraction().collectAsState()
        val popoutBlur by uiPreferences.liquidGlassPopoutBlur().collectAsState()
        val popoutTint by uiPreferences.liquidGlassPopoutTintAlpha().collectAsState()

        // Sliders
        val sliderRefraction by uiPreferences.liquidGlassSliderRefraction().collectAsState()
        val sliderBlur by uiPreferences.liquidGlassSliderBlur().collectAsState()
        val sliderTint by uiPreferences.liquidGlassSliderTintAlpha().collectAsState()

        // Bars
        val barRefraction by uiPreferences.liquidGlassBarRefraction().collectAsState()
        val barBlur by uiPreferences.liquidGlassBarBlur().collectAsState()
        val barTint by uiPreferences.liquidGlassBarTintAlpha().collectAsState()

        return listOf(
            Preference.PreferenceGroup(
                title = "LIVE PREVIEW STUDIO",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.CustomPreference(
                        title = "Component Preview",
                    ) {
                        LiveStudioPreviewCard()
                    },
                ),
            ),
            Preference.PreferenceGroup(
                title = "GLOBAL OPTICAL ENGINE",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SwitchPreference(
                        preference = uiPreferences.liquidGlassEnabled(),
                        title = "Enable Liquid Glass UI",
                        subtitle = "Hardware-accelerated AGSL refractive shaders across application interface",
                    ),
                    Preference.PreferenceItem.SwitchPreference(
                        preference = uiPreferences.liquidGlassChromaticAberration(),
                        title = "Chromatic Aberration Dispersion",
                        subtitle = "Simulate optical lens RGB wave dispersion on curvature boundaries",
                        enabled = isGlassEnabled,
                    ),
                    Preference.PreferenceItem.SwitchPreference(
                        preference = uiPreferences.liquidGlassSpringPhysics(),
                        title = "Tactile Spring Physics",
                        subtitle = "Viscoelastic velocity deformation and bounce on interactive touch gestures",
                        enabled = isGlassEnabled,
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "BUTTONS & ACTION CHIPS",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SliderPreference(
                        value = (btnRefraction * 100).roundToInt(),
                        valueRange = 0..250,
                        title = "Button Refraction",
                        subtitle = "SDF refractive distortion and caustic lens depth on buttons",
                        valueString = "${(btnRefraction * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassButtonRefraction().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (btnBlur * 100).roundToInt(),
                        valueRange = 0..250,
                        title = "Button Frost Blur",
                        subtitle = "Gaussian diffusion and vibrancy blur for button surfaces",
                        valueString = "${(btnBlur * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassButtonBlur().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (btnTint * 100).roundToInt(),
                        valueRange = 0..200,
                        title = "Button Tint Opacity",
                        subtitle = "Translucency and glass color saturation for buttons",
                        valueString = "${(btnTint * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassButtonTintAlpha().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.TextPreference(
                        title = "Reset Button Defaults",
                        subtitle = "Restore default optical parameters for buttons",
                        enabled = isGlassEnabled,
                        onClick = {
                            scope.launch {
                                uiPreferences.liquidGlassButtonRefraction().set(1.0f)
                                uiPreferences.liquidGlassButtonBlur().set(1.0f)
                                uiPreferences.liquidGlassButtonTintAlpha().set(1.0f)
                            }
                        },
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "POPOUTS, DIALOGS & SHEETS",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SliderPreference(
                        value = (popoutRefraction * 100).roundToInt(),
                        valueRange = 0..250,
                        title = "Dialog Refraction",
                        subtitle = "Lens depth and background warp behind dialogs and sheets",
                        valueString = "${(popoutRefraction * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassPopoutRefraction().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (popoutBlur * 100).roundToInt(),
                        valueRange = 0..250,
                        title = "Dialog Frost Blur",
                        subtitle = "Frost density and backdrop diffusion on dialog cards",
                        valueString = "${(popoutBlur * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassPopoutBlur().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (popoutTint * 100).roundToInt(),
                        valueRange = 0..200,
                        title = "Dialog Tint Opacity",
                        subtitle = "Glass opacity and frost layer thickness on dialogs",
                        valueString = "${(popoutTint * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassPopoutTintAlpha().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.TextPreference(
                        title = "Reset Dialog Defaults",
                        subtitle = "Restore default optical parameters for popouts and dialogs",
                        enabled = isGlassEnabled,
                        onClick = {
                            scope.launch {
                                uiPreferences.liquidGlassPopoutRefraction().set(1.0f)
                                uiPreferences.liquidGlassPopoutBlur().set(1.0f)
                                uiPreferences.liquidGlassPopoutTintAlpha().set(1.0f)
                            }
                        },
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "SLIDERS & NAVIGATORS",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SliderPreference(
                        value = (sliderRefraction * 100).roundToInt(),
                        valueRange = 0..250,
                        title = "Slider Refraction",
                        subtitle = "Refractive glass distortion along slider thumb and track",
                        valueString = "${(sliderRefraction * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassSliderRefraction().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (sliderBlur * 100).roundToInt(),
                        valueRange = 0..250,
                        title = "Slider Frost Blur",
                        subtitle = "Vibrancy blur behind slider thumb lens",
                        valueString = "${(sliderBlur * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassSliderBlur().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (sliderTint * 100).roundToInt(),
                        valueRange = 0..200,
                        title = "Slider Tint Opacity",
                        subtitle = "Translucency of slider thumb lens",
                        valueString = "${(sliderTint * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassSliderTintAlpha().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.TextPreference(
                        title = "Reset Slider Defaults",
                        subtitle = "Restore default optical parameters for sliders",
                        enabled = isGlassEnabled,
                        onClick = {
                            scope.launch {
                                uiPreferences.liquidGlassSliderRefraction().set(1.0f)
                                uiPreferences.liquidGlassSliderBlur().set(1.0f)
                                uiPreferences.liquidGlassSliderTintAlpha().set(1.0f)
                            }
                        },
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "BARS & NAVIGATION CONTROLS",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SliderPreference(
                        value = (barRefraction * 100).roundToInt(),
                        valueRange = 0..250,
                        title = "Bar Refraction",
                        subtitle = "Refractive depth on top bars, navigation pills, and tab tracks",
                        valueString = "${(barRefraction * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassBarRefraction().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (barBlur * 100).roundToInt(),
                        valueRange = 0..250,
                        title = "Bar Frost Blur",
                        subtitle = "Background blur on navigation bars and segmented controls",
                        valueString = "${(barBlur * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassBarBlur().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (barTint * 100).roundToInt(),
                        valueRange = 0..200,
                        title = "Bar Tint Opacity",
                        subtitle = "Translucent tint strength on navigation bars",
                        valueString = "${(barTint * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            scope.launch { uiPreferences.liquidGlassBarTintAlpha().set(it / 100f) }
                        },
                    ),
                    Preference.PreferenceItem.TextPreference(
                        title = "Reset Bar Defaults",
                        subtitle = "Restore default optical parameters for bars and navigation",
                        enabled = isGlassEnabled,
                        onClick = {
                            scope.launch {
                                uiPreferences.liquidGlassBarRefraction().set(1.0f)
                                uiPreferences.liquidGlassBarBlur().set(1.0f)
                                uiPreferences.liquidGlassBarTintAlpha().set(1.0f)
                            }
                        },
                    ),
                ),
            ),
        )
    }

    @Composable
    private fun LiveStudioPreviewCard() {
        val isDark = isSystemInDarkTheme()
        var sliderValue by remember { mutableFloatStateOf(0.6f) }

        val canvasBackdrop = rememberCanvasBackdrop {
            val vibrantColors = if (isDark) {
                listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFFFFAF7B))
            } else {
                listOf(Color(0xFF00C9FF), Color(0xFF92FE9D), Color(0xFFFC5C7D))
            }
            drawRect(
                brush = Brush.linearGradient(vibrantColors),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedRectangle(24.dp, RoundedCornerStyle.Continuous))
                .background(
                    Brush.verticalGradient(
                        if (isDark) {
                            listOf(Color(0xFF1E1035), Color(0xFF0A0A10))
                        } else {
                            listOf(Color(0xFFE0F2FE), Color(0xFFF1F5F9))
                        },
                    ),
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "VETRO OPTICS ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "AGSL 120 FPS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFA1A1AA) else Color(0xFF71717A),
                    )
                }

                // Interactive Button Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LiquidButton(
                        onClick = {},
                        backdrop = canvasBackdrop,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "Interactive Glass",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .liquidGlassSurface(
                                shape = Capsule(),
                                blurRadius = 8.dp,
                                refractionHeight = 16.dp,
                                refractionAmount = 24.dp,
                                tintColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                elementType = LiquidGlassElementType.BUTTONS,
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "Action Chip",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }

                // Interactive Slider Preview
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Scrubber Lens (${(sliderValue * 100).roundToInt()}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFD4D4D8) else Color(0xFF3F3F46),
                    )
                    LiquidSlider(
                        value = { sliderValue },
                        onValueChange = { sliderValue = it },
                        valueRange = 0f..1f,
                        visibilityThreshold = 0.001f,
                        backdrop = canvasBackdrop,
                    )
                }

                // Popout Dialog & Segmented Bar Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassSurface(
                            shape = RoundedRectangle(16.dp, RoundedCornerStyle.Continuous),
                            blurRadius = 12.dp,
                            refractionHeight = 18.dp,
                            refractionAmount = 26.dp,
                            tintColor = if (isDark) Color(0x33FFFFFF) else Color(0x55FFFFFF),
                            elementType = LiquidGlassElementType.POPOUTS,
                        )
                        .padding(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Popout / Modal Surface",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Real-time optical caustic refraction behind dynamic artwork and text.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        )
                    }
                }
            }
        }
    }
}
