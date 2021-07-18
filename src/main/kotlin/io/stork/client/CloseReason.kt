package io.stork.client

sealed interface CloseReason {
    object GracefulClose: CloseReason {
        override fun toString(): String = GracefulClose::class.simpleName ?: ""
    }
    data class ExceptionalClose(val cause: Throwable): CloseReason
}