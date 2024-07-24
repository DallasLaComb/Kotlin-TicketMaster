package com.example.cs414_final_project

data class EventData(
    val name: String = "",
    val imageUrl: String = "",
    var date: String = "",
    val venueName: String = "",
    val venueCity: String = "",
    val venueState: String = "",
    val venueAddress: String = "",
    val ticketUrl: String = "",
    val minPrice: String = "",
    val maxPrice: String = ""
)