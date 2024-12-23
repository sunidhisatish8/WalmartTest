package com.example.walmart.data.network.response

import com.google.gson.annotations.SerializedName

data class CountryResponse(
    @SerializedName("capital")
    val capital: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("region")
    val region: String? = null
)