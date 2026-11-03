package com.souldream.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.souldream.model.Destination
import com.souldream.ui.animation.pressScale
import com.souldream.ui.components.AppImage
import com.souldream.ui.components.SkeletonBlock
import com.souldream.ui.components.skeletonSemantics

private val CardWidth = 128.dp
private val CardHeight = 138.dp
private val CardShape = RoundedCornerShape(26.dp)

/** Image card with a bottom scrim, pin, name and tagline. */
@Composable
fun DestinationCard(
    destination: Destination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .pressScale(onClick)
            .size(CardWidth, CardHeight)
            .clip(CardShape),
    ) {
        AppImage(
            model = destination.imageUrl,
            contentDescription = destination.name,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.35f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.62f),
                    ),
                ),
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = destination.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = destination.tagline,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun DestinationCardSkeleton(modifier: Modifier = Modifier) {
    SkeletonBlock(modifier.size(CardWidth, CardHeight).skeletonSemantics(), CardShape)
}
