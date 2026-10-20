package tachiyomi.presentation.core.components.material

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.shapes.Capsule
import tachiyomi.presentation.core.util.animateElevation
import androidx.compose.material3.AssistChip as M3AssistChip
import androidx.compose.material3.Button as M3Button
import androidx.compose.material3.ButtonColors as M3ButtonColors
import androidx.compose.material3.ButtonDefaults as M3ButtonDefaults
import androidx.compose.material3.ButtonElevation as M3ButtonElevation
import androidx.compose.material3.ElevatedButton as M3ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton as M3ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton as M3FilledIconButton
import androidx.compose.material3.FilledTonalButton as M3FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton as M3FilledTonalIconButton
import androidx.compose.material3.FilterChip as M3FilterChip
import androidx.compose.material3.FloatingActionButton as M3FloatingActionButton
import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.material3.LargeFloatingActionButton as M3LargeFloatingActionButton
import androidx.compose.material3.OutlinedButton as M3OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton as M3SmallFloatingActionButton
import androidx.compose.material3.SuggestionChip as M3SuggestionChip
import androidx.compose.material3.TextButton as M3TextButton

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: M3ButtonColors = M3ButtonDefaults.buttonColors(),
    elevation: M3ButtonElevation? = M3ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = M3ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == CircleShape) Capsule() else shape
    val effectiveColors = if (isGlass) {
        val primary = MaterialTheme.colorScheme.primary
        val onPrimary = MaterialTheme.colorScheme.onPrimary
        M3ButtonDefaults.buttonColors(
            containerColor = primary,
            contentColor = onPrimary,
            disabledContainerColor = primary.copy(alpha = 0.38f),
            disabledContentColor = onPrimary.copy(alpha = 0.38f),
        )
    } else {
        colors
    }

    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "buttonPressScale",
    )

    val baseModifier = if (onLongClick != null) {
        modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick,
            onLongClick = onLongClick,
        )
    } else {
        modifier
    }

    val finalModifier = if (isGlass) {
        baseModifier.graphicsLayer {
            scaleX = pressScale
            scaleY = pressScale
        }
    } else {
        baseModifier
    }

    M3Button(
        onClick = onClick,
        modifier = finalModifier,
        enabled = enabled,
        shape = effectiveShape,
        colors = effectiveColors,
        elevation = if (isGlass) null else elevation,
        border = if (isGlass) null else border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun ElevatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: M3ButtonColors = M3ButtonDefaults.elevatedButtonColors(),
    elevation: M3ButtonElevation? = M3ButtonDefaults.elevatedButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = M3ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == CircleShape) Capsule() else shape
    val isDark = isSystemInDarkTheme()
    val effectiveColors = if (isGlass) {
        M3ButtonDefaults.elevatedButtonColors(
            containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
            contentColor = if (isDark) Color.White else Color(0xFF1C1C1E),
        )
    } else {
        colors
    }

    M3ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = effectiveShape,
        colors = effectiveColors,
        elevation = if (isGlass) null else elevation,
        border = if (isGlass) null else border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun FilledTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: M3ButtonColors = M3ButtonDefaults.filledTonalButtonColors(),
    elevation: M3ButtonElevation? = M3ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = M3ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == CircleShape) Capsule() else shape
    val effectiveColors = if (isGlass) {
        val primary = MaterialTheme.colorScheme.primary
        M3ButtonDefaults.filledTonalButtonColors(
            containerColor = primary.copy(alpha = 0.16f),
            contentColor = primary,
            disabledContainerColor = primary.copy(alpha = 0.08f),
            disabledContentColor = primary.copy(alpha = 0.38f),
        )
    } else {
        colors
    }

    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tonalButtonPressScale",
    )

    M3FilledTonalButton(
        onClick = onClick,
        modifier = if (isGlass) {
            modifier.graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
        } else {
            modifier
        },
        enabled = enabled,
        shape = effectiveShape,
        colors = effectiveColors,
        elevation = if (isGlass) null else elevation,
        border = if (isGlass) null else border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: M3ButtonColors = M3ButtonDefaults.outlinedButtonColors(),
    elevation: M3ButtonElevation? = null,
    border: BorderStroke? = M3ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = M3ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == CircleShape) Capsule() else shape
    val primary = MaterialTheme.colorScheme.primary
    val effectiveColors = if (isGlass) {
        M3ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = primary,
            disabledContentColor = primary.copy(alpha = 0.38f),
        )
    } else {
        colors
    }
    val effectiveBorder = if (isGlass) {
        BorderStroke(1.dp, primary.copy(alpha = 0.40f))
    } else {
        border
    }

    M3OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = effectiveShape,
        colors = effectiveColors,
        elevation = if (isGlass) null else elevation,
        border = effectiveBorder,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: M3ButtonColors = M3ButtonDefaults.textButtonColors(),
    elevation: M3ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = M3ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveColors = if (isGlass) {
        val primary = MaterialTheme.colorScheme.primary
        M3ButtonDefaults.textButtonColors(
            contentColor = primary,
            disabledContentColor = primary.copy(alpha = 0.38f),
        )
    } else {
        colors
    }

    val baseModifier = if (onLongClick != null) {
        modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick,
            onLongClick = onLongClick,
        )
    } else {
        modifier
    }

    M3TextButton(
        onClick = onClick,
        modifier = baseModifier,
        enabled = enabled,
        shape = shape,
        colors = effectiveColors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    M3IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun FilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    M3FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun FilledTonalIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    M3FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.shape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == FloatingActionButtonDefaults.shape) CircleShape else shape
    M3FloatingActionButton(
        onClick = onClick,
        modifier = modifier.liquidGlassSurface(
            shape = effectiveShape,
            interactionSource = interactionSource,
            blurRadius = 16.dp,
            refractionHeight = 20.dp,
            refractionAmount = 32.dp,
            tintColor = containerColor,
        ),
        shape = effectiveShape,
        containerColor = if (isGlass) Color.Transparent else containerColor,
        contentColor = contentColor,
        elevation = if (isGlass) FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp) else elevation,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun SmallFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.smallShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == FloatingActionButtonDefaults.smallShape) CircleShape else shape
    M3SmallFloatingActionButton(
        onClick = onClick,
        modifier = modifier.liquidGlassSurface(
            shape = effectiveShape,
            interactionSource = interactionSource,
            blurRadius = 14.dp,
            refractionHeight = 16.dp,
            refractionAmount = 26.dp,
            tintColor = containerColor,
        ),
        shape = effectiveShape,
        containerColor = if (isGlass) Color.Transparent else containerColor,
        contentColor = contentColor,
        elevation = if (isGlass) FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp) else elevation,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun LargeFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.largeShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == FloatingActionButtonDefaults.largeShape) CircleShape else shape
    M3LargeFloatingActionButton(
        onClick = onClick,
        modifier = modifier.liquidGlassSurface(
            shape = effectiveShape,
            interactionSource = interactionSource,
            blurRadius = 18.dp,
            refractionHeight = 24.dp,
            refractionAmount = 38.dp,
            tintColor = containerColor,
        ),
        shape = effectiveShape,
        containerColor = if (isGlass) Color.Transparent else containerColor,
        contentColor = contentColor,
        elevation = if (isGlass) FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp) else elevation,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun ExtendedFloatingActionButton(
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    shape: Shape = CircleShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass) CircleShape else shape
    M3ExtendedFloatingActionButton(
        text = text,
        icon = icon,
        onClick = onClick,
        modifier = modifier.liquidGlassSurface(
            shape = effectiveShape,
            interactionSource = interactionSource,
            blurRadius = 16.dp,
            refractionHeight = 22.dp,
            refractionAmount = 34.dp,
            tintColor = containerColor,
        ),
        expanded = expanded,
        shape = effectiveShape,
        containerColor = if (isGlass) Color.Transparent else containerColor,
        contentColor = contentColor,
        elevation = if (isGlass) FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp) else elevation,
        interactionSource = interactionSource,
    )
}

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = CircleShape,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    elevation: SelectableChipElevation? = FilterChipDefaults.filterChipElevation(),
    border: BorderStroke? = FilterChipDefaults.filterChipBorder(enabled, selected),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    if (isGlass) {
        val pillShape = Capsule()
        val isDark = isSystemInDarkTheme()
        val unselectedBg = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
        val selectedBg = MaterialTheme.colorScheme.primary
        val onPrimary = MaterialTheme.colorScheme.onPrimary
        val contentColor = if (selected) onPrimary else (if (isDark) Color(0xFFEBEBF5) else Color(0xFF1C1C1E))

        Box(
            modifier = modifier
                .clip(pillShape)
                .background(if (selected) selectedBg else unselectedBg)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                )
                .padding(horizontal = 14.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                LocalTextStyle provides MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.5.sp,
                    fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal,
                    color = contentColor,
                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    leadingIcon?.invoke()
                    label()
                    trailingIcon?.invoke()
                }
            }
        }
    } else {
        M3FilterChip(
            selected = selected,
            onClick = onClick,
            label = label,
            modifier = modifier,
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
            interactionSource = interactionSource,
        )
    }
}

