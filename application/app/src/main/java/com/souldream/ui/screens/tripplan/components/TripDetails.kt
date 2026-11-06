package com.souldream.ui.screens.tripplan.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.WbSunny
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
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.model.TripPlan
import com.souldream.ui.components.SectionMessage
import com.souldream.ui.components.SkeletonBlock
import com.souldream.ui.components.skeletonSemantics
import com.souldream.ui.theme.GlassWhite
import com.souldream.ui.theme.selected_icon

private val CardShape = RoundedCornerShape(28.dp)

/** Costs, weather and transport. Each card shows only if the plan has that information. */
@Composable
fun TripDetails(
    plan: TripPlan,
    modifier: Modifier = Modifier,
) {
    val hasCosts = plan.costBreakdown.isNotEmpty() || plan.perDayBudget.isNotBlank()
    if (!hasCosts && plan.weather.isBlank() && plan.transport.isBlank()) {
        // Trips saved before this tab existed.
        SectionMessage(stringResource(R.string.trip_empty_details), modifier)
        return
    }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (hasCosts) {
            DetailCard(Icons.Rounded.Payments, R.string.trip_details_costs) {
                plan.costBreakdown.forEach { CostRow(it.label, it.amount) }
                if (plan.perDayBudget.isNotBlank()) {
                    CostRow(stringResource(R.string.trip_details_per_day), plan.perDayBudget, emphasized = true)
                }
            }
        }
        if (plan.weather.isNotBlank()) {
            DetailCard(Icons.Rounded.WbSunny, R.string.trip_details_weather) { BodyText(plan.weather) }
        }
        if (plan.transport.isNotBlank()) {
            DetailCard(Icons.Rounded.DirectionsBus, R.string.trip_details_transport) { BodyText(plan.transport) }
        }
    }
}

@Composable
fun TripDetailsSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().skeletonSemantics(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(3) { SkeletonBlock(Modifier.fillMaxWidth().height(120.dp), CardShape) }
    }
}

@Composable
private fun DetailCard(
    icon: ImageVector,
    @StringRes titleRes: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(GlassWhite)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = selected_icon, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        content()
    }
}

@Composable
private fun CostRow(label: String, amount: String, emphasized: Boolean = false) {
    val weight = if (emphasized) FontWeight.Bold else FontWeight.Normal
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = weight,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
