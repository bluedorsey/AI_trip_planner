package com.souldream.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.souldream.ui.theme.SkyMid
import com.souldream.ui.theme.SkyTop
import com.souldream.ui.theme.background_colour

/**
 * Full-width hero that sits behind a scrolling list. It always renders the sky
 * gradient, so the screen looks intentional before (or without) a hero image, then
 * fades the image in over it. It parallaxes and fades as the list scrolls.
 *
 * [scrollOffsetProvider] is read inside a graphics layer, so scrolling never recomposes.
 */
@Composable
fun HeroBackdrop(
    imageModel: Any?,
    height: Dp,
    scrollOffsetProvider: () -> Float,
    modifier: Modifier = Modifier,
    fadeColor: Color = background_colour,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                val scroll = scrollOffsetProvider().coerceIn(0f, size.height)
                translationY = -scroll * 0.4f
                alpha = 1f - scroll / size.height
            },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(SkyTop, SkyMid, fadeColor))),
        )
        if (imageModel != null) {
            AppImage(
                model = imageModel,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                showPlaceholder = false,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.55f to fadeColor.copy(alpha = 0f),
                        1f to fadeColor,
                    ),
                ),
        )
    }
}

/** Scroll distance of a list whose first item is the hero, or "far away" once past it. */
fun LazyListState.heroScrollOffset(): Float =
    if (firstVisibleItemIndex == 0) firstVisibleItemScrollOffset.toFloat() else Float.MAX_VALUE
