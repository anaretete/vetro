package eu.kanade.presentation.more.settings.widget

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.shapes.Capsule
import eu.kanade.domain.ui.model.ThemeMode
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.i18n.stringResource

private val options = listOf(
    ThemeMode.SYSTEM to MR.strings.theme_system,
    ThemeMode.LIGHT to MR.strings.theme_light,
    ThemeMode.DARK to MR.strings.theme_dark,
)

@Composable
internal fun AppThemeModePreferenceWidget(
    value: ThemeMode,
    onItemClick: (ThemeMode) -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    if (isGlass) {
        val selectedIndex = options.indexOfFirst { it.first == value }.coerceAtLeast(0)
        BasePreferenceWidget(
            subcomponent = {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PrefsHorizontalPadding, vertical = 6.dp)
                        .height(36.dp)
                        .clip(Capsule())
                        .background(
                            if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    val segmentWidth = maxWidth / options.size.coerceAtLeast(1)
                    val indicatorOffset by animateDpAsState(
                        targetValue = segmentWidth * selectedIndex,
                        animationSpec = spring(
                            dampingRatio = 0.76f,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                        label = "ThemeModeSegmentOffset",
                    )

                    Box(
                        modifier = Modifier
                            .offset(x = indicatorOffset)
                            .width(segmentWidth)
                            .fillMaxHeight()
                            .liquidGlassSurface(
                                shape = Capsule(),
                                blurRadius = 8.dp,
                                refractionHeight = 12.dp,
                                refractionAmount = 20.dp,
                                tintColor = if (isDark) Color(0xFF636366).copy(alpha = 0.90f) else Color.White.copy(alpha = 0.95f),
                            ),
                    )

                    Row(modifier = Modifier.fillMaxSize()) {
                        options.forEachIndexed { index, (mode, labelRes) ->
                            val isSelected = index == selectedIndex
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(Capsule())
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onItemClick(mode) },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(labelRes),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        Color.White
                                    } else {
                                        Color(0xFF8E8E93)
                                    },
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            },
        )
    } else {
        BasePreferenceWidget(
            subcomponent = {
                MultiChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PrefsHorizontalPadding),
                ) {
                    options.forEachIndexed { index, (mode, labelRes) ->
                        SegmentedButton(
                            checked = mode == value,
                            onCheckedChange = { onItemClick(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index,
                                options.size,
                            ),
                        ) {
                            Text(stringResource(labelRes))
                        }
                    }
                }
            },
        )
    }
}
