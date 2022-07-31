package com.example.bookingdesign

import androidx.annotation.DrawableRes

data class Destination(
    @DrawableRes val destinationImage: Int,
    val destinationName: String,
    val destinationRating: Float
)
