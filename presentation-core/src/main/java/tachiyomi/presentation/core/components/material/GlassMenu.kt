package tachiyomi.presentation.core.components.material

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import expo.modules.glassmenuandroid.GlassMenuAndroidView
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord

/** True when the native rit3zh/expo-glass-custom-menu engine can run (AGSL needs Android 13+). */
val supportsNativeGlassMenu: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

/**
 * Invisible anchor for the native rit3zh/expo-glass-custom-menu engine.
 *
 * Place it exactly over the control that opens the menu (for example the `›` chevron of a settings
 * row or a toolbar `⋯` button), typically as the first child of a `Box` with `Modifier.matchParentSize()`
 * so the real control stays on top and keeps receiving touches.
 *
 * The anchor runs in the library's plain-trigger mode (`glass = false`): nothing is drawn while the
 * menu is closed, and the liquid glass menu unfolds out of the anchor's bounds when [expanded] is true.
 *
 * Does nothing below Android 13; callers should fall back to [CupertinoFrostedPopup] there.
 */
@Composable
fun GlassMenuAnchor(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<GlassMenuItemRecord>,
    modifier: Modifier = Modifier,
    alignment: String = "auto",
) {
    if (!supportsNativeGlassMenu) return

    val isDark = isSystemInDarkTheme()
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    var anchorView by remember { mutableStateOf<GlassMenuAndroidView?>(null) }

    AndroidView(
        modifier = modifier.defaultMinSize(minWidth = 18.dp, minHeight = 18.dp),
        factory = { context ->
            GlassMenuAndroidView(context).apply {
                glass = false
                this.alignment = alignment
                colorScheme = if (isDark) "dark" else "light"
                this.items = items
                onMenuOpenChange = { map ->
                    if (map["open"] == false) currentOnDismissRequest()
                }
                anchorView = this
            }
        },
        update = { view ->
            view.glass = false
            view.alignment = alignment
            view.colorScheme = if (isDark) "dark" else "light"
            if (view.items != items) view.items = items
            anchorView = view
        },
    )

    LaunchedEffect(expanded, anchorView) {
        val anchor = anchorView ?: return@LaunchedEffect
        if (expanded) {
            anchor.post {
                // If the engine cannot open (not attached / not measured yet), reset the caller's
                // state so the next tap tries again instead of leaving the menu stuck "open".
                if (!anchor.open()) currentOnDismissRequest()
            }
        } else {
            anchor.close()
        }
    }

    DisposableEffect(Unit) {
        onDispose { anchorView?.close() }
    }
}
