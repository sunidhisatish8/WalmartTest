package com.example.walmart.data.error

import android.content.Context
import com.example.walmart.data.R
import com.example.walmart.domain.error.ErrorFormatter
import java.net.UnknownHostException

class ErrorFormatterImpl(private val context: Context) : ErrorFormatter {
    override fun getDisplayErrorMessage(throwable: Throwable): String {
        return when {
            throwable is UnknownHostException -> {
                context.getString(R.string.error_internet_connection)
            }

            throwable.message.isNullOrBlank() -> {
                context.getString(R.string.error_unknown)
            }

            else -> throwable.message.orEmpty()
        }
    }
}