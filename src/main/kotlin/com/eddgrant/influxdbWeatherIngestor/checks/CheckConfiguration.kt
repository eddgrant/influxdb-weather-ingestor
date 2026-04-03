package com.eddgrant.influxdbWeatherIngestor.checks

import io.micronaut.context.annotation.ConfigurationProperties
import jakarta.validation.constraints.NotBlank
import java.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@ConfigurationProperties("checks")
class CheckConfiguration {

    @NotBlank
    val source: String = DEFAULT_SOURCE

    var checkInterval: Duration = DEFAULT_CHECK_INTERVAL

    @NotBlank
    lateinit var postcode: String

    companion object {
        const val DEFAULT_SOURCE = "influxdb-weather-ingestor"
        val DEFAULT_CHECK_INTERVAL: Duration = 1.minutes.toJavaDuration()
    }
}
