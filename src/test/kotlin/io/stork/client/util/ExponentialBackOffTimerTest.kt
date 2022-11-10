package io.stork.client.util

import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTime::class)
class ExponentialBackoffBackOffTimerTest {
    @Test
    fun testConstructor() {
        val backOffPolicy = ExponentialBackOffTimer()
        assertEquals(ExponentialBackOffTimer.DEFAULT_INITIAL_INTERVAL, backOffPolicy.initialInterval)
        assertEquals(ExponentialBackOffTimer.DEFAULT_INITIAL_INTERVAL, backOffPolicy.getNextInterval())
        assertEquals(ExponentialBackOffTimer.DEFAULT_RANDOMIZATION_FACTOR, backOffPolicy.randomizationFactor, 0.01)
        assertEquals(ExponentialBackOffTimer.DEFAULT_MULTIPLIER, backOffPolicy.multiplier, 0.01)
        assertEquals(ExponentialBackOffTimer.DEFAULT_MAX_INTERVAL, backOffPolicy.maxInterval)
    }

    @Test
    fun testBuilder() {
        var backOffPolicy = ExponentialBackOffTimer()
        assertEquals(ExponentialBackOffTimer.DEFAULT_INITIAL_INTERVAL, backOffPolicy.initialInterval)
        assertEquals(ExponentialBackOffTimer.DEFAULT_INITIAL_INTERVAL, backOffPolicy.getNextInterval())
        assertEquals(ExponentialBackOffTimer.DEFAULT_RANDOMIZATION_FACTOR, backOffPolicy.randomizationFactor, 0.01)
        assertEquals(ExponentialBackOffTimer.DEFAULT_MULTIPLIER, backOffPolicy.multiplier, 0.01)
        assertEquals(ExponentialBackOffTimer.DEFAULT_MAX_INTERVAL, backOffPolicy.maxInterval)

        val testInitialInterval = 1
        val testRandomizationFactor = 0.1
        val testMultiplier = 5.0
        val testMaxInterval = 10

        backOffPolicy = ExponentialBackOffTimer(
            initialInterval = testInitialInterval,
            randomizationFactor = testRandomizationFactor,
            multiplier = testMultiplier,
            maxInterval = testMaxInterval
        )
        assertEquals(testInitialInterval, backOffPolicy.initialInterval)
        assertEquals(testInitialInterval, backOffPolicy.getNextInterval())
        assertEquals(testRandomizationFactor, backOffPolicy.randomizationFactor, 0.01)
        assertEquals(testMultiplier, backOffPolicy.multiplier, 0.01)
        assertEquals(testMaxInterval, backOffPolicy.maxInterval)
    }

    @Test
    fun testBackOff() {
        val testInitialInterval = 500
        val testRandomizationFactor = 0.1
        val testMultiplier = 2.0
        val testMaxInterval = 5000
        val testMaxElapsedTime = 900000

        val backOffPolicy = ExponentialBackOffTimer(
            initialInterval = testInitialInterval,
            randomizationFactor = testRandomizationFactor,
            multiplier = testMultiplier,
            maxInterval = testMaxInterval
        )
        val expectedResults = intArrayOf(500, 1000, 2000, 4000, 5000, 5000, 5000, 5000, 5000, 5000)
        for (expected in expectedResults) {
            assertEquals(expected, backOffPolicy.getNextInterval())
            // Assert that the next back off falls in the expected range.
            val minInterval = (expected - testRandomizationFactor * expected).toInt()
            val maxInterval = (expected + testRandomizationFactor * expected).toInt()
            val actualInterval = backOffPolicy.nextTimeout().toInt(DurationUnit.MILLISECONDS)
            actualInterval shouldBeInRange (minInterval .. maxInterval)
        }
    }

    @Test
    fun testCalculateRandomizedInterval() {
        fun calcInterval(random: Double) =
            ExponentialBackOffTimer.calculateRandomizedInterval(0.5, random, 2)

        // 33% chance of being 1.
        calcInterval(0.0).shouldBe(1)
        calcInterval(0.33).shouldBe(1)
        // 33% chance of being 2.
        calcInterval(0.34).shouldBe(2)
        calcInterval(0.66).shouldBe(2)
        // 33% chance of being 3.
        calcInterval(0.67).shouldBe(3)
        calcInterval(0.99).shouldBe(3)
    }
}
