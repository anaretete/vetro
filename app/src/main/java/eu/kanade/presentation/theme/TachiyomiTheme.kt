package eu.kanade.presentation.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.DynamicMaterialExpressiveTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import eu.kanade.domain.ui.UiPreferences
import tachiyomi.presentation.core.components.material.LiquidGlassConfig
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.util.collectAsState
import eu.kanade.domain.ui.model.AppTheme
import eu.kanade.presentation.theme.colorscheme.BaseColorScheme
import eu.kanade.presentation.theme.colorscheme.AppleAlpineColorScheme
import eu.kanade.presentation.theme.colorscheme.AppleMidnightColorScheme
import eu.kanade.presentation.theme.colorscheme.ApplePurpleColorScheme
import eu.kanade.presentation.theme.colorscheme.AppleTitaniumColorScheme
import eu.kanade.presentation.theme.colorscheme.CatppuccinColorScheme
import eu.kanade.presentation.theme.colorscheme.CupertinoColorScheme
import eu.kanade.presentation.theme.colorscheme.CloudflareColorScheme
import eu.kanade.presentation.theme.colorscheme.CottoncandyColorScheme
import eu.kanade.presentation.theme.colorscheme.CustomColorScheme
import eu.kanade.presentation.theme.colorscheme.DoomColorScheme
import eu.kanade.presentation.theme.colorscheme.GreenAppleColorScheme
import eu.kanade.presentation.theme.colorscheme.LavenderColorScheme
import eu.kanade.presentation.theme.colorscheme.MatrixColorScheme
import eu.kanade.presentation.theme.colorscheme.MidnightDuskColorScheme
import eu.kanade.presentation.theme.colorscheme.MochaColorScheme
import eu.kanade.presentation.theme.colorscheme.MonetColorScheme
import eu.kanade.presentation.theme.colorscheme.MonochromeColorScheme
import eu.kanade.presentation.theme.colorscheme.NordColorScheme
import eu.kanade.presentation.theme.colorscheme.SapphireColorScheme
import eu.kanade.presentation.theme.colorscheme.StrawberryColorScheme
import eu.kanade.presentation.theme.colorscheme.TachiyomiColorScheme
import eu.kanade.presentation.theme.colorscheme.TakoColorScheme
import eu.kanade.presentation.theme.colorscheme.TealTurqoiseColorScheme
import eu.kanade.presentation.theme.colorscheme.TidalWaveColorScheme
import eu.kanade.presentation.theme.colorscheme.YinYangColorScheme
import eu.kanade.presentation.theme.colorscheme.YotsubaColorScheme
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun TachiyomiTheme(
    appTheme: AppTheme? = null,
    amoled: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val uiPreferences = Injekt.get<UiPreferences>()
    BaseTachiyomiTheme(
        appTheme = appTheme ?: uiPreferences.appTheme().get(),
        isAmoled = amoled ?: uiPreferences.themeDarkAmoled().get(),
        content = content,
    )
}

// KMK -->
/** Theme based on Cover */
@Composable
fun TachiyomiTheme(
    seedColor: Color?,
    appTheme: AppTheme? = null,
    amoled: Boolean? = null,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit,
) {
    if (seedColor == null) {
        TachiyomiTheme(appTheme, amoled, content)
    } else {
        val uiPreferences = Injekt.get<UiPreferences>()
        val isAmoled = amoled ?: uiPreferences.themeDarkAmoled().get()
        LiquidGlassThemeWrapper {
            DynamicMaterialExpressiveTheme(
                seedColor = seedColor,
                isAmoled = isAmoled,
                style = uiPreferences.themeCoverBasedStyle().get(),
                typography = typography,
                animate = true,
                content = content,
            )
        }
    }
}
// KMK <--

@Composable
fun TachiyomiPreviewTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit,
) = BaseTachiyomiTheme(appTheme, isAmoled, content)

