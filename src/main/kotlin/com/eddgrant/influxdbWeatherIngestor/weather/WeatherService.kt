@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather

import com.eddgrant.influxdbWeatherIngestor.location.Location
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Singleton
class WeatherService(
    private val weatherClient: WeatherClient,
    @param:Value($$"${weather.provider}") private val provider: String
) {

    init {
        // Log which weather provider has been configured
        LOGGER.info("Weather provider configured: {} (client bean: {})", provider, weatherClient::class.simpleName)
    }

    fun getTemperatureByDateAndLocation(dateTime: Instant, location: Location) : Double {
        val temperature = weatherClient.getTemperatureByDateAndLocation(dateTime, location)
        LOGGER.debug("Temperature for location: {} at date/time: {} is {}", location, dateTime, temperature)
        return temperature
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WeatherService::class.java)
    }
}