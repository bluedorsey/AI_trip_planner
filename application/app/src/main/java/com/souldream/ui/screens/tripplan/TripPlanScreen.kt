package com.souldream.ui.screens.tripplan

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.model.TripActivity
import com.souldream.model.TripDay
import com.souldream.ui.animation.ProvideShimmer
import com.souldream.ui.animation.staggeredEntrance
import com.souldream.ui.components.AsyncContent
import com.souldream.ui.components.HeroBackdrop
import com.souldream.ui.components.SectionMessage
import com.souldream.ui.components.heroScrollOffset
import com.souldream.ui.screens.tripplan.components.DayTimelineItem
import com.souldream.ui.screens.tripplan.components.DayTimelineItemSkeleton
import com.souldream.ui.screens.tripplan.components.MapPlaceholder
import com.souldream.ui.screens.tripplan.components.SaveTripButton
import com.souldream.ui.screens.tripplan.components.TripDetails
import com.souldream.ui.screens.tripplan.components.TripDetailsSkeleton
import com.souldream.ui.screens.tripplan.components.TripHeader
import com.souldream.ui.screens.tripplan.components.TripStatsCard
import com.souldream.ui.screens.tripplan.components.TripTabBar
import com.souldream.ui.screens.tripplan.components.TripTopBar
import com.souldream.ui.state.Async
import com.souldream.ui.state.dataOrNull
import com.souldream.ui.state.map
import com.souldream.ui.theme.SouldreamTheme
import com.souldream.ui.theme.background_colour

private val ScreenPadding = 20.dp
private val HeroHeight = 340.dp
private val TopBarSpace = 72.dp
private val CtaAreaHeight = 108.dp

/**
 * Trip plan detail screen. Stateless with respect to data ([state] in, [actions] out);
 * the selected tab and the optimistic "saved" flag live here.
 *
 * @param mapContent slot for the Map tab. Defaults to a placeholder.
 */
@Composable
fun TripPlanScreen(
    state: TripPlanUiState,
    actions: TripPlanActions,
    modifier: Modifier = Modifier,
    mapContent: @Composable () -> Unit = { MapPlaceholder() },
) {
    var tab by rememberSaveable { mutableStateOf(TripTab.Itinerary) }
    // Re-seeded whenever the host pushes a new value, otherwise toggles optimistically.
    var isSaved by rememberSaveable(state.isSaved) { mutableStateOf(state.isSaved) }
    val toggleSave = {
        isSaved = !isSaved
        actions.onToggleSave(isSaved)
    }

    val plan = state.plan.dataOrNull()
    val listState = rememberLazyListState()
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    ProvideShimmer {
        Box(modifier.fillMaxSize().background(background_colour)) {
            HeroBackdrop(
                imageModel = plan?.heroImageUrl,
                height = HeroHeight,
                scrollOffsetProvider = { listState.heroScrollOffset() },
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = CtaAreaHeight + navigationBarInset),
            ) {
                item(key = "header") {
                    TripHeader(
                        plan = plan,
                        modifier = Modifier.padding(
                            start = ScreenPadding,
                            end = ScreenPadding,
                            top = statusBarInset + TopBarSpace + 24.dp,
                        ),
                    )
                }

                item(key = "stats") {
                    TripStatsCard(
                        plan = plan,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .staggeredEntrance(index = 4),
                    )
                }

                item(key = "tabs") {
                    TripTabBar(
                        selected = tab,
                        onSelect = { tab = it },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .staggeredEntrance(index = 5),
                    )
                }

                item(key = "tab-content") {
                    AnimatedContent(
                        targetState = tab,
                        modifier = Modifier.fillMaxWidth(),
                        transitionSpec = {
                            val forward = targetState.ordinal > initialState.ordinal
                            val direction = if (forward) 1 else -1
                            (slideInHorizontally(tween(350)) { it / 4 * direction } + fadeIn(tween(350))) togetherWith
                                (slideOutHorizontally(tween(300)) { -it / 4 * direction } + fadeOut(tween(200))) using
                                SizeTransform(clip = false)
                        },
                        label = "tripTabContent",
                    ) { current ->
                        when (current) {
                            TripTab.Itinerary -> ItineraryContent(
                                days = state.plan.map { it.days },
                                onActivityClick = actions.onActivityClick,
                            )

                            TripTab.Details -> AsyncContent(
                                state = state.plan,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                                loading = { TripDetailsSkeleton() },
                            ) { TripDetails(it) }

                            TripTab.Map -> Box(Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                                mapContent()
                            }
                        }
                    }
                }
            }

            // Floating controls, drawn above the scrolling content.
            TripTopBar(
                onBack = actions.onBack,
                onShare = actions.onShare,
                onMore = actions.onMore,
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(background_colour.copy(alpha = 0f), background_colour),
                        ),
                    )
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 28.dp,
                        bottom = navigationBarInset + 16.dp,
                    )
                    .staggeredEntrance(index = 6, offsetY = 72.dp),
            ) {
                SaveTripButton(
                    isSaved = isSaved,
                    onClick = toggleSave,
                    enabled = plan != null,
                )
            }
        }
    }
}

@Composable
private fun ItineraryContent(
    days: Async<List<TripDay>>,
    onActivityClick: (TripActivity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sidePadding = Modifier.padding(start = ScreenPadding + 2.dp, end = 16.dp, top = 28.dp)
    AsyncContent(
        state = days,
        modifier = modifier.fillMaxWidth(),
        loading = {
            Column(sidePadding) {
                repeat(3) { DayTimelineItemSkeleton(isLast = it == 2) }
            }
        },
    ) { list ->
        if (list.isEmpty()) {
            SectionMessage(stringResource(R.string.trip_empty_itinerary))
        } else {
            Column(sidePadding) {
                list.forEachIndexed { index, day ->
                    DayTimelineItem(
                        day = day,
                        isLast = index == list.lastIndex,
                        onActivityClick = onActivityClick,
                        index = index,
                    )
                }
            }
        }
    }
}

/** Default state = plan loading, i.e. the shimmer placeholders. */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TripPlanScreenPlaceholderPreview() {
    SouldreamTheme(darkTheme = false, dynamicColor = false) {
        TripPlanScreen(state = TripPlanUiState(), actions = TripPlanActions())
    }
}
