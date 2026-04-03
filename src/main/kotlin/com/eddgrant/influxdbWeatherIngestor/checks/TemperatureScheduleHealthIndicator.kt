package com.eddgrant.influxdbWeatherIngestor.checks

import com.eddgrant.InfluxDBWeatherIngestor
import io.micronaut.health.HealthStatus
import io.micronaut.management.health.indicator.HealthIndicator
import io.micronaut.management.health.indicator.HealthResult
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono

@Singleton
class TemperatureScheduleHealthIndicator(
    private val app: InfluxDBWeatherIngestor
) : HealthIndicator {

    override fun getResult(): Publisher<HealthResult> {
        val active = app.isSubscriptionActive()
        val status = if (active) HealthStatus.UP else HealthStatus.DOWN
        val details = mapOf("subscriptionActive" to active)
        return Mono.just(
            HealthResult.builder("temperatureSchedule", status)
                .details(details)
                .build()
        )
    }
}
