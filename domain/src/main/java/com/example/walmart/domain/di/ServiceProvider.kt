package com.example.walmart.domain.di

import kotlin.reflect.KClass

/**
 * Simple DI to provide dependencies
 */
object ServiceProvider {

    private val dependencies = mutableMapOf<KClass<*>, () -> Any>()

    inline fun <reified T> get(): T = get(T::class)

    fun <T> get(klass: KClass<*>): T {
        @Suppress("UNCHECKED_CAST")
        return requireNotNull(dependencies[klass]?.invoke()) {
            "Dependency ${klass.qualifiedName} is not provided in module"
        } as T
    }

    fun initialize(vararg modules: ServiceModule) {
        check(dependencies.isEmpty()) {
            "Initialization can be called only once"
        }
        modules.forEach { dependencies.putAll(it.dependencies()) }
    }
}