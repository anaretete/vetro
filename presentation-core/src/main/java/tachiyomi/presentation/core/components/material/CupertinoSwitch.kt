package tachiyomi.presentation.core.components.material

import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.catalog.components.LiquidToggle

/**
 * Authentic Apple iOS Cupertino Switch.
 * Standard 51dp x 31dp stadium capsule with Apple Emerald Green active fill and liquid glass optics.
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop? = null,
) {
    val currentChecked by rememberUpdatedState(checked)
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val effectiveBackdrop = backdrop ?: LocalLayerBackdrop.current

    LiquidToggle(
        selected = { currentChecked },
        onSelect = { newValue -> if (enabled) currentOnCheckedChange?.invoke(newValue) },
        backdrop = effectiveBackdrop,
        modifier = modifier.alpha(if (enabled) 1f else 0.38f),
        enabled = enabled,
    )
}

/**
 * Universal drop-in replacement for Material 3 Switch with automatic Apple Cupertino styling.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    backdrop: Backdrop? = null,
) {
    CupertinoSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        backdrop = backdrop,
    )
}
