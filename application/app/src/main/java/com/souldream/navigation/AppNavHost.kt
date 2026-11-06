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
import com.souldream.ui.screens.explore.ExploreActions
import com.souldream.ui.screens.explore.ExploreScreen
import com.souldream.ui.screens.explore.ExploreUiState
import com.souldream.ui.screens.home.HomeActions
import com.souldream.ui.screens.home.HomeScreen
import com.souldream.ui.screens.home.HomeUiState
import com.souldream.ui.screens.login.LoginActions
import com.souldream.ui.screens.login.LoginScreen
import com.souldream.ui.screens.login.LoginUiState
import com.souldream.ui.screens.saved.SavedActions
import com.souldream.ui.screens.saved.SavedScreen
import com.souldream.ui.screens.saved.SavedUiState
import com.souldream.ui.screens.settings.SettingsActions
import com.souldream.ui.screens.settings.SettingsScreen
import com.souldream.ui.screens.settings.SettingsUiState
import com.souldream.ui.screens.tripplan.TripPlanActions
import com.souldream.ui.screens.tripplan.TripPlanScreen
import com.souldream.ui.screens.tripplan.TripPlanUiState

/** [navId] is the bottom-bar tab a destination belongs to; null = full-screen, no bar. */
enum class AppDestination(val navId: String?) {
    Home(BottomNavIds.Home),
    Saved(BottomNavIds.Saved),
    Explore(BottomNavIds.Explore),
    Settings(BottomNavIds.Settings),
    TripPlan(null),
}

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
 * App shell: owns navigation (Login while signed out, the four bottom-bar tabs, Trip Plan), the screen
 * transitions and the bottom bar. Data and side effects stay outside; pass the current UI
 * state in and observe user intents through the actions. The wrapped actions here only
 * *add* navigation, then forward to the caller's action.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    signedIn: Boolean = true,
    loginState: LoginUiState = LoginUiState(),
    loginActions: LoginActions = LoginActions(),
    homeState: HomeUiState = HomeUiState(),
    homeActions: HomeActions = HomeActions(),
    savedState: SavedUiState = SavedUiState(),
    savedActions: SavedActions = SavedActions(),
    exploreState: ExploreUiState = ExploreUiState(),
    exploreActions: ExploreActions = ExploreActions(),
    settingsState: SettingsUiState = SettingsUiState(),
    settingsActions: SettingsActions = SettingsActions(),
    tripPlanState: TripPlanUiState = TripPlanUiState(),
    tripPlanActions: TripPlanActions = TripPlanActions(),
    onBottomNavSelected: (BottomNavItem) -> Unit = {},
) {
    var destination by rememberSaveable { mutableStateOf(AppDestination.Home) }
    // Where Trip Plan's back button returns to (Home or Saved).
    var tripOrigin by rememberSaveable { mutableStateOf(AppDestination.Home) }

    if (!signedIn) {
        LoginScreen(state = loginState, actions = loginActions, modifier = modifier)
        return
    }

    fun openTrip() {
        tripOrigin = destination
        destination = AppDestination.TripPlan
    }

    val navigatingHomeActions = remember(homeActions) {
        homeActions.copy(
            onProfileClick = {
                destination = AppDestination.Settings // where the account lives
                homeActions.onProfileClick()
            },
            onSearchSubmit = { query, categoryId ->
                openTrip()
                homeActions.onSearchSubmit(query, categoryId)
            },
            onDestinationClick = { destinationItem ->
                openTrip()
                homeActions.onDestinationClick(destinationItem)
            },
            onPlanClick = { plan ->
                openTrip()
                homeActions.onPlanClick(plan)
            },
            onSeeAllDestinations = {
                destination = AppDestination.Explore
                homeActions.onSeeAllDestinations()
            },
            onSeeAllPlans = {
                destination = AppDestination.Saved
                homeActions.onSeeAllPlans()
            },
        )
    }
    val navigatingExploreActions = remember(exploreActions) {
        exploreActions.copy(
            onDestinationClick = { destinationItem ->
                openTrip()
                exploreActions.onDestinationClick(destinationItem)
            },
        )
    }
    val navigatingSettingsActions = remember(settingsActions) {
        settingsActions.copy(
            onSignOutClick = {
                destination = AppDestination.Home // so the next sign-in starts on Home
                settingsActions.onSignOutClick()
            },
        )
    }
    val navigatingSavedActions = remember(savedActions) {
        savedActions.copy(
            onPlanClick = { plan ->
                openTrip()
                savedActions.onPlanClick(plan)
            },
        )
    }
    val navigatingTripActions = remember(tripPlanActions) {
        tripPlanActions.copy(
            onBack = {
                destination = tripOrigin
                tripPlanActions.onBack()
            },
        )
    }

    BackHandler(enabled = destination == AppDestination.TripPlan) {
        navigatingTripActions.onBack()
    }
    // Tabs other than Home go back to Home before the app closes.
    BackHandler(enabled = destination.navId != null && destination != AppDestination.Home) {
        destination = AppDestination.Home
    }

    Box(modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = destination,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                if (targetState == AppDestination.TripPlan || initialState == AppDestination.TripPlan) {
                    screenTransition(forward = targetState == AppDestination.TripPlan)
                } else {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                }
            },
            label = "screenTransition",
        ) { current ->
            when (current) {
                AppDestination.Home -> HomeScreen(
                    state = homeState,
                    actions = navigatingHomeActions,
                    bottomBarHeight = BottomNavBarHeight,
                )

                AppDestination.Saved -> SavedScreen(
                    state = savedState,
                    actions = navigatingSavedActions,
                    bottomBarHeight = BottomNavBarHeight,
                )

                AppDestination.Explore -> ExploreScreen(
                    state = exploreState,
                    actions = navigatingExploreActions,
                    bottomBarHeight = BottomNavBarHeight,
                )

                AppDestination.Settings -> SettingsScreen(
                    state = settingsState,
                    actions = navigatingSettingsActions,
                    bottomBarHeight = BottomNavBarHeight,
                )

                AppDestination.TripPlan -> TripPlanScreen(
                    state = tripPlanState,
                    actions = navigatingTripActions,
                )
            }
        }

        AnimatedVisibility(
            visible = destination.navId != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(450, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(300)) { it } + fadeOut(tween(200)),
        ) {
            BottomNavBar(
                items = bottomNavItems,
                selectedId = destination.navId ?: BottomNavIds.Home,
                onItemSelected = { item ->
                    destination = AppDestination.entries.first { it.navId == item.id }
                    onBottomNavSelected(item)
                },
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
