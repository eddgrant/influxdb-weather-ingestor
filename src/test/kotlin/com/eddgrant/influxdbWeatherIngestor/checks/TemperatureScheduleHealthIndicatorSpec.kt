package com.eddgrant.influxdbWeatherIngestor.checks

import com.eddgrant.InfluxDBWeatherIngestor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.micronaut.health.HealthStatus
import io.micronaut.management.health.indicator.HealthResult
import io.mockk.every
import io.mockk.mockk
import reactor.test.StepVerifier

class TemperatureScheduleHealthIndicatorSpec : StringSpec({

    "it reports UP when the subscription is active" {
        val app = mockk<InfluxDBWeatherIngestor>()
        every { app.isSubscriptionActive() } returns true

        val indicator = TemperatureScheduleHealthIndicator(app)

        StepVerifier.create(indicator.result)
            .assertNext { result ->
                result.status shouldBe HealthStatus.UP
                @Suppress("UNCHECKED_CAST")
                val details = result.details as Map<String, Any>
                details["subscriptionActive"] shouldBe true
            }
            .verifyComplete()
    }

    "it reports DOWN when the subscription is not active" {
        val app = mockk<InfluxDBWeatherIngestor>()
        every { app.isSubscriptionActive() } returns false

        val indicator = TemperatureScheduleHealthIndicator(app)

        StepVerifier.create(indicator.result)
            .assertNext { result ->
                result.status shouldBe HealthStatus.DOWN
                @Suppress("UNCHECKED_CAST")
                val details = result.details as Map<String, Any>
                details["subscriptionActive"] shouldBe false
            }
            .verifyComplete()
    }
})
