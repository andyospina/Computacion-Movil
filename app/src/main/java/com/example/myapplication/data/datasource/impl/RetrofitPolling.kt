package com.example.myapplication.data.datasource.impl

import com.example.myapplication.data.ApiConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** El backend Express no envía cambios en tiempo real: se simulan consultando cada cierto tiempo. */
internal fun <T> pollingFlow(block: suspend () -> T): Flow<T> = flow {
    while (true) {
        emit(block())
        delay(ApiConfig.POLL_INTERVAL_MS)
    }
}
