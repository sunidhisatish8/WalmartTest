package com.example.walmart.data.network

/**
 * Implements base url.
 * It will be easy to provide any url based on environment {prod,dev,test}.
 */
fun interface EnvironmentProvider {
    fun provideBaseUrl(): String
}

class EnvironmentProviderImpl : EnvironmentProvider {
    override fun provideBaseUrl(): String {
        //fixme hardcoded url... Can be provide via gradle file or backend configuration, etc...
        return "https://gist.githubusercontent.com/peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/db25946fd77c5873b0303b858e861ce724e0dcd0/"
    }
}