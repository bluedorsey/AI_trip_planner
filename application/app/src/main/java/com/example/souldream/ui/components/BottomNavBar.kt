package com.example.souldream.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.souldream.ui.animation.pressScale
import com.example.souldream.ui.theme.GlassWhiteStrong
import com.example.souldream.ui.theme.selected_icon

/** Height of the bar content, excluding the system navigation-bar inset. */
val BottomNavBarHeight: Dp = 68.dp

data class BottomNavItem(
    val id: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedId: String,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(20.dp, shape, clip = false)
            .clip(shape)
            .background(GlassWhiteStrong)
            .navigationBarsPadding()
            .height(BottomNavBarHeight),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items.forEach { item ->
            BottomNavEntry(
                item = item,
                selected = item.id == selectedId,
                onClick = { onItemSelected(item) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomNavEntry(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint by animateColorAsState(
        targetValue = if (selected) selected_icon else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "navTint",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "navIconScale",
    )
    val indicatorWidth by animateDpAsState(
        targetValue = if (selected) 22.dp else 0.dp,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow),
        label = "navIndicator",
    )

    Box(
        modifier = modifier.fillMaxHeight().pressScale(onClick, pressedScale = 0.9f),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .size(width = indicatorWidth, height = 3.dp)
                .clip(CircleShape)
                .background(selected_icon),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .size(26.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    },
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(item.labelRes),
                style = MaterialTheme.typography.labelMedium,
                color = tint,
                maxLines = 1,
            )
        }
    }
}
