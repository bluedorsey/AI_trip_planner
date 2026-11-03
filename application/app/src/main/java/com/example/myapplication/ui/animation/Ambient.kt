package com.example.myapplication.ui.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Endless gentle scale "breathing". */
fun Modifier.pulse(
    minScale: Float = 0.94f,
    maxScale: Float = 1.08f,
    durationMillis: Int = 1600,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/** Endless gentle vertical bobbing. */
fun Modifier.floating(
    amplitude: Dp = 4.dp,
    durationMillis: Int = 2400,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "floating")
    val offset by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatingOffset",
    )
    graphicsLayer { translationY = offset * amplitude.toPx() }
}
