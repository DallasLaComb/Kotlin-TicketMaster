package com.example.cs414_final_project

data class EventResponse(
    val _embedded: EmbeddedEvents
)
data class EmbeddedEvents(
    val events: ArrayList<Event>
)
data class Event(
    val name: String,
    val url: String,
    val images: ArrayList<Image>,
    val dates: EventDates,
    val _embedded: EventEmbedded,
    val priceRanges: ArrayList<PriceRange>
)
data class Image(
    val url: String,
    val width: Int,
    val height: Int,
)
data class EventDates(
    val start: StartDate
)
data class StartDate(
    val localDate: String,
    val localTime: String
)
data class EventEmbedded(
    val venues: ArrayList<Venue>
)
data class Venue(
    val name: String,
    val city: City,
    val state: State,
    val address: Address,
    val url: String
)
data class City(
    val name: String
)
data class State(
    val name: String
)
data class Address(
    val line1: String
)
data class PriceRange(
    val type: String?,
    val currency: String?,
    val min: Double?,
    val max: Double?
)
