package com.example.walmart.data.di

import com.example.walmart.data.network.CountryRestService
import com.example.walmart.data.network.EnvironmentProvider
import com.example.walmart.data.network.EnvironmentProviderImpl
import com.example.walmart.domain.di.add
import com.example.walmart.domain.di.get
import com.example.walmart.domain.di.module
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    add { Gson() }
    add<EnvironmentProvider> { EnvironmentProviderImpl() }

    add<Retrofit> {
        Retrofit.Builder()
            .baseUrl(get<EnvironmentProvider>().provideBaseUrl())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
    add<CountryRestService> {
        get<Retrofit>().create(CountryRestService::class.java)
    }
}