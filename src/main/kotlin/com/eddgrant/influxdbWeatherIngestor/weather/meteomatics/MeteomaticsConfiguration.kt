package com.eddgrant.influxdbWeatherIngestor.weather.meteomatics

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires
import jakarta.validation.constraints.NotBlank

@ConfigurationProperties("meteomatics")
@Requires(property = "weather.provider", value = "meteomatics", defaultValue = "meteomatics")
class MeteomaticsConfiguration {

    @NotBlank
    lateinit var username : String

    @NotBlank
    lateinit var password : String
}