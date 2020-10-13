package io.stork.client

import io.stork.client.util.SignalSource

internal class SessionManagerImpl : SessionManager {
    override var sessionJwtToken: String? = null
        set(value) {
            field = value
            sessionTokenChangedSignal.emit(value)
        }

    override val sessionTokenChangedSignal: SignalSource<String?> = SignalSource()
}