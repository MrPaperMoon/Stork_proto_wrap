package io.stork.client.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

fun suspendTest(block: suspend CoroutineScope.() -> Unit): Unit {
    runBlocking(block = block)
}