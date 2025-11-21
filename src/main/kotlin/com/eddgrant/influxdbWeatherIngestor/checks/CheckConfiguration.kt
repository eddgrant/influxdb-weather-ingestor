package com.eddgrant.influxdbWeatherIngestor.checks

import io.micronaut.context.annotation.ConfigurationProperties
import jakarta.validation.constraints.NotBlank

@ConfigurationProperties("checks")
class CheckConfiguration {

    @NotBlank
    val source: String = DEFAULT_SOURCE

    @NotBlank
    var scheduleExpression : String = DEFAULT_SCHEDULE_EXPRESSION

    @NotBlank
    lateinit var postcode : String

    companion object {
        const val DEFAULT_SOURCE = "influxdb-weather-ingestor"
        const val DEFAULT_SCHEDULE_EXPRESSION = "* * * * *"
    }
}

