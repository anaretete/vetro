package eu.kanade.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.CupertinoFrostedPopup
import tachiyomi.presentation.core.components.material.CupertinoGlassMenuItem
import tachiyomi.presentation.core.components.material.CupertinoGlassMenuSeparator
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.i18n.stringResource
import androidx.compose.material3.DropdownMenu as ComposeDropdownMenu
import androidx.compose.material3.DropdownMenuItem as ComposeDropdownMenuItem

val DefaultDropdownMenuOffset = DpOffset(8.dp, (-56).dp)

/**
 * Universal DropdownMenu matching rit3zh/expo-glass-custom-menu specifications.
 */
@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DefaultDropdownMenuOffset,
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        CupertinoFrostedPopup(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            offset = offset,
            content = content,
        )
    } else {
        ComposeDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier.sizeIn(minWidth = 196.dp, maxWidth = 196.dp),
            offset = offset,
            scrollState = scrollState,
            properties = properties,
            content = content,
        )
    }
}

/**
 * Universal DropdownMenuItem matching rit3zh/expo-glass-custom-menu glass row specifications.
 */
@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    destructive: Boolean = false,
    selected: Boolean = false,
    colors: MenuItemColors? = null,
    contentPadding: PaddingValues? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        CupertinoGlassMenuItem(
            text = text,
            onClick = onClick,
            modifier = modifier,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            subtitle = subtitle,
            enabled = enabled,
            destructive = destructive,
            selected = selected,
            contentPadding = contentPadding ?: PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        )
    } else {
        ComposeDropdownMenuItem(
            text = text,
            onClick = onClick,
            modifier = modifier,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            enabled = enabled,
            colors = colors ?: androidx.compose.material3.MenuDefaults.itemColors(),
            contentPadding = contentPadding ?: androidx.compose.material3.MenuDefaults.DropdownMenuItemContentPadding,
            interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        )
    }
}

@Composable
fun DropdownMenuSeparator(
    modifier: Modifier = Modifier,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        CupertinoGlassMenuSeparator(modifier = modifier)
    } else {
        androidx.compose.material3.HorizontalDivider(modifier = modifier)
    }
}

@Composable
fun RadioMenuItem(
    text: @Composable () -> Unit,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        DropdownMenuItem(
            text = text,
            onClick = onClick,
            selected = isChecked,
            trailingIcon = if (isChecked) {
                {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(MR.strings.selected),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else null,
            modifier = modifier,
        )
    } else {
        DropdownMenuItem(
            text = text,
            onClick = onClick,
            trailingIcon = {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Outlined.RadioButtonChecked,
                        contentDescription = stringResource(MR.strings.selected),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = stringResource(MR.strings.not_selected),
                    )
                }
            },
            modifier = modifier,
        )
    }
}

@Composable
fun NestedMenuItem(
    text: @Composable () -> Unit,
    children: @Composable ColumnScope.(() -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    var nestedExpanded by remember { mutableStateOf(false) }
    val closeMenu = { nestedExpanded = false }

    Box {
        DropdownMenuItem(
            text = text,
            onClick = { nestedExpanded = true },
            trailingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowRight,
                    contentDescription = null,
                )
            },
        )

        DropdownMenu(
            expanded = nestedExpanded,
            onDismissRequest = closeMenu,
            modifier = modifier,
        ) {
            children(closeMenu)
        }
    }
}
