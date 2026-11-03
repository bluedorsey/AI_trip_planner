package com.example.souldream.ui.screens.tripplan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.souldream.model.TripActivity
import com.example.souldream.ui.animation.pressScale
import com.example.souldream.ui.components.AppImage
import com.example.souldream.ui.components.SkeletonBlock
import com.example.souldream.ui.components.SkeletonLine
import com.example.souldream.ui.components.skeletonSemantics
import com.example.souldream.ui.components.TagChip
import com.example.souldream.ui.theme.GlassWhite

private val CardShape = RoundedCornerShape(28.dp)
private val ThumbShape = RoundedCornerShape(20.dp)
private val ThumbSize = 98.dp

/** One itinerary entry: image, title, description, location and a type tag. */
@Composable
fun ActivityCard(
    activity: TripActivity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(onClick, pressedScale = 0.97f)
            .clip(CardShape)
            .background(GlassWhite)
            .padding(10.dp),
    ) {
        AppImage(
            model = activity.imageUrl,
            contentDescription = activity.title,
            modifier = Modifier.size(ThumbSize).clip(ThumbShape),
        )
        Spacer(Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(top = 2.dp),
                )
                Spacer(Modifier.width(8.dp))
                TagChip(
                    text = stringResource(activity.type.labelRes),
                    icon = activity.type.icon,
                )
            }
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = activity.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun ActivityCardSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(GlassWhite)
            .padding(10.dp)
            .skeletonSemantics(),
    ) {
        SkeletonBlock(Modifier.size(ThumbSize), ThumbShape)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SkeletonLine(height = 18.dp, widthFraction = 0.5f)
                SkeletonLine(Modifier.width(56.dp), height = 26.dp) // type tag
            }
            SkeletonLine(height = 12.dp, widthFraction = 1f)
            SkeletonLine(height = 12.dp, widthFraction = 0.75f)
            SkeletonLine(height = 12.dp, widthFraction = 0.4f)
        }
    }
}
