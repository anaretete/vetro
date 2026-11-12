package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import eu.kanade.presentation.more.settings.Preference
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.LabeledCheckbox
import tachiyomi.presentation.core.components.material.AlertDialog
import tachiyomi.presentation.core.components.material.CupertinoFrostedPopup
import tachiyomi.presentation.core.components.material.CupertinoGlassMenuItem
import tachiyomi.presentation.core.components.material.CupertinoGlassMenuSeparator
import tachiyomi.presentation.core.components.material.GlassMenuAnchor
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.TextButton
import tachiyomi.presentation.core.components.material.supportsNativeGlassMenu
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun MultiSelectListPreferenceWidget(
    preference: Preference.PreferenceItem.MultiSelectListPreference,
    values: Set<String>,
    onValuesChange: (Set<String>) -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    var isPopupExpanded by remember { mutableStateOf(false) }
    val currentValues by rememberUpdatedState(values)
    val currentOnValuesChange by rememberUpdatedState(onValuesChange)

    if (isGlass) {
        TextPreferenceWidget(
            title = preference.title,
            subtitle = preference.subtitleProvider(values, preference.entries),
            icon = preference.icon,
            onPreferenceClick = { isPopupExpanded = true },
            widget = {
                Box(contentAlignment = Alignment.CenterEnd) {
                    if (supportsNativeGlassMenu) {
                        val items = remember(preference.entries, values) {
                            preference.entries.map { (key, label) ->
                                GlassMenuItemRecord(
                                    id = key,
                                    title = label,
                                    checked = values.contains(key),
                                    keepOpen = true,
                                    onClick = {
                                        val updated = currentValues.toMutableSet()
                                        if (updated.contains(key)) {
                                            updated.remove(key)
                                        } else {
                                            updated.add(key)
                                        }
                                        currentOnValuesChange(updated)
                                    },
                                )
                            }
                        }
                        GlassMenuAnchor(
                            expanded = isPopupExpanded,
                            onDismissRequest = { isPopupExpanded = false },
                            items = items,
                            modifier = Modifier.matchParentSize(),
                        )
                    } else {
                        CupertinoFrostedPopup(
                            expanded = isPopupExpanded,
                            onDismissRequest = { isPopupExpanded = false },
                        ) {
                            val entryList = remember(preference.entries) { preference.entries.toList() }
                            entryList.forEachIndexed { index, (key, label) ->
                                val isSelected = values.contains(key)
                                CupertinoGlassMenuItem(
                                    text = { Text(text = label) },
                                    onClick = {
                                        val updated = currentValues.toMutableSet()
                                        if (isSelected) {
                                            updated.remove(key)
                                        } else {
                                            updated.add(key)
                                        }
                                        currentOnValuesChange(updated)
                                    },
                                    selected = isSelected,
                                    trailingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                    } else {
                                        null
                                    },
                                )
                                if (index < entryList.lastIndex) {
                                    CupertinoGlassMenuSeparator()
                                }
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93).copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp),
                    )
                }
            },
        )
    } else {
        TextPreferenceWidget(
            title = preference.title,
            subtitle = preference.subtitleProvider(values, preference.entries),
            icon = preference.icon,
            onPreferenceClick = { isPopupExpanded = true },
        )

        if (isPopupExpanded) {
            val selected = remember {
                preference.entries.keys
                    .filter { values.contains(it) }
                    .toMutableStateList()
            }
            AlertDialog(
                onDismissRequest = { isPopupExpanded = false },
                title = { Text(text = preference.title) },
                text = {
                    LazyColumn {
                        preference.entries.forEach { current ->
                            item {
                                val isSelected = selected.contains(current.key)
                                LabeledCheckbox(
                                    label = current.value,
                                    checked = isSelected,
                                    onCheckedChange = {
                                        if (it) {
                                            selected.add(current.key)
                                        } else {
                                            selected.remove(current.key)
                                        }
                                    },
                                )
                            }
                        }
                    }
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = true,
                ),
                confirmButton = {
                    TextButton(
                        onClick = {
                            onValuesChange(selected.toMutableSet())
                            isPopupExpanded = false
                        },
                    ) {
                        Text(text = stringResource(MR.strings.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isPopupExpanded = false }) {
                        Text(text = stringResource(MR.strings.action_cancel))
                    }
                },
            )
        }
    }
}
