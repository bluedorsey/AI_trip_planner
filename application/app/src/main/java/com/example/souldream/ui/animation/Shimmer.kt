package com.example.souldream.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.souldream.ui.theme.ShimmerBase
import com.example.souldream.ui.theme.ShimmerHighlight

/** Progress (0..1) of the sweep shared by every skeleton below a [ProvideShimmer]. */
private val LocalShimmerProgress = compositionLocalOf<State<Float>?> { null }

/**
 * Runs ONE shimmer clock for everything inside [content], so all skeletons on a screen
 * are lit by a single highlight that travels across the whole window (instead of each
 * block pulsing on its own, out-of-phase clock). The progress is only read while
 * drawing, so it never triggers recomposition.
 */
@Composable
fun ProvideShimmer(
    durationMillis: Int = 1600,
    content: @Composable () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "sharedShimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sharedShimmerProgress",
    )
    CompositionLocalProvider(LocalShimmerProgress provides progress, content = content)
}

/**
 * Paints a placeholder colour with a highlight band sweeping across it.
 * Clip first (`Modifier.clip(shape).shimmer()`) to get a rounded placeholder.
 *
 * Inside [ProvideShimmer] the sweep is window-aligned and shared. Outside it, the
 * element falls back to its own clock, so it still works on its own.
 */
fun Modifier.shimmer(
    baseColor: Color = ShimmerBase,
    highlightColor: Color = ShimmerHighlight,
    durationMillis: Int = 1400,
): Modifier = composed {
    val shared = LocalShimmerProgress.current
    val progress: State<Float> = shared ?: run {
        val transition = rememberInfiniteTransition(label = "shimmer")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "shimmerProgress",
        )
    }
    val screenWidthPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    // Left edge of this element in window coordinates; read only while drawing.
    var windowX by remember { mutableFloatStateOf(0f) }

    this
        .onGloballyPositioned { windowX = it.positionInWindow().x }
        .drawBehind {
            val bandWidth = screenWidthPx * 0.6f
            // The band travels from fully off the left edge to fully off the right edge.
            val bandStart = -bandWidth + progress.value * (screenWidthPx + bandWidth)
            val startX = bandStart - windowX
            drawRect(baseColor)
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        highlightColor.copy(alpha = 0f),
                        highlightColor,
                        highlightColor.copy(alpha = 0f),
                    ),
                    start = Offset(startX, 0f),
                    end = Offset(startX + bandWidth, size.height * 0.3f),
                ),
            )
        }
}
