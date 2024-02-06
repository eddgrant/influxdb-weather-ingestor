package com.eddgrant.influxdbWeatherIngestor.weather.meteomatics

import io.micronaut.context.annotation.ConfigurationProperties
import jakarta.validation.constraints.NotBlank

@ConfigurationProperties("meteomatics")
class MeteomaticsConfiguration {

    @NotBlank
    lateinit var username : String

    @NotBlank
    lateinit var password : String
}