package com.example.souldream.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.souldream.R

/*
 * Skeleton primitives. Compose skeletons out of these so every loading state shares the
 * same look, shapes and (inside ProvideShimmer) the same synchronized sweep.
 */

/** A rectangular placeholder, e.g. for an image or a card. */
@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
) = ShimmerBox(modifier, shape)

/**
 * A pill-shaped placeholder for a line of text. Give it a width through [modifier]
 * or a [widthFraction] of its parent.
 */
@Composable
fun SkeletonLine(
    modifier: Modifier = Modifier,
    height: Dp = 14.dp,
    widthFraction: Float? = null,
) {
    val sized = if (widthFraction != null) modifier.fillMaxWidth(widthFraction) else modifier
    ShimmerBox(sized.height(height), CircleShape)
}

/** A circular placeholder, e.g. for an avatar or an icon button. */
@Composable
fun SkeletonCircle(
    size: Dp,
    modifier: Modifier = Modifier,
) = ShimmerBox(modifier.size(size), CircleShape)

/**
 * Collapses a skeleton's many placeholder shapes into a single "Loading" node for
 * screen readers. Apply to the root of each skeleton composable.
 */
@Composable
fun Modifier.skeletonSemantics(): Modifier {
    val description = stringResource(R.string.loading)
    return clearAndSetSemantics { contentDescription = description }
}
