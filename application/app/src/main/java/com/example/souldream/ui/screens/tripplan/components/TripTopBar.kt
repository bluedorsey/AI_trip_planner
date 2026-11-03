package com.example.souldream.ui.screens.tripplan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.souldream.R
import com.example.souldream.ui.animation.staggeredEntrance
import com.example.souldream.ui.components.RoundIconButton

/** Back / share / more buttons floating above the hero. */
@Composable
fun TripTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundIconButton(
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.cd_back),
            onClick = onBack,
            size = 48.dp,
            modifier = Modifier.staggeredEntrance(index = 0, offsetY = (-12).dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoundIconButton(
                icon = Icons.Rounded.IosShare,
                contentDescription = stringResource(R.string.cd_share),
                onClick = onShare,
                size = 48.dp,
                modifier = Modifier.staggeredEntrance(index = 1, offsetY = (-12).dp),
            )
            RoundIconButton(
                icon = Icons.Rounded.MoreHoriz,
                contentDescription = stringResource(R.string.cd_more),
                onClick = onMore,
                size = 48.dp,
                modifier = Modifier.staggeredEntrance(index = 2, offsetY = (-12).dp),
            )
        }
    }
}
