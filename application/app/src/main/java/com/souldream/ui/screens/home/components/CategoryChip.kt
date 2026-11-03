package com.souldream.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.souldream.model.TripCategory
import com.souldream.ui.animation.pressScale
import com.souldream.ui.theme.button_colour
import com.souldream.ui.theme.radio_button_colour

/** Selectable quick-filter pill; colours animate between the idle and selected state. */
@Composable
fun CategoryChip(
    category: TripCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container by animateColorAsState(
        targetValue = if (selected) button_colour else radio_button_colour,
        label = "chipContainer",
    )
    val content by animateColorAsState(
        targetValue = if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
        label = "chipContent",
    )

    Row(
        modifier = modifier
            .pressScale(onClick)
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        category.icon?.let { icon ->
            Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = category.label,
            style = MaterialTheme.typography.labelLarge,
            color = content,
            maxLines = 1,
        )
    }
}
