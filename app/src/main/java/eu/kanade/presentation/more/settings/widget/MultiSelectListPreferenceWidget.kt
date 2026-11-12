package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import eu.kanade.presentation.more.settings.Preference
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.LabeledCheckbox
import tachiyomi.presentation.core.components.material.AlertDialog
import tachiyomi.presentation.core.components.material.CupertinoGlassMenuItem
import tachiyomi.presentation.core.components.material.CupertinoGlassMenuSeparator
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.TextButton
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun MultiSelectListPreferenceWidget(
    preference: Preference.PreferenceItem.MultiSelectListPreference,
    values: Set<String>,
    onValuesChange: (Set<String>) -> Unit,
) {
    var isDialogShown by remember { mutableStateOf(false) }

    TextPreferenceWidget(
        title = preference.title,
        subtitle = preference.subtitleProvider(values, preference.entries),
        icon = preference.icon,
        onPreferenceClick = { isDialogShown = true },
    )

    if (isDialogShown) {
        val selected = remember {
            preference.entries.keys
                .filter { values.contains(it) }
                .toMutableStateList()
        }
        AlertDialog(
            onDismissRequest = { isDialogShown = false },
            title = { Text(text = preference.title) },
            text = {
                val isGlass = LocalLiquidGlassConfig.current.enabled
                if (isGlass) {
                    val entryList = remember(preference.entries) { preference.entries.toList() }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    ) {
                        entryList.forEachIndexed { index, (key, label) ->
                            val isSelected = selected.contains(key)
                            CupertinoGlassMenuItem(
                                text = { Text(text = label) },
                                onClick = {
                                    if (isSelected) {
                                        selected.remove(key)
                                    } else {
                                        selected.add(key)
                                    }
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
                                } else null,
                            )
                            if (index < entryList.lastIndex) {
                                CupertinoGlassMenuSeparator()
                            }
                        }
                    }
                } else {
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
                }
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = true,
            ),
            confirmButton = {
                TextButton(
                    onClick = {
                        onValuesChange(selected.toMutableSet())
                        isDialogShown = false
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogShown = false }) {
                    Text(text = stringResource(MR.strings.action_cancel))
                }
            },
        )
    }
}
