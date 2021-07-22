package io.stork.client

data class StorkServer(val address: String, val kind: Kind) {
    enum class Kind {
        Dev,
        Staging,
        Production
    }
}