@Composable
fun AssistChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = CircleShape,
    colors: ChipColors = AssistChipDefaults.assistChipColors(),
    elevation: ChipElevation? = AssistChipDefaults.assistChipElevation(),
    border: BorderStroke? = AssistChipDefaults.assistChipBorder(enabled),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == CircleShape) Capsule() else shape
    val isDark = isSystemInDarkTheme()
    val effectiveColors = if (isGlass) {
        AssistChipDefaults.assistChipColors(
            containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
            labelColor = if (isDark) Color(0xFFEBEBF5) else Color(0xFF1C1C1E),
        )
    } else {
        colors
    }

    M3AssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = effectiveShape,
        colors = effectiveColors,
        elevation = if (isGlass) null else elevation,
        border = if (isGlass) null else border,
        interactionSource = interactionSource,
    )
}

@Composable
fun SuggestionChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = CircleShape,
    colors: ChipColors = SuggestionChipDefaults.suggestionChipColors(),
    elevation: ChipElevation? = SuggestionChipDefaults.suggestionChipElevation(),
    border: BorderStroke? = SuggestionChipDefaults.suggestionChipBorder(enabled),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val effectiveShape = if (isGlass && shape == CircleShape) Capsule() else shape
    val isDark = isSystemInDarkTheme()
    val effectiveColors = if (isGlass) {
        SuggestionChipDefaults.suggestionChipColors(
            containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
            labelColor = if (isDark) Color(0xFFEBEBF5) else Color(0xFF1C1C1E),
        )
    } else {
        colors
    }

    M3SuggestionChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        shape = effectiveShape,
        colors = effectiveColors,
        elevation = if (isGlass) null else elevation,
        border = if (isGlass) null else border,
        interactionSource = interactionSource,
    )
}

