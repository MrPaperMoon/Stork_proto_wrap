package io.stork.client.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.repeat(): Flow<T> = flow {
    while (true) {
        emitAll(this@repeat)
    }
}