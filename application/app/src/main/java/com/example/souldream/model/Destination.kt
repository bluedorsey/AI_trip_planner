package com.example.souldream.model

/** A destination shown in the "Explore" carousel. All values come from the data layer. */
data class Destination(
    val id: String,
    val name: String,
    val tagline: String,
    val imageUrl: String? = null,
)
