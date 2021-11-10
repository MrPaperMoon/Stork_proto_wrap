package io.stork.client.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

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