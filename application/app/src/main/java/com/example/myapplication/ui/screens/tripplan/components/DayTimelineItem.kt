package com.example.myapplication.ui.screens.tripplan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.example.myapplication.R
import com.example.myapplication.model.TripActivity
import com.example.myapplication.model.TripDay
import com.example.myapplication.ui.animation.rememberEntranceProgress
import com.example.myapplication.ui.animation.staggeredEntrance
import com.example.myapplication.ui.components.ShimmerBox
import com.example.myapplication.ui.theme.DividerSoft
import com.example.myapplication.ui.theme.button_colour
import com.example.myapplication.ui.theme.selected_slider

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
            Text(
                text = stringResource(R.string.trip_day_header, day.dayNumber, day.date),
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
    Row(modifier = modifier.timelineGutter(progress = { 1f }, drawLine = !isLast)) {
        Spacer(Modifier.width(GutterWidth))
        Column(
            modifier = Modifier.weight(1f).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ShimmerBox(Modifier.fillMaxWidth(0.5f).height(20.dp), CircleShape)
            ShimmerBox(Modifier.fillMaxWidth(0.35f).height(14.dp), CircleShape)
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
