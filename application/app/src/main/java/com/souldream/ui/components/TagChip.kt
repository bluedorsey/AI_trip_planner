package com.souldream.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.souldream.ui.theme.selected_icon
import com.souldream.ui.theme.selected_slider

/** Small pill with an icon and a label, used for categories/tags. */
@Composable
fun TagChip(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = selected_slider,
    contentColor: Color = selected_icon,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
        )
    }
}
