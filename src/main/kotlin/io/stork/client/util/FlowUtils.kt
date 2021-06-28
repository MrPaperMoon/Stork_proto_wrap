package io.stork.client.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> Flow<T>.repeat(): Flow<T> = flow {
    while (true) {
        emitAll(this@repeat)
    }
}

fun <T> CoroutineScope.launchCatching(context: CoroutineContext = EmptyCoroutineContext,
                                      coroutine: suspend CoroutineScope.() -> T): Job {
    return launch(context) {
        try {
            coroutine(this)
        } catch (ex: Throwable) {
            LoggerFactory.getLogger(this.toString()).error("launchCatching failure: ", ex)
        }
    }
}