package com.eddgrant.influxdbWeatherIngestor.weather.weatherapi

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires
import jakarta.validation.constraints.NotBlank

@ConfigurationProperties("weatherapi")
@Requires(property = "weather.provider", value = "weatherapi.com")
class WeatherApiConfiguration {

    @NotBlank
    lateinit var apiKey: String
}
