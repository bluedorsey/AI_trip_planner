package com.souldream.model

import androidx.compose.ui.graphics.vector.ImageVector

/** A quick-filter chip under the search field. */
data class TripCategory(
    val id: String,
    val label: String,
    val icon: ImageVector? = null,
)
