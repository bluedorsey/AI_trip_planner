package com.example.souldream.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.example.souldream.ui.animation.shimmer

/** Animated placeholder block. Size it from the call site. */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
) {
    Box(modifier.clip(shape).shimmer())
}

/**
 * Text that renders a shimmer bar while [text] is null and cross-fades to the real
 * text once it arrives. The bar reserves the same line height, so nothing jumps.
 */
@Composable
fun ShimmerText(
    text: String?,
    placeholderWidth: Dp,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
) {
    Crossfade(targetState = text, modifier = modifier, label = "shimmerText") { value ->
        if (value == null) {
            val density = LocalDensity.current
            val lineHeight = with(density) {
                (if (style.lineHeight.isSpecified) style.lineHeight else style.fontSize).toDp()
            }
            val fontHeight = with(density) { style.fontSize.toDp() }
            Box(
                modifier = Modifier.height(lineHeight),
                contentAlignment = Alignment.CenterStart,
            ) {
                ShimmerBox(
                    modifier = Modifier.width(placeholderWidth).height(fontHeight * 0.8f),
                    shape = RoundedCornerShape(50),
                )
            }
        } else {
            Text(
                text = value,
                style = style,
                color = color,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
