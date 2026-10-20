package tachiyomi.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.DisabledByDefault
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.StringResource
import tachiyomi.core.common.preference.Preference
import tachiyomi.core.common.preference.TriState
import tachiyomi.core.common.preference.toggle
import tachiyomi.presentation.core.components.material.CupertinoCheckmark
import tachiyomi.presentation.core.components.material.CupertinoSwitch
import tachiyomi.presentation.core.components.material.DISABLED_ALPHA
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.Slider
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.theme.header
import tachiyomi.presentation.core.util.collectAsState
import tachiyomi.presentation.core.util.secondaryItemAlpha
import kotlin.math.roundToInt

object SettingsItemsPaddings {
    val Horizontal = 24.dp
    val Vertical = 10.dp
}

@Composable
fun HeadingItem(labelRes: StringResource) {
    HeadingItem(stringResource(labelRes))
}

@Composable
fun HeadingItem(text: String) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    Text(
        text = text,
        style = if (isGlass) {
            MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.White else Color(0xFF1C1C1E),
                letterSpacing = (-0.2).sp,
            )
        } else {
            MaterialTheme.typography.header
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isGlass) 20.dp else SettingsItemsPaddings.Horizontal,
                vertical = if (isGlass) 8.dp else SettingsItemsPaddings.Vertical,
            ),
    )
}

@Composable
fun IconItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    BaseSettingsItem(
        label = label,
        widget = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        onClick = onClick,
    )
}

@Composable
fun SortItem(label: String, sortDescending: Boolean?, onClick: () -> Unit) {
    val arrowIcon = when (sortDescending) {
        true -> Icons.Default.ArrowDownward
        false -> Icons.Default.ArrowUpward
        null -> null
    }

    BaseSortItem(
        label = label,
        icon = arrowIcon,
        onClick = onClick,
    )
}

@Composable
fun BaseSortItem(label: String, icon: ImageVector?, onClick: () -> Unit) {
    BaseSettingsItem(
        label = label,
        widget = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        },
        onClick = onClick,
    )
}

@Composable
fun CheckboxItem(label: String, pref: Preference<Boolean>) {
    val checked by pref.collectAsState()
    CheckboxItem(
        label = label,
        checked = checked,
        onClick = { pref.toggle() },
    )
}

@Composable
fun CheckboxItem(label: String, checked: Boolean, onClick: () -> Unit) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    BaseSettingsItem(
        label = label,
        widget = {
            if (isGlass) {
                CupertinoSwitch(
                    checked = checked,
                    onCheckedChange = { onClick() },
                )
            } else {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null,
                )
            }
        },
        onClick = onClick,
    )
}

@Composable
fun RadioItem(label: String, selected: Boolean, onClick: () -> Unit) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    BaseSettingsItem(
        label = label,
        widget = {
            if (isGlass) {
                CupertinoCheckmark(
                    checked = selected,
                )
            } else {
                RadioButton(
                    selected = selected,
                    onClick = null,
                )
            }
        },
        onClick = onClick,
    )
}

@Composable
fun SliderItem(
    value: Int,
    valueRange: IntProgression,
    label: String,
    onChange: (Int) -> Unit,
    steps: Int = with(valueRange) { (last - first) - 1 },
    valueString: String = value.toString(),
    labelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    pillColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    enabled: Boolean = true,
    accentColor: Color? = null,
) {
    BaseSliderItem(
        value = value,
        valueRange = valueRange,
        steps = steps,
        title = label,
        valueString = valueString,
        onChange = onChange,
        titleStyle = labelStyle,
        pillColor = pillColor,
        enabled = enabled,
        accentColor = accentColor,
        modifier = Modifier.padding(
            horizontal = SettingsItemsPaddings.Horizontal,
            vertical = SettingsItemsPaddings.Vertical,
        ),
    )
}

@Composable
fun BaseSliderItem(
    value: Int,
    valueRange: IntProgression,
    title: String,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    steps: Int = with(valueRange) { (last - first) - 1 },
    valueString: String = value.toString(),
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    subtitleStyle: TextStyle = MaterialTheme.typography.bodySmall,
    pillColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    enabled: Boolean = true,
    accentColor: Color? = null,
) {
    val haptic = LocalHapticFeedback.current
    var localPosition by remember(value) { mutableFloatStateOf(value.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier else Modifier.alpha(0.38f))
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = titleStyle,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = subtitleStyle,
                        modifier = Modifier.secondaryItemAlpha(),
                    )
                }
            }
            val isPercentage = valueString.endsWith("%")
            Pill(
                text = if (localPosition.roundToInt() == value) {
                    valueString
                } else if (isPercentage) {
                    "${localPosition.roundToInt()}%"
                } else {
                    "${localPosition.roundToInt()}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = pillColor,
            )
        }
        Slider(
            value = localPosition,
            onValueChange = f@{ nextFloat ->
                if (!enabled) return@f
                localPosition = nextFloat
                val nextInt = nextFloat.roundToInt()
                if (nextInt != value) {
                    onChange(nextInt)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            },
            valueRange = with(valueRange) { first.toFloat()..last.toFloat() },
            steps = steps,
            enabled = enabled,
            accentColor = accentColor,
        )
    }
}