object ButtonDefaults {
    @Composable
    fun buttonColors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ALPHA),
    ): M3ButtonColors = M3ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun filledTonalButtonColors(
        containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ALPHA),
    ): M3ButtonColors = M3ButtonDefaults.filledTonalButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun outlinedButtonColors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = MaterialTheme.colorScheme.primary,
        disabledContainerColor: Color = Color.Transparent,
        disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ALPHA),
    ): M3ButtonColors = M3ButtonDefaults.outlinedButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun textButtonColors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = MaterialTheme.colorScheme.primary,
        disabledContainerColor: Color = Color.Transparent,
        disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ALPHA),
    ): M3ButtonColors = M3ButtonDefaults.textButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun elevatedButtonColors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        contentColor: Color = MaterialTheme.colorScheme.primary,
        disabledContainerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ALPHA),
    ): M3ButtonColors = M3ButtonDefaults.elevatedButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun buttonElevation(
        defaultElevation: Dp = 0.dp,
        pressedElevation: Dp = 0.dp,
        focusedElevation: Dp = 0.dp,
        hoveredElevation: Dp = 1.dp,
        disabledElevation: Dp = 0.dp,
    ): ButtonElevation = ButtonElevation(
        defaultElevation = defaultElevation,
        pressedElevation = pressedElevation,
        focusedElevation = focusedElevation,
        hoveredElevation = hoveredElevation,
        disabledElevation = disabledElevation,
    )
}

@Stable
class ButtonElevation internal constructor(
    private val defaultElevation: Dp,
    private val pressedElevation: Dp,
    private val focusedElevation: Dp,
    private val hoveredElevation: Dp,
    private val disabledElevation: Dp,
) {
    @Composable
    internal fun tonalElevation(enabled: Boolean, interactionSource: InteractionSource): State<Dp> {
        return animateElevation(enabled = enabled, interactionSource = interactionSource)
    }

    @Composable
    internal fun shadowElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        return animateElevation(enabled = enabled, interactionSource = interactionSource)
    }

    @Composable
    private fun animateElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> interactions.add(interaction)
                    is HoverInteraction.Exit -> interactions.remove(interaction.enter)
                    is FocusInteraction.Focus -> interactions.add(interaction)
                    is FocusInteraction.Unfocus -> interactions.remove(interaction.focus)
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                }
            }
        }

        val interaction = interactions.lastOrNull()
        val target = if (!enabled) {
            disabledElevation
        } else {
            when (interaction) {
                is PressInteraction.Press -> pressedElevation
                is HoverInteraction.Enter -> hoveredElevation
                is FocusInteraction.Focus -> focusedElevation
                else -> defaultElevation
            }
        }

        val animatable = remember { Animatable(target, Dp.VectorConverter) }

        if (!enabled) {
            LaunchedEffect(target) { animatable.snapTo(target) }
        } else {
            LaunchedEffect(target) {
                val lastInteraction = when (animatable.targetValue) {
                    pressedElevation -> PressInteraction.Press(Offset.Zero)
                    hoveredElevation -> HoverInteraction.Enter()
                    focusedElevation -> FocusInteraction.Focus()
                    else -> null
                }
                animatable.animateElevation(
                    from = lastInteraction,
                    to = interaction,
                    target = target,
                )
            }
        }

        return animatable.asState()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ButtonElevation) return false
        if (defaultElevation != other.defaultElevation) return false
        if (pressedElevation != other.pressedElevation) return false
        if (focusedElevation != other.focusedElevation) return false
        if (hoveredElevation != other.hoveredElevation) return false
        if (disabledElevation != other.disabledElevation) return false
        return true
    }

    override fun hashCode(): Int {
        var result = defaultElevation.hashCode()
        result = 31 * result + pressedElevation.hashCode()
        result = 31 * result + focusedElevation.hashCode()
        result = 31 * result + hoveredElevation.hashCode()
        result = 31 * result + disabledElevation.hashCode()
        return result
    }
}

typealias ButtonColors = M3ButtonColors
