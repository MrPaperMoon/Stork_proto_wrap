package io.stork.client.util

import org.reactivestreams.Publisher
import org.reactivestreams.Subscription

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

fun <T> Signal<T>.toPublisher(): Publisher<T> = Publisher<T> { subscriber ->
    subscriber.onSubscribe(SignalSubscription(this@toPublisher.connect(subscriber::onNext)))
}

private class SignalSubscription(private val connection: Connection): Subscription {
    override fun cancel() {
        connection.dispose()
    }

    override fun request(n: Long) {
        // noop -- backpressure not supported
    }
}