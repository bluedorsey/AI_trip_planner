package com.example.souldream.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.souldream.ui.animation.pressScale
import com.example.souldream.ui.theme.SaveTripGradientEnd
import com.example.souldream.ui.theme.save_trip_button

/**
 * Full-width dark gradient pill with a light sweep gliding across it.
 * Content is a slot so callers can animate their own label/icon.
 */
@Composable
fun GradientActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = RoundedCornerShape(32.dp)
    val transition = rememberInfiniteTransition(label = "buttonSweep")
    val sweep by transition.animateFloat(
        initialValue = -0.6f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "sweep",
    )
    val buttonAlpha by animateFloatAsState(if (enabled) 1f else 0.55f, label = "buttonAlpha")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = buttonAlpha }
            .pressScale(onClick, enabled = enabled, pressedScale = 0.97f)
            .shadow(16.dp, shape, ambientColor = save_trip_button, spotColor = save_trip_button)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(save_trip_button, SaveTripGradientEnd)))
            .drawWithContent {
                drawContent()
                val band = size.width * 0.35f
                val x = size.width * sweep
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = 0.16f),
                            Color.White.copy(alpha = 0f),
                        ),
                        start = Offset(x - band, 0f),
                        end = Offset(x + band, size.height),
                    ),
                )
            }
            .height(60.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
