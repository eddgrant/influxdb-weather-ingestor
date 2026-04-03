package com.eddgrant.influxdbWeatherIngestor.checks

import io.micronaut.core.async.publisher.Publishers
import io.micronaut.health.HealthStatus
import io.micronaut.management.health.indicator.HealthIndicator
import io.micronaut.management.health.indicator.HealthResult
import jakarta.inject.Singleton
import org.reactivestreams.Publisher

@Singleton
class TemperatureScheduleHealthIndicator(
    private val registerChecksAction: RegisterChecksAction
) : HealthIndicator {

    override fun getResult(): Publisher<HealthResult> {
        val active = registerChecksAction.isScheduleActive()
        val status = if (active) HealthStatus.UP else HealthStatus.DOWN
        val details = mapOf("scheduleActive" to active)
        return Publishers.just(
            HealthResult.builder("temperatureSchedule", status)
                .details(details)
                .build()
        )
    }
}
