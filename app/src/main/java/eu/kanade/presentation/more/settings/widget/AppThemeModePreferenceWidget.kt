package eu.kanade.presentation.more.settings.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.domain.ui.model.ThemeMode
import kotlinx.collections.immutable.persistentMapOf
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.CupertinoFrostedPopupMenu
import tachiyomi.presentation.core.i18n.stringResource

private val options = persistentMapOf(
    ThemeMode.SYSTEM to MR.strings.theme_system,
    ThemeMode.LIGHT to MR.strings.theme_light,
    ThemeMode.DARK to MR.strings.theme_dark,
)

@Composable
internal fun AppThemeModePreferenceWidget(
    value: ThemeMode,
    onItemClick: (ThemeMode) -> Unit,
) {
    var isPopupExpanded by remember { mutableStateOf(false) }
    val currentLabel = stringResource(options[value] ?: MR.strings.theme_system)

    Box(modifier = Modifier.fillMaxWidth()) {
        BasePreferenceWidget(
            title = stringResource(MR.strings.pref_category_theme),
            onClick = { isPopupExpanded = true },
            widget = {
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
            },
        )

        CupertinoFrostedPopupMenu(
            expanded = isPopupExpanded,
            onDismissRequest = { isPopupExpanded = false },
            entries = options.mapValues { stringResource(it.value) },
            selectedValue = value,
            onSelect = onItemClick,
        )
    }
}
