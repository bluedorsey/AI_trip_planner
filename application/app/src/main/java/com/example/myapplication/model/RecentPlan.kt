package com.example.myapplication.model

/** Summary of a previously generated plan, shown under "Your Recent Plans". */
data class RecentPlan(
    val id: String,
    val title: String,
    /** Pre-formatted, e.g. a date range. Formatting is the data layer's job. */
    val dateRange: String,
    val location: String,
    val imageUrl: String? = null,
)
