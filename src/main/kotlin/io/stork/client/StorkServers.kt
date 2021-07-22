package io.stork.client

object StorkServers {
    val dev = StorkServer("dev.stork.io", StorkServer.Kind.Dev)
    val staging = StorkServer("stork.io", StorkServer.Kind.Staging)
    val production = StorkServer("stork.ai", StorkServer.Kind.Production)

    val knownServers = listOf(dev, staging, production)
}