package io.stork.client.util

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

fun <T> Flow<T>.repeat(): Flow<T> = flow {
    while (true) {
        emitAll(this@repeat)
    }
}

suspend fun <T> Flow<T>.safeFirst(): T {
    val stacktrace = RuntimeException("Flow.first() called here")
    try {
        return first()
    } catch (ex: Exception) {
        stacktrace.addSuppressed(ex)
        throw stacktrace
    }
}

fun CoroutineScope.launchWithStacktrace(work: suspend () -> Unit): Job {
    val stacktrace = RuntimeException("CoroutineScope.launch() called here")
    return launch {
        try {
            work()
        } catch (ex: Exception) {
            stacktrace.addSuppressed(ex)
            throw ex
        }
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

fun CoroutineScope.beforeCancel(block: suspend () -> Unit) {
    launch {
        try {
            awaitCancellation()
        } finally {
            runIgnoreAnyResult(block)
        }
    }
}


suspend fun runIgnoreAnyResult(block: suspend () -> Unit) {
    withContext(NonCancellable) {
        try {
            block()
        } catch (e: Throwable) {

        }
    }
}

fun <T, U> Flow<T>.takeWhile(otherFlow: Flow<U>, predicate: (U) -> Boolean): Flow<T> {
    return combineTransform(otherFlow) { thisItem, otherItem ->
        if (predicate(otherItem)) {
            emit(Either.Left(thisItem))
        } else {
            emit(Either.Right(otherItem))
        }
    }.takeWhile { it is Either.Left }.map { (it as Either.Left).value }
}

fun <T, U> Flow<T>.takeUntil(otherFlow: Flow<U>, predicate: (U) -> Boolean): Flow<T> {
    return combineTransform(otherFlow) { thisItem, otherItem ->
        if (predicate(otherItem)) {
            emit(Either.Right(otherItem))
        } else {
            emit(Either.Left(thisItem))
        }
    }.takeWhile { it is Either.Left }.map { (it as Either.Left).value }
}