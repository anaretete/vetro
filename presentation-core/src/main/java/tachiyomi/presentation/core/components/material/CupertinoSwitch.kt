package tachiyomi.presentation.core.components.material

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.catalog.components.LiquidToggle
import com.kyant.shapes.Capsule

@Composable
fun rememberDefaultSwitchBackdrop(): Backdrop {
    val isDark = isSystemInDarkTheme()
    return rememberCanvasBackdrop {
        drawRect(if (isDark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF))
    }
}

/**
 * Authentic Apple iOS Cupertino Liquid Glass Switch (UISwitch standard).
 * Powered by Kyant0's authentic catalog LiquidToggle with DampedDragAnimation physics.
 */
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop? = null,
) {
    val effectiveBackdrop = backdrop ?: LocalLayerBackdrop.current ?: rememberDefaultSwitchBackdrop()
    val currentChecked by rememberUpdatedState(checked)
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    LiquidToggle(
        selected = { currentChecked },
        onSelect = { newValue -> if (enabled) currentOnCheckedChange?.invoke(newValue) },
        backdrop = effectiveBackdrop,
        modifier = modifier.alpha(if (enabled) 1f else 0.5f),
    )
}

/**
 * Authentic Apple iOS Solid UISwitch (used when Liquid Glass is disabled).
 * Features standard 51dp x 31dp capsule, 27dp thumb, Apple emerald green active fill.
 */
@Composable
fun SolidCupertinoSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val isDark = isSystemInDarkTheme()
    val activeColor = if (isDark) Color(0xFF30D158) else Color(0xFF34C759)
    val inactiveColor = if (isDark) Color(0xFF39393D) else Color(0xFFE9E9EB)

    val trackColor by animateColorAsState(
        targetValue = if (checked) activeColor else inactiveColor,
        animationSpec = spring(stiffness = 500f),
        label = "SwitchTrackColor",
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 600f),
        label = "SwitchThumbOffset",
    )

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .size(width = 51.dp, height = 31.dp)
            .clip(Capsule())
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled && onCheckedChange != null,
                role = Role.Switch,
                onClick = { onCheckedChange?.invoke(!checked) },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .shadow(
                    elevation = 1.5.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.12f),
                    spotColor = Color.Black.copy(alpha = 0.18f),
                )
                .size(27.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
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
    val effectiveBackdrop = backdrop ?: LocalLayerBackdrop.current ?: rememberDefaultSwitchBackdrop()
    CupertinoSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        backdrop = effectiveBackdrop,
    )
}
