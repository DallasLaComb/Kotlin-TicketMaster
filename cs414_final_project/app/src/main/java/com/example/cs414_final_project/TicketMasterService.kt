package com.example.cs414_final_project

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterService {
    @GET("discovery/v2/events.json")
    fun searchEvents(
        @Query("apikey") apiKey: String,
        @Query("keyword") eventType: String,
        @Query("city") city: String,
        @Query("sort") sortBy: String,
    ): Call<EventResponse>
}