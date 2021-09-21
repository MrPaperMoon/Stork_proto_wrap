package io.stork.client.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

interface Signal<T> {
    fun connect(listener: Listener<T>): Connection
}

fun <T, U> Signal<T>.map(mapper: (T) -> U): Signal<U> = object: Signal<U> {
    override fun connect(listener: Listener<U>): Connection = this@map.connect {
        listener.invoke(mapper(it))
    }
}

fun <T> Signal<T>.filter(filter: (T) -> Boolean): Signal<T> = object: Signal<T> {
    override fun connect(listener: Listener<T>): Connection = this@filter.connect {
        if (filter(it)) {
            listener.invoke(it)
        }
    }
}

fun <T> Flow<T>.toSignal(scope: CoroutineScope): Signal<T> = object: Signal<T> {
    override fun connect(listener: Listener<T>): Connection {
        return JobConnection(scope.launch {
            collect {
                listener(it)
            }
        })
    }
}

class JobConnection(private val job: Job): Connection {
    override fun dispose() {
        job.cancel()
    }
}

fun <T> Signal<T>.toFlow(): Flow<T> = callbackFlow<T> {
    val listener = object: Listener<T> {
        override fun invoke(p1: T) {
            sendBlocking(p1)
        }
    }

    val connection = connect(listener)
    awaitClose {
        connection.dispose()
    }
}