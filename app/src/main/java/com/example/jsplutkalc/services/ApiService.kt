package com.example.jsplutkalc.services
import com.example.jsplutkalc.models.Trip
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/jspl")
    fun postTrip(@Body trip: Trip): Call<Void>
}