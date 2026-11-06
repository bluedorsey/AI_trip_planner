package com.souldream.ui.screens.tripplan.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.ui.animation.pressScale
import com.souldream.ui.screens.tripplan.TripTab
import com.souldream.ui.theme.GlassWhite
import com.souldream.ui.theme.selected_icon
import com.souldream.ui.theme.selected_slider

private const val CellCount = 3

/** Itinerary | Details | Map, sharing a sliding, springy highlight. */
@Composable
fun TripTabBar(
    selected: TripTab,
    onSelect: (TripTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(30.dp)
    val indicatorPosition by animateFloatAsState(
        targetValue = selected.ordinal.toFloat(),
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
        label = "tabIndicator",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(shape)
            .background(GlassWhite)
            .drawBehind {
                val inset = 5.dp.toPx()
                val cellWidth = size.width / CellCount
                val indicatorHeight = size.height - inset * 2
                drawRoundRect(
                    color = selected_slider,
                    topLeft = Offset(indicatorPosition * cellWidth + inset, inset),
                    size = Size(cellWidth - inset * 2, indicatorHeight),
                    cornerRadius = CornerRadius(indicatorHeight / 2),
                )
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TabCell(
            icon = Icons.Rounded.CalendarMonth,
            label = stringResource(R.string.trip_tab_itinerary),
            selected = selected == TripTab.Itinerary,
            onClick = { onSelect(TripTab.Itinerary) },
        )
        TabCell(
            icon = Icons.Rounded.Info,
            label = stringResource(R.string.trip_tab_details),
            selected = selected == TripTab.Details,
            onClick = { onSelect(TripTab.Details) },
        )
        TabCell(
            icon = Icons.Rounded.LocationOn,
            label = stringResource(R.string.trip_tab_map),
            selected = selected == TripTab.Map,
            onClick = { onSelect(TripTab.Map) },
        )
    }
}

@Composable
private fun RowScope.TabCell(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint by animateColorAsState(
        targetValue = if (selected) selected_icon else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tabTint",
    )
    CellContent(onClick = onClick) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = tint, maxLines = 1)
    }
}

@Composable
private fun RowScope.CellContent(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .pressScale(onClick, pressedScale = 0.94f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