@Composable
private fun LiquidGlassThemeWrapper(content: @Composable () -> Unit) {
    val uiPreferences = Injekt.get<UiPreferences>()
    val liquidGlassEnabled by uiPreferences.liquidGlassEnabled().collectAsState()
    val chromaticAberration by uiPreferences.liquidGlassChromaticAberration().collectAsState()
    val refractionStrength by uiPreferences.liquidGlassRefractionStrength().collectAsState()
    val blurRadius by uiPreferences.liquidGlassBlurRadius().collectAsState()
    val lensHeight by uiPreferences.liquidGlassLensHeight().collectAsState()
    val tintAlpha by uiPreferences.liquidGlassTintAlpha().collectAsState()
    val springPhysics by uiPreferences.liquidGlassSpringPhysics().collectAsState()
    val depthEffect by uiPreferences.liquidGlassDepthEffect().collectAsState()

    val glassConfig = remember(
        liquidGlassEnabled,
        chromaticAberration,
        refractionStrength,
        blurRadius,
        lensHeight,
        tintAlpha,
        springPhysics,
        depthEffect,
    ) {
        LiquidGlassConfig(
            enabled = liquidGlassEnabled,
            chromaticAberration = chromaticAberration,
            refractionMultiplier = refractionStrength,
            blurMultiplier = blurRadius,
            lensHeightMultiplier = lensHeight,
            tintAlphaMultiplier = tintAlpha,
            springPhysicsEnabled = springPhysics,
            depthEffect = depthEffect,
        )
    }

    CompositionLocalProvider(LocalLiquidGlassConfig provides glassConfig) {
        content()
    }
}

val CupertinoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
private fun BaseTachiyomiTheme(
    appTheme: AppTheme,
    isAmoled: Boolean,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    LiquidGlassThemeWrapper {
        val isCupertino = appTheme in listOf(
            AppTheme.CUPERTINO,
            AppTheme.APPLE_MIDNIGHT,
            AppTheme.APPLE_ALPINE,
            AppTheme.APPLE_PURPLE,
            AppTheme.APPLE_TITANIUM,
        ) || LocalLiquidGlassConfig.current.enabled
        MaterialExpressiveTheme(
            colorScheme = remember(appTheme, isDark, isAmoled) {
                getThemeColorScheme(
                    context = context,
                    appTheme = appTheme,
                    isDark = isDark,
                    isAmoled = isAmoled,
                )
            },
            shapes = if (isCupertino) CupertinoShapes else MaterialTheme.shapes,
            content = content,
        )
    }
}

private fun getThemeColorScheme(
    context: Context,
    appTheme: AppTheme,
    isDark: Boolean,
    isAmoled: Boolean,
): ColorScheme {
    val colorScheme = when (appTheme) {
        AppTheme.MONET -> {
            MonetColorScheme(context)
        }
        // KMK -->
        AppTheme.CUSTOM -> {
            val uiPreferences = Injekt.get<UiPreferences>()
            CustomColorScheme(
                seed = Color(uiPreferences.colorTheme().get()),
                style = uiPreferences.customThemeStyle().get(),
            )
        }
        // KMK <--
        else -> {
            colorSchemes.getOrDefault(appTheme, TachiyomiColorScheme)
        }
    }
    return colorScheme.getColorScheme(
        isDark = isDark,
        isAmoled = isAmoled,
        overrideDarkSurfaceContainers = appTheme != AppTheme.MONET,
    )
}

private val colorSchemes: Map<AppTheme, BaseColorScheme> = mapOf(
    AppTheme.DEFAULT to TachiyomiColorScheme,
    AppTheme.CUPERTINO to CupertinoColorScheme,
    AppTheme.APPLE_MIDNIGHT to AppleMidnightColorScheme,
    AppTheme.APPLE_ALPINE to AppleAlpineColorScheme,
    AppTheme.APPLE_PURPLE to ApplePurpleColorScheme,
    AppTheme.APPLE_TITANIUM to AppleTitaniumColorScheme,
    AppTheme.CATPPUCCIN to CatppuccinColorScheme,
    AppTheme.GREEN_APPLE to GreenAppleColorScheme,
    AppTheme.LAVENDER to LavenderColorScheme,
    AppTheme.MIDNIGHT_DUSK to MidnightDuskColorScheme,
    AppTheme.MONOCHROME to MonochromeColorScheme,
    AppTheme.NORD to NordColorScheme,
    AppTheme.STRAWBERRY_DAIQUIRI to StrawberryColorScheme,
    AppTheme.TAKO to TakoColorScheme,
    AppTheme.TEALTURQUOISE to TealTurqoiseColorScheme,
    AppTheme.TIDAL_WAVE to TidalWaveColorScheme,
    AppTheme.YINYANG to YinYangColorScheme,
    AppTheme.YOTSUBA to YotsubaColorScheme,
    AppTheme.CLOUDFLARE to CloudflareColorScheme,
    AppTheme.COTTONCANDY to CottoncandyColorScheme,
    AppTheme.DOOM to DoomColorScheme,
    AppTheme.MATRIX to MatrixColorScheme,
    AppTheme.MOCHA to MochaColorScheme,
    AppTheme.SAPPHIRE to SapphireColorScheme,
)