@Composable
@PreviewLightDark
fun SliderItemPreview() {
    MaterialTheme(if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        var value by remember { mutableIntStateOf(0) }
        Surface {
            BaseSliderItem(
                value = value,
                valueRange = 0..10,
                title = "Item per row",
                valueString = if (value == 0) "Auto" else value.toString(),
                onChange = { value = it },
                modifier = Modifier.padding(
                    horizontal = SettingsItemsPaddings.Horizontal,
                    vertical = SettingsItemsPaddings.Vertical,
                ),
            )
        }
    }
}

@Composable
fun SelectItem(
    label: String,
    options: Array<out Any?>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .padding(
                    horizontal = SettingsItemsPaddings.Horizontal,
                    vertical = SettingsItemsPaddings.Vertical,
                ),
            label = { Text(text = label) },
            value = options[selectedIndex].toString(),
            onValueChange = {},
            enabled = false,
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
            ),
        )

        ExposedDropdownMenu(
            modifier = Modifier.exposedDropdownSize(),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = { Text(text.toString()) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
fun TriStateItem(
    label: String,
    state: TriState,
    enabled: Boolean = true,
    onClick: ((TriState) -> Unit)?,
) {
    val isGlass = tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig.current.enabled

    Row(
        modifier = Modifier
            .clickable(
                enabled = enabled && onClick != null,
                onClick = {
                    when (state) {
                        TriState.DISABLED -> onClick?.invoke(TriState.ENABLED_IS)
                        TriState.ENABLED_IS -> onClick?.invoke(TriState.ENABLED_NOT)
                        TriState.ENABLED_NOT -> onClick?.invoke(TriState.DISABLED)
                    }
                },
            )
            .fillMaxWidth()
            .padding(
                horizontal = SettingsItemsPaddings.Horizontal,
                vertical = SettingsItemsPaddings.Vertical,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.large),
    ) {
        val stateAlpha = if (enabled && onClick != null) 1f else DISABLED_ALPHA

        if (isGlass) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = stateAlpha),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier.weight(1f),
            )
            when (state) {
                TriState.ENABLED_IS -> CupertinoCheckmark(checked = true, tint = Color(0xFF0A84FF))
                TriState.DISABLED -> CupertinoCheckmark(checked = false)
                TriState.ENABLED_NOT -> {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF453A)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        } else {
            Icon(
                imageVector = when (state) {
                    TriState.DISABLED -> Icons.Rounded.CheckBoxOutlineBlank
                    TriState.ENABLED_IS -> Icons.Rounded.CheckBox
                    TriState.ENABLED_NOT -> Icons.Rounded.DisabledByDefault
                },
                contentDescription = null,
                tint = if (!enabled || state == TriState.DISABLED) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = stateAlpha)
                } else {
                    when (onClick) {
                        null -> MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ALPHA)
                        else -> MaterialTheme.colorScheme.primary
                    }
                },
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = stateAlpha),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun TextItem(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SettingsItemsPaddings.Horizontal, vertical = 4.dp),
        label = { Text(text = label) },
        value = value,
        onValueChange = onChange,
        singleLine = true,
    )
}

// SY -->
@Composable
fun IconItem(
    label: String,
    icon: Painter,
    selected: Boolean,
    onClick: () -> Unit,
) {
    BaseSettingsItem(
        label = label,
        widget = {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        },
        onClick = onClick,
    )
}
// SY <--

@Composable
fun SettingsChipRow(labelRes: StringResource, content: @Composable FlowRowScope.() -> Unit) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    Column {
        HeadingItem(labelRes)
        FlowRow(
            modifier = Modifier.padding(
                start = if (isGlass) 20.dp else SettingsItemsPaddings.Horizontal,
                top = 0.dp,
                end = if (isGlass) 20.dp else SettingsItemsPaddings.Horizontal,
                bottom = if (isGlass) 12.dp else SettingsItemsPaddings.Vertical,
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}

@Composable
fun SettingsIconGrid(labelRes: StringResource, content: LazyGridScope.() -> Unit) {
    Column {
        HeadingItem(labelRes)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            modifier = Modifier
                .heightIn(max = 360.dp)
                .padding(
                    start = SettingsItemsPaddings.Horizontal,
                    end = SettingsItemsPaddings.Horizontal,
                    bottom = SettingsItemsPaddings.Vertical,
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            content = content,
        )
    }
}

@Composable
private fun BaseSettingsItem(
    label: String,
    widget: @Composable RowScope.() -> Unit,
    onClick: () -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(
                horizontal = SettingsItemsPaddings.Horizontal,
                vertical = SettingsItemsPaddings.Vertical,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isGlass) Arrangement.SpaceBetween else Arrangement.spacedBy(24.dp),
    ) {
        if (isGlass) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            widget(this)
        } else {
            widget(this)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
