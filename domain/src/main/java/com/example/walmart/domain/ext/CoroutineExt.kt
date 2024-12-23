package com.example.walmart.domain.ext

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * Provides simple api for handling errors.
 */
inline fun onError(crossinline block: (Throwable) -> Unit): CoroutineContext {
    return CoroutineExceptionHandler { _, throwable ->
        block(throwable)
    }
}
