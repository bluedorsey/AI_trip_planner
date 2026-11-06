package com.souldream.ui.screens.tripplan

import com.souldream.model.TripActivity
import com.souldream.model.TripPlan
import com.souldream.ui.state.Async

/**
 * Everything the Trip Plan screen renders. While [plan] is [Async.Loading] the header,
 * stats and itinerary all show shimmer placeholders (this doubles as the
 * "AI is generating your trip" state).
 */
data class TripPlanUiState(
    val plan: Async<TripPlan> = Async.Loading,
    val isSaved: Boolean = false,
)

/** User intents emitted by the Trip Plan screen. Defaults are no-ops. */
data class TripPlanActions(
    val onBack: () -> Unit = {},
    val onShare: () -> Unit = {},
    val onMore: () -> Unit = {},
    val onToggleSave: (isSaved: Boolean) -> Unit = {},
    val onActivityClick: (TripActivity) -> Unit = {},
)

enum class TripTab { Itinerary, Details, Map }
