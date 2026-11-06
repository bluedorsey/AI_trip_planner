package com.souldream.ui.screens.tripplan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.model.TripActivity
import com.souldream.model.TripDay
import com.souldream.ui.animation.rememberEntranceProgress
import com.souldream.ui.animation.staggeredEntrance
import com.souldream.ui.components.SkeletonLine
import com.souldream.ui.components.skeletonSemantics
import com.souldream.ui.theme.DividerSoft
import com.souldream.ui.theme.button_colour
import com.souldream.ui.theme.selected_slider

private val GutterWidth = 28.dp
private val DotCenterX = 10.dp
private val DotCenterY = 14.dp
private val DotRadius = 7.dp

/**
 * A day on the vertical timeline: a dot, a line that draws itself down to the next
 * day, then the header and the day's activity cards.
 */
@Composable
fun DayTimelineItem(
    day: TripDay,
    isLast: Boolean,
    onActivityClick: (TripActivity) -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
) {
    val progress by rememberEntranceProgress(index = index, durationMillis = 700)

    Row(modifier = modifier.timelineGutter(progress = { progress }, drawLine = !isLast)) {
        Spacer(Modifier.width(GutterWidth))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 24.dp)
                .graphicsLayer {
                    alpha = progress
                    translationY = (1f - progress) * 24.dp.toPx()
                },
        ) {
            // The model often writes "Day 1" as the date too, which showed as "Day 1 • Day 1".
            val hasDate = day.date.isNotBlank() && !day.date.startsWith("Day", ignoreCase = true)
            Text(
                text = if (hasDate) stringResource(R.string.trip_day_header, day.dayNumber, day.date)
                else stringResource(R.string.trip_day_plain, day.dayNumber),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = day.theme,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                day.activities.forEachIndexed { i, activity ->
                    ActivityCard(
                        activity = activity,
                        onClick = { onActivityClick(activity) },
                        modifier = Modifier.staggeredEntrance(index = index + i + 1),
                    )
                }
            }
        }
    }
}

@Composable
fun DayTimelineItemSkeleton(isLast: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .timelineGutter(progress = { 1f }, drawLine = !isLast)
            .skeletonSemantics(),
    ) {
        Spacer(Modifier.width(GutterWidth))
        Column(
            modifier = Modifier.weight(1f).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SkeletonLine(height = 20.dp, widthFraction = 0.5f)
            SkeletonLine(height = 14.dp, widthFraction = 0.35f)
            Spacer(Modifier.height(4.dp))
            ActivityCardSkeleton()
        }
    }
}

/**
 * Draws the dot and (optionally) the connecting line behind the row.
 * [progress] is read at draw time, so animating it never recomposes.
 */
private fun Modifier.timelineGutter(progress: () -> Float, drawLine: Boolean): Modifier =
    drawBehind {
        val p = progress()
        val cx = DotCenterX.toPx()
        val cy = DotCenterY.toPx()
        val radius = DotRadius.toPx()
        // Dot pops in during the first third of the animation.
        val dotScale = (p * 3f).coerceIn(0f, 1f)
        drawCircle(selected_slider, radius = radius * 1.9f * dotScale, center = Offset(cx, cy))
        drawCircle(button_colour, radius = radius * dotScale, center = Offset(cx, cy))

        if (drawLine) {
            val top = cy + radius * 2.2f
            val bottom = size.height + (cy - radius * 2.2f) // reach just above the next dot
            if (bottom > top) {
                drawLine(
                    color = DividerSoft,
                    start = Offset(cx, top),
                    end = Offset(cx, top + (bottom - top) * p),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
