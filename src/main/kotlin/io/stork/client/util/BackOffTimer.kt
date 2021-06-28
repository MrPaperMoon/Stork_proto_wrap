package io.stork.client.util

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface BackOffTimer {
    fun hasNextTimeout(): Boolean
    fun nextTimeout(): Duration
    fun reset()
}