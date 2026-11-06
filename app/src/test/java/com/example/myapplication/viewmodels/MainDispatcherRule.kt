package com.example.myapplication.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * `viewModelScope` usa `Dispatchers.Main`, que no existe en una prueba JVM. Esta regla lo reemplaza por
 * un dispatcher de prueba "unconfined": las corrutinas del ViewModel corren al instante, sin esperas.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}
