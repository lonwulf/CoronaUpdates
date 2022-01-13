package com.example.coronaupdate.retrofit

import com.example.coronaupdate.retrofit.structures.CoronaResponseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("v1/stats")
    fun getCoronaUpdates(@Query("country")country:String):Call<CoronaResponseDTO>

    @GET("v1/stats")
    suspend fun getCoronaUpdatesWithCoroutines(@Query("country")country:String):CoronaResponseDTO
}