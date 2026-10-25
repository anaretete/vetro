package tachiyomi.presentation.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tachiyomi.presentation.core.components.material.CupertinoCheckmark
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.padding

@Composable
fun LabeledCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(
                role = Role.Checkbox,
                onClick = {
                    if (enabled) {
                        onCheckedChange(!checked)
                    }
                },
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isGlass) Arrangement.SpaceBetween else Arrangement.spacedBy(MaterialTheme.padding.small),
    ) {
        if (isGlass) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            CupertinoCheckmark(
                checked = checked,
                enabled = enabled,
            )
        } else {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
            )
            Text(text = label)
        }
    }
}
