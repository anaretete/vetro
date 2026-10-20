package tachiyomi.presentation.core.components.material

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.Capsule
import com.kyant.backdrop.catalog.components.LiquidButton as KyantLiquidButton

@Composable
fun LiquidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = LocalLayerBackdrop.current ?: rememberLayerBackdrop(),
    enabled: Boolean = true,
    isInteractive: Boolean = true,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    shape: Shape = Capsule(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit,
) {
    KyantLiquidButton(
        onClick = onClick,
        modifier = modifier,
        backdrop = backdrop,
        enabled = enabled,
        isInteractive = isInteractive,
        tint = tint,
        surfaceColor = surfaceColor,
        contentColor = contentColor,
        shape = shape,
        contentPadding = contentPadding,
        content = content,
    )
}
