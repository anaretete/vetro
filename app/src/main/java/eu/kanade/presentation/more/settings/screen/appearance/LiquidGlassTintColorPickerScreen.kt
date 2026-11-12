package eu.kanade.presentation.more.settings.screen.appearance

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.kyant.shapes.Capsule
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.util.Screen
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.liquidGlassPopupSurface
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.util.collectAsState
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.math.roundToInt

class LiquidGlassTintColorPickerScreen : Screen() {

    @OptIn(ExperimentalLayoutApi::class, ExperimentalStdlibApi::class)
    @Composable
    override fun Content() {
        val uiPreferences: UiPreferences = Injekt.get()
        val navigator = LocalNavigator.currentOrThrow

        val tintPref = uiPreferences.liquidGlassTintColor()
        val savedTintInt by tintPref.collectAsState()

        val initialColor = remember(savedTintInt) {
            if (savedTintInt != 0) Color(savedTintInt) else Color(0xFF007AFF)
        }

        val controller = rememberColorPickerController()
        var selectedColor by remember { mutableStateOf(initialColor) }
        var isCustomSet by remember { mutableStateOf(savedTintInt != 0) }
        var hexCode by remember { mutableStateOf(initialColor.toArgb().toHexString()) }

        val wheelSize = with(LocalDensity.current) { 20.dp.toPx().roundToInt() }
        val wheelStrokeWidth = with(LocalDensity.current) { 2.dp.toPx() }

        val wheelBitmap = remember(wheelSize, wheelStrokeWidth) {
            val bitmap = Bitmap.createBitmap(wheelSize, wheelSize, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = wheelStrokeWidth
                isAntiAlias = true
            }
            canvas.drawCircle(
                wheelSize / 2f,
                wheelSize / 2f,
                wheelSize / 2f - wheelStrokeWidth,
                paint,
            )
            bitmap.asImageBitmap()
        }

        val presets = listOf(
            Preset("Default (Crystal)", 0),
            Preset("iOS Blue", 0xFF007AFF.toInt()),
            Preset("Teal / Cyan", 0xFF30B0C7.toInt()),
            Preset("Indigo", 0xFF5856D6.toInt()),
            Preset("Purple", 0xFFAF52DE.toInt()),
            Preset("Rose Pink", 0xFFFF2D55.toInt()),
            Preset("Sunset Amber", 0xFFFF9500.toInt()),
            Preset("Emerald Green", 0xFF34C759.toInt()),
            Preset("Smoke / Slate", 0xFF48484A.toInt()),
            Preset("Frost White", 0xFFF2F2F7.toInt()),
        )

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = "Liquid Glass Tint Color",
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Live Liquid Glass Preview Card
                Text(
                    text = "Live Glass Preview",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start),
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2E3192),
                                    Color(0xFF1BFFFF),
                                    Color(0xFFD4145A),
                                    Color(0xFFFBB03B),
                                ),
                            ),
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val previewTint = if (isCustomSet) selectedColor else Color.Unspecified
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlassPopupSurface(
                                shape = Capsule(),
                                tintColor = previewTint,
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = if (isCustomSet) "Custom Tint (#$hexCode)" else "Crystal Glass (Default)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White),
                        )
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .liquidGlassSurface(
                                    shape = CircleShape,
                                    tintColor = previewTint,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // HSV Color Picker Wheel
                HsvColorPicker(
                    modifier = Modifier.size(240.dp),
                    controller = controller,
                    wheelImageBitmap = wheelBitmap,
                    initialColor = initialColor,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        hexCode = colorEnvelope.hexCode
                        selectedColor = colorEnvelope.color
                        isCustomSet = true
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp),
                    controller = controller,
                    borderRadius = 13.dp,
                    wheelImageBitmap = wheelBitmap,
                    initialColor = initialColor,
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Presets FlowRow
                Text(
                    text = "Presets",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start),
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (preset in presets) {
                        val isSelected = if (preset.argb == 0) !isCustomSet else (isCustomSet && selectedColor.toArgb() == preset.argb)
                        val chipBg = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
                        val chipText = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

                        Row(
                            modifier = Modifier
                                .clip(Capsule())
                                .background(chipBg)
                                .clickable {
                                    if (preset.argb == 0) {
                                        isCustomSet = false
                                    } else {
                                        isCustomSet = true
                                        selectedColor = Color(preset.argb)
                                        hexCode = selectedColor.toArgb().toHexString()
                                        controller.selectByColor(selectedColor, fromUser = false)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            if (preset.argb != 0) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color(preset.argb)),
                                )
                            }
                            Text(
                                text = preset.name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = chipText,
                                ),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Apply & Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            tintPref.set(0)
                            navigator.pop()
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) {
                        Icon(Icons.Outlined.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Default")
                    }

                    Button(
                        onClick = {
                            if (isCustomSet) {
                                tintPref.set(selectedColor.toArgb())
                            } else {
                                tintPref.set(0)
                            }
                            navigator.pop()
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) {
                        Text("Apply Tint")
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    private data class Preset(val name: String, val argb: Int)
}
