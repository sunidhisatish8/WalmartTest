package com.example.walmart.data.mapper

import com.example.walmart.data.network.response.CountryResponse
import com.example.walmart.domain.model.Country

class CountryMapper {

    fun toModel(response: CountryResponse) = with(response) {
        Country(
            name = name.orEmpty(),
            region = region.orEmpty(),
            code = code.orEmpty(),
            capital = capital.orEmpty()
        )
    }
}