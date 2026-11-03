package com.souldream.ui.screens.tripplan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
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
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.model.TripPlan
import com.souldream.ui.animation.pulse
import com.souldream.ui.animation.staggeredEntrance
import com.souldream.ui.components.ShimmerText
import com.souldream.ui.theme.GlassWhite
import com.souldream.ui.theme.selected_icon

/** Title block. [plan] == null renders shimmer placeholders for every dynamic value. */
@Composable
fun TripHeader(
    plan: TripPlan?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(
            modifier = Modifier.staggeredEntrance(index = 0),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp).pulse(),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.trip_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(6.dp))

        ShimmerText(
            text = plan?.title,
            placeholderWidth = 220.dp,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            modifier = Modifier.staggeredEntrance(index = 1),
        )
        Spacer(Modifier.height(6.dp))

        ShimmerText(
            text = plan?.let { stringResource(R.string.trip_subtitle, it.duration, it.dateRange) },
            placeholderWidth = 200.dp,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.staggeredEntrance(index = 2),
        )
        Spacer(Modifier.height(16.dp))

        LocationPill(
            location = plan?.location,
            modifier = Modifier.staggeredEntrance(index = 3),
        )
    }
}

@Composable
private fun LocationPill(location: String?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(GlassWhite)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = null,
            tint = selected_icon,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        ShimmerText(
            text = location,
            placeholderWidth = 96.dp,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
        )
    }
}
