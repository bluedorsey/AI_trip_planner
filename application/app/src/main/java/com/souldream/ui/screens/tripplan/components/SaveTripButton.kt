package com.souldream.ui.screens.tripplan.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.ui.components.GradientActionButton

/** Primary CTA; its label slides between "Save Trip Plan" and "Saved". */
@Composable
fun SaveTripButton(
    isSaved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    GradientActionButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        AnimatedContent(
            targetState = isSaved,
            transitionSpec = {
                (slideInVertically { it / 2 } + fadeIn()) togetherWith
                    (slideOutVertically { -it / 2 } + fadeOut())
            },
            label = "saveLabel",
        ) { saved ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (saved) Icons.Rounded.Check else Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(if (saved) R.string.trip_saved else R.string.trip_save_plan),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
            }
        }
    }
}
