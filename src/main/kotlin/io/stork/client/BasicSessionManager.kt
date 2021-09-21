package io.stork.client

import io.stork.client.util.SignalSource

class BasicSessionManager : SessionManager {
    override var sessionJwtToken: String? = null
        set(value) {
            field = value
            sessionTokenChangedSignal.emit(value)
        }

    override val sessionTokenChangedSignal: SignalSource<String?> = SignalSource()
}