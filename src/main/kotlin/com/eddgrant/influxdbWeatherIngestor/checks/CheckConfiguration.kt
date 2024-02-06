package com.eddgrant.influxdbWeatherIngestor.checks

import io.micronaut.context.annotation.ConfigurationProperties
import jakarta.validation.constraints.NotBlank

@ConfigurationProperties("checks")
class CheckConfiguration {
    @NotBlank
    var scheduleExpression : String = "* * * * *"

    @NotBlank
    lateinit var postcode : String
}

