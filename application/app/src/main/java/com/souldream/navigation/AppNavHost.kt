package com.souldream.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.souldream.R
import com.souldream.ui.components.BottomNavBar
import com.souldream.ui.components.BottomNavBarHeight
import com.souldream.ui.components.BottomNavItem
import com.souldream.ui.screens.home.HomeActions
import com.souldream.ui.screens.home.HomeScreen
import com.souldream.ui.screens.home.HomeUiState
import com.souldream.ui.screens.tripplan.TripPlanActions
import com.souldream.ui.screens.tripplan.TripPlanScreen
import com.souldream.ui.screens.tripplan.TripPlanUiState

enum class AppDestination { Home, TripPlan }

object BottomNavIds {
    const val Home = "home"
    const val Saved = "saved"
    const val Explore = "explore"
    const val Settings = "settings"
}

private val bottomNavItems = listOf(
    BottomNavItem(BottomNavIds.Home, R.string.nav_home, Icons.Outlined.Home, Icons.Rounded.Home),
    BottomNavItem(BottomNavIds.Saved, R.string.nav_saved, Icons.Outlined.BookmarkBorder, Icons.Rounded.Bookmark),
    BottomNavItem(BottomNavIds.Explore, R.string.nav_explore, Icons.Outlined.Explore, Icons.Rounded.Explore),
    BottomNavItem(BottomNavIds.Settings, R.string.nav_settings, Icons.Outlined.Settings, Icons.Rounded.Settings),
)

/**
 * App shell: owns navigation between Home and Trip Plan, the screen transitions and the
 * bottom bar. Data and side effects stay outside; pass the current UI state in and
 * observe user intents through the actions. The wrapped actions here only *add*
 * navigation, then forward to the caller's action.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    homeState: HomeUiState = HomeUiState(),
    homeActions: HomeActions = HomeActions(),
    tripPlanState: TripPlanUiState = TripPlanUiState(),
    tripPlanActions: TripPlanActions = TripPlanActions(),
    onBottomNavSelected: (BottomNavItem) -> Unit = {},
) {
    var destination by rememberSaveable { mutableStateOf(AppDestination.Home) }

    val navigatingHomeActions = remember(homeActions) {
        homeActions.copy(
            onSearchSubmit = { query, categoryId ->
                destination = AppDestination.TripPlan
                homeActions.onSearchSubmit(query, categoryId)
            },
            onPlanClick = { plan ->
                destination = AppDestination.TripPlan
                homeActions.onPlanClick(plan)
            },
        )
    }
    val navigatingTripActions = remember(tripPlanActions) {
        tripPlanActions.copy(
            onBack = {
                destination = AppDestination.Home
                tripPlanActions.onBack()
            },
        )
    }

    BackHandler(enabled = destination == AppDestination.TripPlan) {
        navigatingTripActions.onBack()
    }

    Box(modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = destination,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { screenTransition(forward = targetState == AppDestination.TripPlan) },
            label = "screenTransition",
        ) { current ->
            when (current) {
                AppDestination.Home -> HomeScreen(
                    state = homeState,
                    actions = navigatingHomeActions,
                    bottomBarHeight = BottomNavBarHeight,
                )

                AppDestination.TripPlan -> TripPlanScreen(
                    state = tripPlanState,
                    actions = navigatingTripActions,
                )
            }
        }

        AnimatedVisibility(
            visible = destination == AppDestination.Home,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(450, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(300)) { it } + fadeOut(tween(200)),
        ) {
            BottomNavBar(
                items = bottomNavItems,
                // Only Home exists as a destination so far; other tabs just notify the caller.
                selectedId = BottomNavIds.Home,
                onItemSelected = onBottomNavSelected,
            )
        }
    }
}

/** Forward: new screen slides in from the right on top. Back: reverse, old screen on top. */
private fun screenTransition(forward: Boolean): ContentTransform {
    val spec = tween<IntOffset>(450, easing = FastOutSlowInEasing)
    val transition = if (forward) {
        (slideInHorizontally(spec) { it / 3 } + fadeIn(tween(350))) togetherWith
            (slideOutHorizontally(spec) { -it / 4 } + fadeOut(tween(300)))
    } else {
        (slideInHorizontally(spec) { -it / 4 } + fadeIn(tween(350))) togetherWith
            (slideOutHorizontally(spec) { it / 3 } + fadeOut(tween(300)))
    }
    transition.targetContentZIndex = if (forward) 1f else -1f
    return transition
}
