package com.example.souldream.ui.screens.home.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.souldream.R
import com.example.souldream.model.RecentPlan
import com.example.souldream.ui.animation.pressScale
import com.example.souldream.ui.components.AppImage
import com.example.souldream.ui.components.ShimmerBox
import com.example.souldream.ui.theme.GlassWhite
import com.example.souldream.ui.theme.radio_button_colour

private val CardShape = RoundedCornerShape(28.dp)
private val ThumbShape = RoundedCornerShape(20.dp)
private val ThumbSize = 92.dp

@Composable
fun RecentPlanCard(
    plan: RecentPlan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(onClick, pressedScale = 0.97f)
            .clip(CardShape)
            .background(GlassWhite)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppImage(
            model = plan.imageUrl,
            contentDescription = plan.title,
            modifier = Modifier.size(ThumbSize).clip(ThumbShape),
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconLine(Icons.Rounded.CalendarMonth, plan.dateRange)
            IconLine(Icons.Rounded.LocationOn, plan.location)
        }
        Spacer(Modifier.width(8.dp))
        Row(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(radio_button_colour),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = stringResource(R.string.cd_open_plan),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun IconLine(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun RecentPlanCardSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(GlassWhite)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBox(Modifier.size(ThumbSize), ThumbShape)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ShimmerBox(Modifier.fillMaxWidth(0.6f).height(18.dp), CircleShape)
            ShimmerBox(Modifier.fillMaxWidth(0.8f).height(14.dp), CircleShape)
            ShimmerBox(Modifier.fillMaxWidth(0.5f).height(14.dp), CircleShape)
        }
        Spacer(Modifier.width(8.dp))
        ShimmerBox(Modifier.size(40.dp), CircleShape)
    }
}
