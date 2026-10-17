package eu.kanade.presentation.more.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.StringResource
import eu.kanade.presentation.components.AppBar
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun PreferenceScaffold(
    titleRes: StringResource? = null,
    title: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressed: (() -> Unit)? = null,
    itemsProvider: @Composable () -> List<Preference>,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isGlass) {
        if (isDark) Color(0xFF000000) else Color(0xFFF2F2F7)
    } else {
        MaterialTheme.colorScheme.background
    }

    Scaffold(
        topBar = {
            AppBar(
                title = title ?: titleRes?.let { stringResource(it) } ?: "",
                navigateUp = onBackPressed,
                actions = actions,
                scrollBehavior = it,
            )
        },
        containerColor = containerColor,
        content = { contentPadding ->
            PreferenceScreen(
                items = itemsProvider(),
                contentPadding = contentPadding,
            )
        },
    )
}
