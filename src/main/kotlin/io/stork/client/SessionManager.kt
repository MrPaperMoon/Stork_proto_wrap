package io.stork.client

import io.stork.client.util.Signal

interface SessionManager {
    var sessionJwtToken: String?
    val sessionTokenChangedSignal: Signal<String?>
}