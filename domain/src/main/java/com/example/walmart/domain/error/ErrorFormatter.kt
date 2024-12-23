package com.example.walmart.domain.error

interface ErrorFormatter {

    fun getDisplayErrorMessage(throwable: Throwable): String
}