package com.example.myapplication.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.ShimmerBase
import com.example.myapplication.ui.theme.ShimmerHighlight

/**
 * Paints a placeholder colour with a highlight band sweeping across it.
 * Clip first (`Modifier.clip(shape).shimmer()`) to get a rounded placeholder.
 */
fun Modifier.shimmer(
    baseColor: Color = ShimmerBase,
    highlightColor: Color = ShimmerHighlight,
    durationMillis: Int = 1400,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )
    drawBehind {
        val width = size.width.coerceAtLeast(1f)
        val startX = -width + progress * 2f * width
        drawRect(baseColor)
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    highlightColor.copy(alpha = 0f),
                    highlightColor,
                    highlightColor.copy(alpha = 0f),
                ),
                start = Offset(startX, 0f),
                end = Offset(startX + width, size.height * 0.35f),
            ),
        )
    }
}
