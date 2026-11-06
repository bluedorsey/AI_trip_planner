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
    /** Latest progress message while the trip is being planned, e.g. "Found restaurants". */
    val status: String? = null,
    /** True while days are still arriving: [plan] holds a partial trip and can't be saved yet. */
    val streaming: Boolean = false,
)

/** User intents emitted by the Trip Plan screen. Defaults are no-ops. */
data class TripPlanActions(
    val onBack: () -> Unit = {},
    val onShare: () -> Unit = {},
    val onToggleSave: (isSaved: Boolean) -> Unit = {},
    val onActivityClick: (TripActivity) -> Unit = {},
)

enum class TripTab { Itinerary, Details, Map }
