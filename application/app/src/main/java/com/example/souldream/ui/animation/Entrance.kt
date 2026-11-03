package com.example.souldream.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Drives a 0f -> 1f entrance animation, delayed by [index] * [stepDelayMillis].
 *
 * The "already played" flag is saveable, so lazy-list items that scroll away and come
 * back (or survive rotation) do not replay their entrance.
 */
@Composable
fun rememberEntranceProgress(
    index: Int = 0,
    stepDelayMillis: Int = 70,
    durationMillis: Int = 550,
    maxDelayMillis: Int = 450,
): State<Float> {
    var played by rememberSaveable { mutableStateOf(false) }
    val progress = remember { Animatable(if (played) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!played) {
            delay((index * stepDelayMillis).coerceAtMost(maxDelayMillis).toLong())
            progress.animateTo(1f, tween(durationMillis, easing = FastOutSlowInEasing))
            played = true
        }
    }
    return progress.asState()
}

/** Fade + slide-up entrance, staggered by [index]. */
fun Modifier.staggeredEntrance(
    index: Int = 0,
    offsetY: Dp = 28.dp,
    stepDelayMillis: Int = 70,
    durationMillis: Int = 550,
): Modifier = composed {
    val progress by rememberEntranceProgress(index, stepDelayMillis, durationMillis)
    graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * offsetY.toPx()
    }
}
