package com.souldream.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.model.RecentPlan
import com.souldream.ui.animation.ProvideShimmer
import com.souldream.ui.animation.staggeredEntrance
import com.souldream.ui.components.ScreenTitle
import com.souldream.ui.components.SectionMessage
import com.souldream.ui.components.asyncItems
import com.souldream.ui.screens.home.components.RecentPlanCard
import com.souldream.ui.screens.home.components.RecentPlanCardSkeleton
import com.souldream.ui.state.Async
import com.souldream.ui.theme.background_colour

data class SavedUiState(val plans: Async<List<RecentPlan>> = Async.Loading)

data class SavedActions(val onPlanClick: (RecentPlan) -> Unit = {})

/** Saved trips: the same plan cards as Home's "Recent Plans", as a full list. */
@Composable
fun SavedScreen(
    state: SavedUiState,
    actions: SavedActions,
    modifier: Modifier = Modifier,
    bottomBarHeight: Dp = 0.dp,
) {
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    ProvideShimmer {
        LazyColumn(
            modifier = modifier.fillMaxSize().background(background_colour),
            contentPadding = PaddingValues(bottom = bottomBarHeight + navigationBarInset + 24.dp),
        ) {
            item(key = "title") { ScreenTitle(stringResource(R.string.saved_title)) }
            asyncItems(
                state = state.plans,
                skeletonCount = 4,
                key = { it.id },
                skeleton = { RecentPlanCardSkeleton(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) },
                empty = { SectionMessage(stringResource(R.string.saved_empty)) },
                failure = { SectionMessage(it ?: stringResource(R.string.section_error)) },
            ) { index, plan ->
                RecentPlanCard(
                    plan = plan,
                    onClick = { actions.onPlanClick(plan) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp).staggeredEntrance(index = index),
                )
            }
        }
    }
}
