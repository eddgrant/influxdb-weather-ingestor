package com.eddgrant

import com.eddgrant.influxdbWeatherIngestor.checks.TemperatureEmitter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import reactor.core.publisher.Flux

class InfluxDBWeatherIngestorSpec : StringSpec({

    "subscription remains active after the pipeline fails with a terminal error" {
        // The reactive pipeline in TemperatureEmitter has onErrorResume handlers that catch
        // expected errors (e.g. InfluxDB connection failures) per-interval and allow the
        // pipeline to continue. However, if an unexpected error escapes those handlers and
        // reaches the root subscription as a terminal error, we need to ensure the application
        // re-subscribes rather than letting the subscription die.
        //
        // If the subscription dies, isSubscriptionActive() returns false, causing the health
        // check to report DOWN. In a containerised environment this may trigger a new container
        // to be scheduled. If the error persists, this may lead to container thrashing.
        //
        // By using .retry() on the pipeline, terminal errors trigger an automatic re-subscription,
        // keeping the subscription active and the health check reporting UP.

        val temperatureEmitter = mockk<TemperatureEmitter>()
        var subscriptionCount = 0

        every { temperatureEmitter.getTemperatureData() } returns Flux.empty()
        // Flux.defer ensures each subscription evaluates fresh: the first subscription
        // emits a terminal error, the second stays alive indefinitely (simulating recovery).
        every { temperatureEmitter.publishTemperature(any()) } returns Flux.defer {
            subscriptionCount++
            if (subscriptionCount == 1) Flux.error(RuntimeException("Unexpected pipeline failure"))
            else Flux.never()
        }

        val ingestor = InfluxDBWeatherIngestor(temperatureEmitter)
        ingestor.start()

        // Verify that .retry() re-subscribed after the first failure.
        // This is a cumulative count, not concurrent — only the second subscription is active.
        subscriptionCount shouldBe 2
        // The subscription remains active, so the health check will report UP
        ingestor.isSubscriptionActive() shouldBe true
    }
})
