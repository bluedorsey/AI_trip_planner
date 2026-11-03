package com.example.souldream.ui.screens.tripplan.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CalendarMonth
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
import com.example.souldream.R
import com.example.souldream.ui.animation.pressScale
import com.example.souldream.ui.screens.tripplan.TripTab
import com.example.souldream.ui.theme.GlassWhite
import com.example.souldream.ui.theme.selected_icon
import com.example.souldream.ui.theme.selected_slider

private const val CellCount = 3

/**
 * Itinerary | Map | Save. The first two are tabs sharing a sliding, springy
 * highlight; "Save" is a toggle whose bookmark pops when it flips.
 */
@Composable
fun TripTabBar(
    selected: TripTab,
    onSelect: (TripTab) -> Unit,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
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
            icon = Icons.Rounded.LocationOn,
            label = stringResource(R.string.trip_tab_map),
            selected = selected == TripTab.Map,
            onClick = { onSelect(TripTab.Map) },
        )
        SaveCell(isSaved = isSaved, onClick = onToggleSave)
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
private fun RowScope.SaveCell(isSaved: Boolean, onClick: () -> Unit) {
    val tint by animateColorAsState(
        targetValue = if (isSaved) selected_icon else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "saveTint",
    )
    CellContent(onClick = onClick) {
        AnimatedContent(
            targetState = isSaved,
            transitionSpec = {
                (scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn()) togetherWith
                    (scaleOut() + fadeOut())
            },
            label = "bookmark",
        ) { saved ->
            Icon(
                imageVector = if (saved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(if (isSaved) R.string.trip_tab_saved else R.string.trip_tab_save),
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            maxLines = 1,
        )
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
