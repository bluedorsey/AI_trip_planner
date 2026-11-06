package com.souldream.model

/** Kind of itinerary entry. Label + icon + tint are resolved in the UI layer. */
enum class ActivityType { Hotel, Activity, Sightseeing, Food, Transport }

data class TripActivity(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val type: ActivityType,
    val imageUrl: String? = null,
)

data class TripDay(
    val id: String,
    val dayNumber: Int,
    /** Pre-formatted date label, e.g. "Dec 20, Sat". */
    val date: String,
    /** Short theme of the day, e.g. "Arrival & Relax". */
    val theme: String,
    val activities: List<TripActivity>,
)

data class CostItem(val label: String, val amount: String)

/**
 * A full trip plan. Display strings (budget, duration, rating, date range) are
 * expected to be pre-formatted so the UI stays free of formatting/locale logic.
 */
data class TripPlan(
    val id: String,
    val title: String,
    val dateRange: String,
    val location: String,
    val budget: String,
    val duration: String,
    val rating: String,
    val days: List<TripDay>,
    val heroImageUrl: String? = null,
    val perDayBudget: String = "",
    val costBreakdown: List<CostItem> = emptyList(),
    val weather: String = "",
    val transport: String = "",
)
