package com.souldream.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.model.Destination
import com.souldream.ui.animation.ProvideShimmer
import com.souldream.ui.animation.staggeredEntrance
import com.souldream.ui.components.ScreenTitle
import com.souldream.ui.components.SectionMessage
import com.souldream.ui.screens.home.components.DestinationCard
import com.souldream.ui.screens.home.components.DestinationCardSkeleton
import com.souldream.ui.state.Async
import com.souldream.ui.theme.background_colour

data class ExploreUiState(val destinations: Async<List<Destination>> = Async.Loading)

data class ExploreActions(val onDestinationClick: (Destination) -> Unit = {})

private val CardModifier = Modifier.fillMaxWidth().height(190.dp)

/** Two-column grid of every destination (Home's carousel, "See all"). */
@Composable
fun ExploreScreen(
    state: ExploreUiState,
    actions: ExploreActions,
    modifier: Modifier = Modifier,
    bottomBarHeight: Dp = 0.dp,
) {
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val fullWidth: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }
    ProvideShimmer {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize().background(background_colour),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, bottom = bottomBarHeight + navigationBarInset + 24.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "title", span = fullWidth) {
                // Title carries its own 20dp inset; the grid already pads 16dp.
                ScreenTitle(stringResource(R.string.explore_title))
            }
            when (val destinations = state.destinations) {
                is Async.Loading -> items(count = 6, key = { "skeleton-$it" }) { DestinationCardSkeleton(CardModifier) }
                is Async.Failure -> item(key = "failure", span = fullWidth) {
                    SectionMessage(destinations.message ?: stringResource(R.string.section_error))
                }
                is Async.Success -> if (destinations.data.isEmpty()) {
                    item(key = "empty", span = fullWidth) { SectionMessage(stringResource(R.string.home_empty_destinations)) }
                } else {
                    items(destinations.data, key = { it.id }) { destination ->
                        DestinationCard(
                            destination = destination,
                            onClick = { actions.onDestinationClick(destination) },
                            modifier = CardModifier.staggeredEntrance(index = destinations.data.indexOf(destination), offsetY = 0.dp),
                        )
                    }
                }
            }
        }
    }
}
