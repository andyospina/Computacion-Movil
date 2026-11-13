package com.example.myapplication.data.time

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/** Hora actual inyectable: permite probar ventanas de tiempo (p. ej. «últimas 24 horas») sin esperar. */
interface TimeProvider {
    fun nowMillis(): Long
}

@Singleton
class SystemTimeProvider @Inject constructor() : TimeProvider {
    override fun nowMillis(): Long = System.currentTimeMillis()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {
    @Binds
    abstract fun bindTimeProvider(impl: SystemTimeProvider): TimeProvider
}
