package eu.kanade.presentation.theme.colorscheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Apple Cupertino / iOS 26 Color Scheme
 * Inspired by Apple Human Interface Guidelines and iOS system colors.
 */
internal object CupertinoColorScheme : BaseColorScheme() {

    override val darkScheme = darkColorScheme(
        primary = Color(0xFF0A84FF),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF0040A8),
        onPrimaryContainer = Color(0xFFD0E4FF),
        inversePrimary = Color(0xFF007AFF),
        secondary = Color(0xFF5E5CE6),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF2E2C8A),
        onSecondaryContainer = Color(0xFFDFDFFF),
        tertiary = Color(0xFFBF5AF2),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF5E1B7F),
        onTertiaryContainer = Color(0xFFF6D8FF),
        background = Color(0xFF000000),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF1C1C1E),
        onSurface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFF2C2C2E),
        onSurfaceVariant = Color(0xFFEBEBF5),
        surfaceTint = Color(0xFF0A84FF),
        inverseSurface = Color(0xFFF2F2F7),
        inverseOnSurface = Color(0xFF1C1C1E),
        outline = Color(0xFF38383A),
        outlineVariant = Color(0xFF2C2C2E),
    )

    override val lightScheme = lightColorScheme(
        primary = Color(0xFF007AFF),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE5F1FF),
        onPrimaryContainer = Color(0xFF0040A8),
        inversePrimary = Color(0xFF0A84FF),
        secondary = Color(0xFF5856D6),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFEBEBFC),
        onSecondaryContainer = Color(0xFF2E2C8A),
        tertiary = Color(0xFFAF52DE),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFF7ECFC),
        onTertiaryContainer = Color(0xFF5E1B7F),
        background = Color(0xFFF2F2F7),
        onBackground = Color(0xFF000000),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF000000),
        surfaceVariant = Color(0xFFE5E5EA),
        onSurfaceVariant = Color(0xFF3C3C43),
        surfaceTint = Color(0xFF007AFF),
        inverseSurface = Color(0xFF1C1C1E),
        inverseOnSurface = Color(0xFFFFFFFF),
        outline = Color(0xFFC7C7CC),
        outlineVariant = Color(0xFFE5E5EA),
    )
}
