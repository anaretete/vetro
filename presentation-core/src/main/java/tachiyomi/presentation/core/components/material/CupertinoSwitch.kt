package tachiyomi.presentation.core.components.material

import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidToggle

/**
 * Authentic Apple iOS 26 Cupertino Liquid Glass Switch (UISwitch standard).
 * Powered by Kyant0's authentic catalog LiquidToggle with DampedDragAnimation physics.
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop = LocalLayerBackdrop.current ?: rememberLayerBackdrop(),
) {
    val currentChecked by rememberUpdatedState(checked)
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    LiquidToggle(
        selected = { currentChecked },
        onSelect = { newValue -> if (enabled) currentOnCheckedChange?.invoke(newValue) },
        backdrop = backdrop,
        modifier = modifier.alpha(if (enabled) 1f else 0.5f),
    )
}

/**
 * Global drop-in replacement for Material 3 Switch with automatic Cupertino Liquid Glass styling.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    backdrop: Backdrop = LocalLayerBackdrop.current ?: rememberLayerBackdrop(),
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        CupertinoSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
            backdrop = backdrop,
        )
    } else {
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            thumbContent = thumbContent,
            enabled = enabled,
            colors = colors,
        )
    }
}
