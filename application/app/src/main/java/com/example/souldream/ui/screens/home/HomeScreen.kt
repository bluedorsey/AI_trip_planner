package com.example.souldream.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.souldream.R
import com.example.souldream.ui.animation.staggeredEntrance
import com.example.souldream.ui.components.HeroBackdrop
import com.example.souldream.ui.components.SectionHeader
import com.example.souldream.ui.components.SectionMessage
import com.example.souldream.ui.components.asyncItems
import com.example.souldream.ui.components.heroScrollOffset
import com.example.souldream.ui.screens.home.components.DestinationCard
import com.example.souldream.ui.screens.home.components.DestinationCardSkeleton
import com.example.souldream.ui.screens.home.components.HeroHeadline
import com.example.souldream.ui.screens.home.components.HomeTopBar
import com.example.souldream.ui.screens.home.components.RecentPlanCard
import com.example.souldream.ui.screens.home.components.RecentPlanCardSkeleton
import com.example.souldream.ui.screens.home.components.SearchCard
import com.example.souldream.ui.theme.SouldreamTheme
import com.example.souldream.ui.theme.background_colour

private val ScreenPadding = 20.dp
private val HeroHeight = 400.dp

/**
 * Home / landing screen.
 *
 * Stateless with respect to data ([state] in, [actions] out). Only ephemeral input
 * (query text, selected chip) lives here.
 *
 * @param bottomBarHeight height of any bar the host overlays at the bottom, so the
 * last item can scroll clear of it.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    actions: HomeActions,
    modifier: Modifier = Modifier,
    bottomBarHeight: Dp = 0.dp,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(modifier.fillMaxSize().background(background_colour)) {
        HeroBackdrop(
            imageModel = state.heroImage,
            height = HeroHeight,
            scrollOffsetProvider = { listState.heroScrollOffset() },
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomBarHeight + navigationBarInset + 24.dp),
        ) {
            item(key = "top-bar") {
                HomeTopBar(
                    onProfileClick = actions.onProfileClick,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = ScreenPadding, vertical = 12.dp)
                        .staggeredEntrance(index = 0, offsetY = (-16).dp),
                )
            }

            item(key = "headline") {
                HeroHeadline(
                    modifier = Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 24.dp),
                )
            }

            // Breathing room so the hero image shows between the headline and the search card.
            item(key = "hero-gap") { Spacer(Modifier.height(96.dp)) }

            item(key = "search") {
                SearchCard(
                    query = query,
                    onQueryChange = { query = it },
                    onSubmit = { actions.onSearchSubmit(query.trim(), selectedCategoryId) },
                    categories = state.categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategoryClick = { category ->
                        // Tapping the selected chip again clears the selection.
                        selectedCategoryId = if (selectedCategoryId == category.id) null else category.id
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .staggeredEntrance(index = 3),
                )
            }

            item(key = "explore-header") {
                SectionHeader(
                    title = stringResource(R.string.home_section_explore),
                    actionLabel = stringResource(R.string.action_see_all),
                    onActionClick = actions.onSeeAllDestinations,
                    modifier = Modifier
                        .padding(start = ScreenPadding, end = ScreenPadding, top = 32.dp, bottom = 14.dp)
                        .staggeredEntrance(index = 4),
                )
            }

            item(key = "explore-row") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = ScreenPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    asyncItems(
                        state = state.destinations,
                        skeletonCount = 3,
                        key = { it.id },
                        skeleton = { DestinationCardSkeleton() },
                        empty = { SectionMessage(stringResource(R.string.home_empty_destinations)) },
                    ) { index, destination ->
                        DestinationCard(
                            destination = destination,
                            onClick = { actions.onDestinationClick(destination) },
                            modifier = Modifier.staggeredEntrance(index = index, offsetY = 0.dp),
                        )
                    }
                }
            }

            item(key = "plans-header") {
                SectionHeader(
                    title = stringResource(R.string.home_section_recent),
                    actionLabel = stringResource(R.string.action_see_all),
                    onActionClick = actions.onSeeAllPlans,
                    modifier = Modifier
                        .padding(start = ScreenPadding, end = ScreenPadding, top = 28.dp, bottom = 14.dp)
                        .staggeredEntrance(index = 5),
                )
            }

            asyncItems(
                state = state.recentPlans,
                skeletonCount = 2,
                key = { it.id },
                skeleton = {
                    RecentPlanCardSkeleton(Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                },
                empty = { SectionMessage(stringResource(R.string.home_empty_plans)) },
            ) { index, plan ->
                RecentPlanCard(
                    plan = plan,
                    onClick = { actions.onPlanClick(plan) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .staggeredEntrance(index = index),
                )
            }
        }
    }
}

/** Default state = every section loading, i.e. the shimmer placeholders. */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPlaceholderPreview() {
    SouldreamTheme(darkTheme = false, dynamicColor = false) {
        HomeScreen(state = HomeUiState(), actions = HomeActions())
    }
}
