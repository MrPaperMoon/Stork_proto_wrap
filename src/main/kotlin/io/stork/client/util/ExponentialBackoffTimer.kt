package io.stork.client.util

import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


// port of ExponentialBackOff from google-http-java-client
// https://code.google.com/p/google-http-java-client/wiki/ExponentialBackoff
@OptIn(ExperimentalTime::class)
class ExponentialBackOffTimer(val initialInterval: Int = DEFAULT_INITIAL_INTERVAL,
                              val randomizationFactor: Double = DEFAULT_RANDOMIZATION_FACTOR,
                              val multiplier: Double = DEFAULT_MULTIPLIER,
                              val maxInterval: Int = DEFAULT_MAX_INTERVAL
) : BackOffTimer {
    private val nextInterval = AtomicInteger(initialInterval)

    init {
        check(initialInterval > 0)
        check(0 <= randomizationFactor && randomizationFactor < 1)
        check(multiplier >= 1)
        check(maxInterval >= initialInterval)
    }

    fun getNextInterval(): Int {
        return nextInterval.get()
    }

    override fun reset() {
        nextInterval.set(initialInterval)
    }

    override fun hasNextTimeout(): Boolean {
        return true
    }

    override fun nextTimeout(): Duration {
        val currentTimeout = nextInterval.get()
        val randomizedInterval = calculateRandomizedInterval(randomizationFactor, Math.random(), currentTimeout)
        nextInterval.compareAndSet(currentTimeout, calculateNextInterval(currentTimeout, maxInterval, multiplier))
        return Duration.milliseconds(randomizedInterval)
    }

    override fun toString(): String {
        return "ExponentialBackOffTimer(" + getNextInterval() + ")"
    }

    companion object {
        // all units are milliseconds //
        const val DEFAULT_INITIAL_INTERVAL = 500
        const val DEFAULT_RANDOMIZATION_FACTOR = 0.5
        const val DEFAULT_MULTIPLIER = 1.5
        const val DEFAULT_MAX_INTERVAL = 60000

        fun calculateRandomizedInterval(randomizationFactor: Double,
                                        random: Double,
                                        currentIntervalMillis: Int): Int {
            val delta = randomizationFactor * currentIntervalMillis
            val minInterval = currentIntervalMillis - delta
            val maxInterval = currentIntervalMillis + delta
            return getRandomIntervalBetween(minInterval, maxInterval, random)
        }

        // Get a random value from the range [minInterval, maxInterval].
        // The formula used below has a +1 because if the minInterval is 1 and the maxInterval is 3 then
        // we want a 33% chance for selecting either 1, 2 or 3.
        fun getRandomIntervalBetween(minInterval: Double,
                                     maxInterval: Double,
                                     random: Double): Int {
            return (minInterval + random * (maxInterval - minInterval + 1)).toInt()
        }

        // Check for overflow, if overflow is detected set the current interval to the max interval.
        private fun calculateNextInterval(currentInterval: Int,
                                          maxInterval: Int,
                                          multiplier: Double): Int {
            return if (currentInterval >= maxInterval / multiplier) {
                maxInterval
            } else {
                (currentInterval * multiplier).toInt()
            }
        }
    }
}