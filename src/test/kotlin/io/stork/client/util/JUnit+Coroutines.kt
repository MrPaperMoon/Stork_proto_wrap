package io.stork.client.util

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

@OptIn(ExperimentalTime::class)
fun suspendTest(timeout: Duration = Duration.minutes(1),
                block: suspend CoroutineScope.() -> Unit): Unit {
    runBlocking {
        withTimeout(timeout) {
            block()
        }
    }
}