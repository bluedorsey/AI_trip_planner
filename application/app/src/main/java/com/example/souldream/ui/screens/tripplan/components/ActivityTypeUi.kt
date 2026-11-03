package com.example.souldream.ui.screens.tripplan.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Hiking
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.souldream.R
import com.example.souldream.model.ActivityType

/** UI-layer presentation of [ActivityType]: keeps the model free of Compose/resources. */
@get:StringRes
val ActivityType.labelRes: Int
    get() = when (this) {
        ActivityType.Hotel -> R.string.activity_type_hotel
        ActivityType.Activity -> R.string.activity_type_activity
        ActivityType.Sightseeing -> R.string.activity_type_sightseeing
        ActivityType.Food -> R.string.activity_type_food
        ActivityType.Transport -> R.string.activity_type_transport
    }

val ActivityType.icon: ImageVector
    get() = when (this) {
        ActivityType.Hotel -> Icons.Rounded.Hotel
        ActivityType.Activity -> Icons.Rounded.Hiking
        ActivityType.Sightseeing -> Icons.Rounded.PhotoCamera
        ActivityType.Food -> Icons.Rounded.Restaurant
        ActivityType.Transport -> Icons.Rounded.DirectionsBus
    }
