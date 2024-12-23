package com.example.walmart.data.di

import com.example.walmart.data.error.ErrorFormatterImpl
import com.example.walmart.data.mapper.CountryMapper
import com.example.walmart.data.repo.CountryRepoImpl
import com.example.walmart.domain.di.add
import com.example.walmart.domain.di.get
import com.example.walmart.domain.di.module
import com.example.walmart.domain.error.ErrorFormatter
import com.example.walmart.domain.provider.DispatcherProvider
import com.example.walmart.domain.repo.CountryRepo

val dataModule = module {
    add<DispatcherProvider> { object : DispatcherProvider {} }
    add<ErrorFormatter> { ErrorFormatterImpl(context = get()) }
    add<CountryRepo> { CountryRepoImpl(restService = get(), countryMapper = get()) }
    add { CountryMapper() }
}