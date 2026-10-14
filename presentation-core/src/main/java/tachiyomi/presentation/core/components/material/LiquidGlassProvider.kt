package tachiyomi.presentation.core.components.material

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Immutable
data class LiquidGlassConfig(
    val enabled: Boolean = true,
    val chromaticAberration: Boolean = true,
    val refractionMultiplier: Float = 1.0f,
)

val LocalLiquidGlassConfig = staticCompositionLocalOf { LiquidGlassConfig() }

/**
 * Holds the active Backdrop ONLY when the current subtree is outside (or layered above)
 * the node recording the LayerBackdrop. When null, liquidGlassSurface uses the standalone
 * specular glass shader so it never triggers recursive self-draw crashes.
 */
val LocalLayerBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/**
 * Universal Modifier that transforms ANY Composable (Button, IconButton, FAB, Chip, NavBar, AppBar)
 * into a refractive, spring-animated Liquid Glass element.
 */
fun Modifier.liquidGlassSurface(
    shape: Shape = CircleShape,
    interactionSource: InteractionSource? = null,
    blurRadius: Dp = 12.dp,
    refractionHeight: Dp = 16.dp,
    refractionAmount: Dp = 24.dp,
    tintColor: Color = Color.Unspecified,
    exportedBackdrop: LayerBackdrop? = null,
): Modifier = composed {
    val config = LocalLiquidGlassConfig.current
    if (!config.enabled) return@composed this

    val activeBackdrop = LocalLayerBackdrop.current
    val isDark = isSystemInDarkTheme()
    val isPressed by interactionSource?.collectIsPressedAsState() ?: remember { mutableStateOf(false) }

    // iOS 26-style Liquid Glass spring physics on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "LiquidGlassScale",
    )

    val dynamicRefractionAmount by animateFloatAsState(
        targetValue = if (isPressed) refractionAmount.value * 1.45f else refractionAmount.value,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "LiquidGlassRefraction",
    )

    val baseTint = if (tintColor != Color.Unspecified && tintColor != Color.Transparent) {
        tintColor.copy(alpha = if (isDark) 0.20f else 0.25f)
    } else {
        if (isDark) Color.White.copy(alpha = 0.11f) else Color.White.copy(alpha = 0.32f)
    }

    val rimBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(
                Color.White.copy(alpha = if (isPressed) 0.78f else 0.52f),
                Color.White.copy(alpha = 0.10f),
                Color.White.copy(alpha = 0.30f),
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.90f),
                Color.White.copy(alpha = 0.25f),
                Color.Black.copy(alpha = 0.10f),
            )
        },
    )

    val backdropModifier = if (activeBackdrop != null) {
        // Full GPU Backdrop Refraction + Blur + Vibrancy + Chromatic Aberration
        Modifier.drawBackdrop(
            backdrop = activeBackdrop,
            shape = { shape },
            exportedBackdrop = exportedBackdrop,
            effects = {
                vibrancy()
                blur(blurRadius.toPx())
                lens(
                    refractionHeight = (refractionHeight.toPx() * config.refractionMultiplier),
                    refractionAmount = (dynamicRefractionAmount.dp.toPx() * config.refractionMultiplier),
                    depthEffect = true,
                    chromaticAberration = config.chromaticAberration,
                )
            },
            onDrawSurface = {
                drawRect(baseTint)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isPressed) 0.32f else 0.18f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.06f),
                        ),
                    ),
                )
            },
        )
    } else {
        // Standalone Specular Liquid Glass Lens (safe inside scrolling lists where backdrop is recording)
        Modifier
            .clip(shape)
            .drawWithCache {
                val specularBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.28f else 0.55f),
                        baseTint,
                        if (config.chromaticAberration) Color(0xFF00E5FF).copy(alpha = 0.08f) else baseTint,
                        if (config.chromaticAberration) Color(0xFFFF2A85).copy(alpha = 0.08f) else baseTint,
                        Color.White.copy(alpha = if (isDark) 0.16f else 0.35f),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                )
                onDrawBehind {
                    drawRect(baseTint)
                    drawRect(specularBrush)
                }
            }
    }

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(backdropModifier)
        .border(
            width = 1.dp,
            brush = rimBrush,
            shape = shape,
        )
}
