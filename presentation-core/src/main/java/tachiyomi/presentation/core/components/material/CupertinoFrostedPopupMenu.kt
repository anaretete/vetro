package tachiyomi.presentation.core.components.material

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

/**
 * An authentic Apple iOS 26 frosted liquid glass popup menu container.
 */
@Composable
fun CupertinoFrostedPopup(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 4.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    val popupSurface = if (isDark) Color(0xDC202024) else Color(0xF4FAFAFC)
    val popupTextColor = if (isDark) Color.White else Color(0xFF111113)
    val popupBorder = if (isDark) Color(0x35FFFFFF) else Color(0x22000000)

    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = shape),
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            offset = offset,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
            modifier = modifier
                .widthIn(min = 190.dp, max = 280.dp)
                .liquidGlassSurface(
                    shape = shape,
                    blurRadius = 24.dp,
                    refractionHeight = 16.dp,
                    refractionAmount = 22.dp,
                    tintColor = Color.Unspecified,
                    surfaceColor = popupSurface,
                )
                .border(
                    width = 0.5.dp,
                    color = popupBorder,
                    shape = shape,
                ),
            shape = shape,
            containerColor = Color.Transparent,
            shadowElevation = 14.dp,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides popupTextColor,
            ) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.bodyLarge.copy(
                        color = popupTextColor,
                    ),
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * A tiny frosted liquid glass popup with selectable options formatted like native Apple iOS pop-up buttons.
 */
@Composable
fun <T> CupertinoFrostedPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    entries: Map<out T, String>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 4.dp),
) {
    val isDark = isSystemInDarkTheme()

    CupertinoFrostedPopup(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        modifier = modifier,
    ) {
        val entryList = remember(entries) { entries.toList() }
        entryList.forEachIndexed { index, (key, label) ->
            val isSelected = key == selectedValue
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelect(key)
                        onDismissRequest()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else if (isDark) {
                            Color.White
                        } else {
                            Color(0xFF111113)
                        },
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(18.dp),
                    )
                }
            }

            if (index < entryList.lastIndex) {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = if (isDark) Color(0x25FFFFFF) else Color(0x18000000),
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        }
    }
}
