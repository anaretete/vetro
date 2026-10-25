package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.AlertDialog
import tachiyomi.presentation.core.components.material.CupertinoFrostedPopupMenu
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.TextButton
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
fun <T> ListPreferenceWidget(
    value: T,
    title: String,
    subtitle: String?,
    icon: ImageVector?,
    entries: Map<out T, String>,
    onValueChange: (T) -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled

    if (isGlass) {
        var isPopupExpanded by remember { mutableStateOf(false) }
        val currentLabel = entries[value] ?: ""
        val effectiveSubtitle = if (subtitle != null && subtitle != currentLabel && subtitle != "%s") {
            subtitle
        } else {
            null
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            BasePreferenceWidget(
                title = title,
                subcomponent = if (!effectiveSubtitle.isNullOrBlank()) {
                    {
                        val textStartPadding = if (icon != null) 0.dp else PrefsHorizontalPadding
                        Text(
                            text = effectiveSubtitle,
                            modifier = Modifier
                                .padding(start = textStartPadding, end = PrefsHorizontalPadding)
                                .secondaryItemAlpha(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                color = Color(0xFF8E8E93),
                            ),
                            maxLines = 10,
                        )
                    }
                } else {
                    null
                },
                icon = if (icon != null) {
                    {
                        Icon(
                            imageVector = icon,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                        )
                    }
                } else {
                    null
                },
                onClick = { isPopupExpanded = true },
                widget = {
                    Box(contentAlignment = Alignment.CenterEnd) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = currentLabel,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 17.sp,
                                    color = Color(0xFF8E8E93),
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF8E8E93).copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        CupertinoFrostedPopupMenu(
                            expanded = isPopupExpanded,
                            onDismissRequest = { isPopupExpanded = false },
                            entries = entries,
                            selectedValue = value,
                            onSelect = onValueChange,
                            modifier = Modifier.heightIn(max = 360.dp),
                        )
                    }
                },
            )
        }
    } else {
        var isDialogShown by remember { mutableStateOf(false) }

        TextPreferenceWidget(
            title = title,
            subtitle = subtitle,
            icon = icon,
            onPreferenceClick = { isDialogShown = true },
        )

        if (isDialogShown) {
            AlertDialog(
                onDismissRequest = { isDialogShown = false },
                title = { Text(text = title) },
                text = {
                    Box {
                        val state = rememberLazyListState()
                        ScrollbarLazyColumn(state = state) {
                            entries.forEach { current ->
                                val isSelected = value == current.key
                                item {
                                    DialogRow(
                                        label = current.value,
                                        isSelected = isSelected,
                                        onSelected = {
                                            onValueChange(current.key!!)
                                            isDialogShown = false
                                        },
                                    )
                                }
                            }
                        }
                        if (state.canScrollBackward) HorizontalDivider(modifier = Modifier.align(Alignment.TopCenter))
                        if (state.canScrollForward) HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
                    }
                },
                confirmButton = {
                    TextButton(onClick = { isDialogShown = false }) {
                        Text(text = stringResource(MR.strings.action_cancel))
                    }
                },
            )
        }
    }
}

@Composable
private fun DialogRow(
    label: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val isGlass = tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig.current.enabled
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected = isSelected,
                onClick = { if (!isSelected) onSelected() },
            )
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .padding(horizontal = 4.dp),
    ) {
        if (isGlass) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.merge(),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
            )
            tachiyomi.presentation.core.components.material.CupertinoCheckmark(
                checked = isSelected,
            )
        } else {
            RadioButton(
                selected = isSelected,
                onClick = null,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.merge(),
                modifier = Modifier.padding(start = 24.dp),
            )
        }
    }
}
