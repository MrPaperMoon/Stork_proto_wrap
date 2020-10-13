package io.stork.client.util

import java.util.concurrent.CopyOnWriteArrayList

class SignalSource<T> : Signal<T> {
    private val listeners: MutableList<Listener<T>> = CopyOnWriteArrayList()

    fun emit(value: T) {
        for (listener in listeners) {
            listener.invoke(value)
        }
    }

    override fun connect(listener: Listener<T>): Connection {
        listeners.add(listener)
        return ConnectionImpl(listener)
    }

    private inner class ConnectionImpl(val listener: Listener<T>): Connection {
        override fun dispose() {
            listeners.remove(listener)
        }
    }
}