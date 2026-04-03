package com.eddgrant.influxdbWeatherIngestor.checks

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.micronaut.health.HealthStatus
import io.micronaut.management.health.indicator.HealthResult
import io.mockk.every
import io.mockk.mockk
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.CompletableFuture

private fun Publisher<HealthResult>.blockFirst(): HealthResult {
    val future = CompletableFuture<HealthResult>()
    subscribe(object : Subscriber<HealthResult> {
        override fun onSubscribe(s: Subscription) = s.request(1)
        override fun onNext(t: HealthResult) = future.complete(t).let {}
        override fun onError(t: Throwable) = future.completeExceptionally(t).let {}
        override fun onComplete() {}
    })
    return future.get()
}

class TemperatureScheduleHealthIndicatorSpec : StringSpec({

    "it reports UP when the schedule is active" {
        val registerChecksAction = mockk<RegisterChecksAction>()
        every { registerChecksAction.isScheduleActive() } returns true

        val indicator = TemperatureScheduleHealthIndicator(registerChecksAction)
        val result = indicator.result.blockFirst()

        result.status shouldBe HealthStatus.UP
        @Suppress("UNCHECKED_CAST")
        val details = result.details as Map<String, Any>
        details["scheduleActive"] shouldBe true
    }

    "it reports DOWN when the schedule is not active" {
        val registerChecksAction = mockk<RegisterChecksAction>()
        every { registerChecksAction.isScheduleActive() } returns false

        val indicator = TemperatureScheduleHealthIndicator(registerChecksAction)
        val result = indicator.result.blockFirst()

        result.status shouldBe HealthStatus.DOWN
        @Suppress("UNCHECKED_CAST")
        val details = result.details as Map<String, Any>
        details["scheduleActive"] shouldBe false
    }
})
