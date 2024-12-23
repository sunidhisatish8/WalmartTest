package com.example.walmart.data.network

import com.example.walmart.data.network.response.CountryResponse
import retrofit2.http.GET

interface CountryRestService {

    @GET("countries.json")
    suspend fun getCountries(): List<CountryResponse>
}