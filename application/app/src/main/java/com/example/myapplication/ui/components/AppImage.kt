package com.example.myapplication.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.myapplication.ui.theme.ShimmerBase

private enum class ImageStatus { Loading, Success, Error }

/**
 * The single place images are loaded. [model] is anything Coil accepts (URL string,
 * Uri, @DrawableRes Int, ...). While [model] is null or still loading, a shimmer
 * placeholder is shown, which cross-fades into the image when it is ready.
 *
 * Clip it from the call site: `AppImage(..., Modifier.clip(shape))`.
 */
@Composable
fun AppImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    showPlaceholder: Boolean = true,
) {
    var status by remember(model) { mutableStateOf(ImageStatus.Loading) }
    val imageAlpha by animateFloatAsState(
        targetValue = if (status == ImageStatus.Success) 1f else 0f,
        animationSpec = tween(450),
        label = "imageAlpha",
    )

    Box(modifier) {
        if (showPlaceholder && imageAlpha < 1f) {
            if (status == ImageStatus.Error) {
                Box(
                    modifier = Modifier.fillMaxSize().background(ShimmerBase),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BrokenImage,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.25f),
                        modifier = Modifier.size(24.dp),
                    )
                }
            } else {
                ShimmerBox(Modifier.fillMaxSize(), shape = RectangleShape)
            }
        }
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = contentDescription,
                contentScale = contentScale,
                onState = { state ->
                    status = when (state) {
                        is AsyncImagePainter.State.Success -> ImageStatus.Success
                        is AsyncImagePainter.State.Error -> ImageStatus.Error
                        else -> ImageStatus.Loading
                    }
                },
                modifier = Modifier.fillMaxSize().graphicsLayer { alpha = imageAlpha },
            )
        }
    }
}
