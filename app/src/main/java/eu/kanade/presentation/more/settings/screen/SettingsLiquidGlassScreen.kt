package eu.kanade.presentation.more.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.kyant.backdrop.catalog.components.LiquidButton
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle
import tachiyomi.presentation.core.components.material.Button
import tachiyomi.presentation.core.components.material.FilledTonalButton
import tachiyomi.presentation.core.components.material.Slider
import tachiyomi.presentation.core.components.material.Switch
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.presentation.more.settings.PreferenceScaffold
import eu.kanade.presentation.util.LocalBackPress
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.LocalLayerBackdrop
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.util.collectAsState
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.math.roundToInt

object SettingsLiquidGlassScreen : SearchableSettings {
    @Suppress("unused")
    private fun readResolve(): Any = SettingsLiquidGlassScreen

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = MR.strings.pref_category_appearance

    @Composable
    override fun Content() {
        val handleBack = LocalBackPress.current
        PreferenceScaffold(
            title = "Liquid Glass",
            onBackPressed = if (handleBack != null) handleBack::invoke else null,
            itemsProvider = { getPreferences() },
        )
    }

    @Composable
    override fun getPreferences(): List<Preference> {
        val uiPreferences = remember { Injekt.get<UiPreferences>() }
        val scope = rememberCoroutineScope()

        val isGlassEnabled by uiPreferences.liquidGlassEnabled().collectAsState()
        val refractionStrength by uiPreferences.liquidGlassRefractionStrength().collectAsState()
        val blurRadius by uiPreferences.liquidGlassBlurRadius().collectAsState()
        val lensHeight by uiPreferences.liquidGlassLensHeight().collectAsState()
        val tintAlpha by uiPreferences.liquidGlassTintAlpha().collectAsState()

        return listOf(
            Preference.PreferenceGroup(
                title = "LIVE PREVIEW",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.CustomPreference(
                        title = "Preview",
                    ) {
                        LiveGlassPreviewCard()
                    },
                ),
            ),
            Preference.PreferenceGroup(
                title = "ENGINE",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SwitchPreference(
                        preference = uiPreferences.liquidGlassEnabled(),
                        title = "Liquid Glass Engine",
                        subtitle = "Enable real AGSL refraction, continuous squircles, and Apple HIG physics",
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "OPTICS & REFRACTION",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SliderPreference(
                        value = (refractionStrength * 100).roundToInt(),
                        valueRange = 25..200,
                        title = "Refraction Strength",
                        subtitle = "Adjust lens refraction depth and optical distortion",
                        valueString = "${(refractionStrength * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            uiPreferences.liquidGlassRefractionStrength().set(it / 100f)
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (lensHeight * 100).roundToInt(),
                        valueRange = 50..200,
                        title = "Lens Height",
                        subtitle = "Control 3D optical lens dome curvature",
                        valueString = "${(lensHeight * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            uiPreferences.liquidGlassLensHeight().set(it / 100f)
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (blurRadius * 100).roundToInt(),
                        valueRange = 0..200,
                        title = "Blur Radius",
                        subtitle = "Background gaussian blur intensity",
                        valueString = "${(blurRadius * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            uiPreferences.liquidGlassBlurRadius().set(it / 100f)
                        },
                    ),
                    Preference.PreferenceItem.SliderPreference(
                        value = (tintAlpha * 100).roundToInt(),
                        valueRange = 20..200,
                        title = "Tint Opacity",
                        subtitle = "Translucent background tint saturation",
                        valueString = "${(tintAlpha * 100).roundToInt()}%",
                        enabled = isGlassEnabled,
                        onValueChanged = {
                            uiPreferences.liquidGlassTintAlpha().set(it / 100f)
                        },
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "PHYSICS & EFFECTS",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SwitchPreference(
                        preference = uiPreferences.liquidGlassDepthEffect(),
                        title = "3D Depth Lens Effect",
                        subtitle = "Enable 3D depth distortion and edge illumination",
                        enabled = isGlassEnabled,
                    ),
                    Preference.PreferenceItem.SwitchPreference(
                        preference = uiPreferences.liquidGlassChromaticAberration(),
                        title = "Chromatic Aberration",
                        subtitle = "Prismatic color dispersion along curved glass borders",
                        enabled = isGlassEnabled,
                    ),
                    Preference.PreferenceItem.SwitchPreference(
                        preference = uiPreferences.liquidGlassSpringPhysics(),
                        title = "Spring Physics Animation",
                        subtitle = "Fluid Apple spring rebound on press, drag, and release",
                        enabled = isGlassEnabled,
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "RESET",
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.TextPreference(
                        title = "Reset to Apple Defaults",
                        subtitle = "Restore all liquid glass optical and physics parameters to original values",
                        onClick = {
                            scope.launch {
                                uiPreferences.liquidGlassEnabled().set(true)
                                uiPreferences.liquidGlassChromaticAberration().set(true)
                                uiPreferences.liquidGlassRefractionStrength().set(1.0f)
                                uiPreferences.liquidGlassBlurRadius().set(1.0f)
                                uiPreferences.liquidGlassLensHeight().set(1.0f)
                                uiPreferences.liquidGlassTintAlpha().set(1.0f)
                                uiPreferences.liquidGlassSpringPhysics().set(true)
                                uiPreferences.liquidGlassDepthEffect().set(true)
                            }
                        },
                    ),
                ),
            ),
        )
    }

    @Composable
    private fun LiveGlassPreviewCard() {
        val isDark = isSystemInDarkTheme()
        val isGlass = LocalLiquidGlassConfig.current.enabled
        var previewToggle by remember { mutableStateOf(true) }
        var previewSliderValue by remember { mutableFloatStateOf(0.65f) }
        var buttonTaps by remember { mutableIntStateOf(0) }

        val cardModifier = if (isGlass) {
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedRectangle(22.dp, RoundedCornerStyle.Continuous))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F2027),
                            Color(0xFF203A43),
                            Color(0xFF2C5364),
                            Color(0xFF1B4D7E),
                        ),
                    ),
                )
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedRectangle(22.dp, RoundedCornerStyle.Continuous),
                )
                .padding(16.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp)
        }

        Box(modifier = cardModifier) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = if (isGlass) "Interactive Optics" else "Material Controls",
                            color = if (isGlass) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = if (isGlass) "Live refraction & spring preview" else "Standard Android M3 controls",
                            color = if (isGlass) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                    Switch(
                        checked = previewToggle,
                        onCheckedChange = { previewToggle = it },
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Slider(
                        value = previewSliderValue,
                        onValueChange = { previewSliderValue = it },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${(previewSliderValue * 100).roundToInt()}%",
                        color = if (isGlass) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isGlass) {
                        LiquidButton(
                            onClick = { buttonTaps++ },
                            shape = Capsule(),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            tint = Color(0xFF007AFF),
                        ) {
                            Text(
                                text = if (buttonTaps == 0) "Liquid Button" else "Tapped ($buttonTaps)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        LiquidButton(
                            onClick = { buttonTaps = 0 },
                            shape = Capsule(),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            tint = Color(0xFF5856D6),
                        ) {
                            Text(
                                text = "Reset",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    } else {
                        Button(
                            onClick = { buttonTaps++ },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                        ) {
                            Text(
                                text = if (buttonTaps == 0) "Primary Action" else "Tapped ($buttonTaps)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        FilledTonalButton(
                            onClick = { buttonTaps = 0 },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                        ) {
                            Text(
                                text = "Reset",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}
