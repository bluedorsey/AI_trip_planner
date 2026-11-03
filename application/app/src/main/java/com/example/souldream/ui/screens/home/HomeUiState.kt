package com.example.souldream.ui.screens.home

import com.example.souldream.model.Destination
import com.example.souldream.model.RecentPlan
import com.example.souldream.model.TripCategory
import com.example.souldream.ui.state.Async

/**
 * Everything the Home screen renders. Each section starts in [Async.Loading], which
 * the UI shows as shimmer placeholders until the data layer supplies real values.
 */
data class HomeUiState(
    /** Anything Coil can load (URL, Uri, drawable id). Null shows the sky gradient. */
    val heroImage: Any? = null,
    val categories: Async<List<TripCategory>> = Async.Loading,
    val destinations: Async<List<Destination>> = Async.Loading,
    val recentPlans: Async<List<RecentPlan>> = Async.Loading,
)

/** User intents emitted by the Home screen. Defaults are no-ops. */
data class HomeActions(
    val onProfileClick: () -> Unit = {},
    val onSearchSubmit: (query: String, categoryId: String?) -> Unit = { _, _ -> },
    val onDestinationClick: (Destination) -> Unit = {},
    val onPlanClick: (RecentPlan) -> Unit = {},
    val onSeeAllDestinations: () -> Unit = {},
    val onSeeAllPlans: () -> Unit = {},
)
