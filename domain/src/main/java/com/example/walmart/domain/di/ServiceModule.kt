package com.example.walmart.domain.di

import kotlin.reflect.KClass

/**
 * Provide module to create dependencies
 */
interface ServiceModule {

    fun <T : Any> add(kClass: KClass<T>, creation: () -> T)

    fun dependencies(): Map<KClass<*>, () -> Any>

    fun <T> get(kClass: KClass<*>): T
}

inline fun <reified T : Any> ServiceModule.add(noinline creation: () -> T) {
    add(T::class, creation)
}

inline fun <reified T : Any> ServiceModule.get() = get(T::class) as T

fun module(block: ServiceModule.() -> Unit): ServiceModule {
    return object : ServiceModule {
        private val map = mutableMapOf<KClass<*>, () -> Any>()

        override fun <T : Any> add(kClass: KClass<T>, creation: () -> T) {
            map[kClass] = creation
        }

        override fun dependencies(): Map<KClass<*>, () -> Any> = map

        override fun <T> get(kClass: KClass<*>): T = ServiceProvider.get(kClass)
    }.apply(block)
}