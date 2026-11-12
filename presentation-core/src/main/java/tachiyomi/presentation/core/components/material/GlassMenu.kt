package tachiyomi.presentation.core.components.material

import android.os.Build
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import expo.modules.glassmenuandroid.GlassMenuAndroidView
import expo.modules.glassmenuandroid.records.GlassMenuConfigRecord
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord
import expo.modules.glassmenuandroid.records.GlassTriggerConfigRecord

/**
 * A Jetpack Compose wrapper for rit3zh/expo-glass-custom-menu's GlassMenuAndroidView.
 * Provides organic 2-pass metaball SDF morphing from the trigger into the liquid glass popup menu,
 * with backdrop capture, jelly physics, and AGSL runtime shaders.
 */
@Composable
fun GlassMenuBox(
    items: List<GlassMenuItemRecord>,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onDismissRequest: () -> Unit = {},
    menuConfig: GlassMenuConfigRecord = remember { GlassMenuConfigRecord() },
    triggerConfig: GlassTriggerConfigRecord = remember { GlassTriggerConfigRecord() },
    content: @Composable () -> Unit,
) {
    val isDark = isSystemInDarkTheme()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        var anchorView by remember { mutableStateOf<GlassMenuAndroidView?>(null) }

        AndroidView(
            modifier = modifier,
            factory = { context ->
                GlassMenuAndroidView(context).apply {
                    this.config = menuConfig
                    this.triggerConfig = triggerConfig
                    this.colorScheme = if (isDark) "dark" else "light"
                    this.items = items
                    this.onMenuOpenChange = { map ->
                        if (map["open"] == false) {
                            onDismissRequest()
                        }
                    }
                    val composeView = ComposeView(context).apply {
                        setContent {
                            content()
                        }
                    }
                    addView(
                        composeView,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        ),
                    )
                    anchorView = this
                }
            },
            update = { view ->
                view.config = menuConfig
                view.triggerConfig = triggerConfig
                view.colorScheme = if (isDark) "dark" else "light"
                view.items = items
                anchorView = view
            },
        )

        LaunchedEffect(expanded, anchorView) {
            val anchor = anchorView ?: return@LaunchedEffect
            if (expanded) {
                anchor.post { anchor.open() }
            } else {
                anchor.close()
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                anchorView?.close()
            }
        }
    } else {
        Box(modifier = modifier) {
            content()
        }
    }
}
