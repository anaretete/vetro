package tachiyomi.presentation.core.components.material

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

val LocalCupertinoHapticsEnabled = compositionLocalOf { true }

class CupertinoHaptics(
    private val view: View?,
    private val enabled: Boolean,
) {
    fun toggle() {
        if (!enabled || view == null) return
        view.performHapticFeedback(
            HapticFeedbackConstants.CONFIRM,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
        )
    }

    fun tick() {
        if (!enabled || view == null) return
        view.performHapticFeedback(
            HapticFeedbackConstants.CLOCK_TICK,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
        )
    }

    fun click() {
        if (!enabled || view == null) return
        view.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
        )
    }

    fun selection() {
        if (!enabled || view == null) return
        view.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
        )
    }
}

@Composable
fun rememberCupertinoHaptics(): CupertinoHaptics {
    val view = LocalView.current
    val enabled = LocalCupertinoHapticsEnabled.current
    return remember(view, enabled) {
        CupertinoHaptics(view, enabled)
    }
}
