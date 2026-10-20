package tachiyomi.presentation.core.components.material

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.catalog.utils.InteractiveHighlight
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangularShape

@Immutable
data class LiquidGlassConfig(
    val enabled: Boolean = true,
    val chromaticAberration: Boolean = true,
    val refractionMultiplier: Float = 1.0f,
    val blurMultiplier: Float = 1.0f,
    val lensHeightMultiplier: Float = 1.0f,
    val tintAlphaMultiplier: Float = 1.0f,
    val springPhysicsEnabled: Boolean = true,
    val depthEffect: Boolean = true,
)

val LocalLiquidGlassConfig = compositionLocalOf { LiquidGlassConfig() }

/**
 * Holds the active Backdrop instance. If null, components sample an ambient canvas backdrop
 * with real AGSL refraction and chromatic aberration so shaders ALWAYS run.
 */
val LocalLayerBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/**
 * Indicates whether a component is nested inside an existing Liquid Glass bar or capsule.
 * Child icon buttons use this to avoid drawing redundant, high-contrast outer lens rims.
 */
val LocalInGlassBar = staticCompositionLocalOf { false }

fun sanitizeLensShape(shape: Shape): Shape {
    return if (shape is CornerBasedShape || shape is RoundedRectangularShape) {
        shape
    } else {
        Capsule()
    }
}

/**
 * Universal Modifier that transforms ANY Composable (Button, IconButton, FAB, Chip, NavBar, AppBar)
 * into a refractive, spring-animated Kyant Liquid Glass lens with AGSL shaders.
 */
fun Modifier.liquidGlassSurface(
    shape: Shape = Capsule(),
    interactionSource: InteractionSource? = null,
    blurRadius: Dp = 10.dp,
    refractionHeight: Dp = 14.dp,
    refractionAmount: Dp = 22.dp,
    tintColor: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    exportedBackdrop: LayerBackdrop? = null,
    isInteractive: Boolean = false,
): Modifier = composed {
    val config = LocalLiquidGlassConfig.current
    if (!config.enabled) return@composed this

    val isDark = isSystemInDarkTheme()
    val isPressed by (interactionSource?.collectIsPressedAsState() ?: remember { mutableStateOf<Boolean>(false) })
    val animationScope = rememberCoroutineScope()

    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(
            animationScope = animationScope,
        )
    }

    // Liquid Glass spring physics on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed && config.springPhysicsEnabled) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "LiquidGlassScale",
    )

    val dynamicRefractionAmount by animateFloatAsState(
        targetValue = if (isPressed) refractionAmount.value * 1.4f else refractionAmount.value,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "LiquidGlassRefraction",
    )

    val safeShape = remember(shape) { sanitizeLensShape(shape) }

    val activeBackdrop = LocalLayerBackdrop.current ?: rememberCanvasBackdrop {
        val baseColor = if (isDark) Color(0xFF16161A) else Color(0xFFF2F2F6)
        drawRect(baseColor)
    }

    val defaultAlpha = if (isDark) 0.22f else 0.28f
    val effectiveTintAlpha = (defaultAlpha * config.tintAlphaMultiplier).coerceIn(0f, 1f)
    val baseTint = if (tintColor.isSpecified && tintColor != Color.Transparent) {
        tintColor.copy(alpha = effectiveTintAlpha)
    } else {
        if (isDark) {
            Color.White.copy(alpha = (0.12f * config.tintAlphaMultiplier).coerceIn(0f, 1f))
        } else {
            Color.White.copy(alpha = (0.35f * config.tintAlphaMultiplier).coerceIn(0f, 1f))
        }
    }

    val backdropModifier = Modifier.drawBackdrop(
        backdrop = activeBackdrop,
        shape = { safeShape },
        exportedBackdrop = exportedBackdrop,
        effects = {
            vibrancy()
            val effectiveBlur = (blurRadius.toPx() * config.blurMultiplier).coerceAtLeast(0f)
            if (effectiveBlur > 0.5f) {
                blur(effectiveBlur)
            }
            try {
                lens(
                    refractionHeight = (refractionHeight.toPx() * config.refractionMultiplier * config.lensHeightMultiplier).coerceAtLeast(0.1f),
                    refractionAmount = (dynamicRefractionAmount.dp.toPx() * config.refractionMultiplier).coerceAtLeast(0.1f),
                    depthEffect = config.depthEffect,
                    chromaticAberration = config.chromaticAberration,
                )
            } catch (_: Throwable) {
            }
        },
        highlight = { Highlight.Default },
        shadow = { Shadow.Default },
        onDrawSurface = {
            if (tintColor.isSpecified && tintColor != Color.Transparent) {
                drawRect(tintColor, blendMode = BlendMode.Hue)
                drawRect(baseTint)
            } else if (surfaceColor.isSpecified) {
                drawRect(surfaceColor)
            } else {
                drawRect(baseTint)
            }
        },
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(backdropModifier)
        .then(
            if (isInteractive) {
                interactiveHighlight.modifier
            } else {
                Modifier
            },
        )
}
