package tachiyomi.presentation.core.components.material

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Authentic Apple iOS Cupertino Activity Indicator (spinning 8-spoke gear).
 */
@Composable
fun CupertinoActivityIndicator(
    modifier: Modifier = Modifier,
    color: Color = if (isSystemInDarkTheme()) Color(0xFFEBEBF5).copy(alpha = 0.85f) else Color(0xFF3C3C43).copy(alpha = 0.85f),
    radius: Dp = 10.dp,
    spokeCount: Int = 8,
    isSpinning: Boolean = true,
    progress: Float = 1f,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CupertinoSpinnerTransition")
    val step by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = spokeCount.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "CupertinoSpinnerStep",
    )

    val currentStep = if (isSpinning) step.toInt() % spokeCount else -1

    Canvas(
        modifier = modifier.size(radius * 2),
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val spokeWidth = size.width * 0.10f
        val spokeHeight = size.height * 0.28f
        val spokeRadius = CornerRadius(spokeWidth / 2f, spokeWidth / 2f)
        val innerRadius = size.height * 0.20f

        for (i in 0 until spokeCount) {
            val angle = i * (360f / spokeCount)
            val alpha = if (isSpinning) {
                val distance = (i - currentStep + spokeCount) % spokeCount
                1f - (distance.toFloat() / spokeCount.toFloat()) * 0.75f
            } else {
                val threshold = (i.toFloat() + 1f) / spokeCount.toFloat()
                if (progress >= threshold) 0.9f else 0.15f
            }

            rotate(degrees = angle, pivot = center) {
                drawRoundRect(
                    color = color.copy(alpha = alpha.coerceIn(0.12f, 1f)),
                    topLeft = Offset(center.x - spokeWidth / 2f, center.y - innerRadius - spokeHeight),
                    size = Size(spokeWidth, spokeHeight),
                    cornerRadius = spokeRadius,
                )
            }
        }
    }
}
