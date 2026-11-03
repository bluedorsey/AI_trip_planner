package com.example.souldream.ui.screens.tripplan.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.souldream.R
import com.example.souldream.model.TripPlan
import com.example.souldream.ui.components.ShimmerText
import com.example.souldream.ui.theme.DividerSoft
import com.example.souldream.ui.theme.GlassWhite

/** Budget / duration / rating summary. [plan] == null shows shimmering values. */
@Composable
fun TripStatsCard(
    plan: TripPlan?,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, shape, ambientColor = DividerSoft, spotColor = DividerSoft)
            .clip(shape)
            .background(GlassWhite)
            .drawBehind {
                // Equal-weight columns, so the dividers sit at 1/3 and 2/3 of the width.
                val inset = 18.dp.toPx()
                for (i in 1..2) {
                    val x = size.width * i / 3f
                    drawLine(
                        color = DividerSoft,
                        start = Offset(x, inset),
                        end = Offset(x, size.height - inset),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
            }
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatItem(Icons.Rounded.Flight, plan?.budget, R.string.trip_stat_budget, Modifier.weight(1f))
        StatItem(Icons.Rounded.Schedule, plan?.duration, R.string.trip_stat_duration, Modifier.weight(1f))
        StatItem(Icons.Rounded.Star, plan?.rating, R.string.trip_stat_rating, Modifier.weight(1f))
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String?,
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp),
        )
        ShimmerText(
            text = value,
            placeholderWidth = 56.dp,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
        )
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}
