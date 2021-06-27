package io.stork.client.util

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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