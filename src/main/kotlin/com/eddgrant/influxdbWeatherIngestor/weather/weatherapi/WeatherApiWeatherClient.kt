@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather.weatherapi

import com.eddgrant.influxdbWeatherIngestor.location.Location
import com.eddgrant.influxdbWeatherIngestor.weather.WeatherClient
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Singleton
@Requires(property = "weather.provider", value = "weatherapi.com")
class WeatherApiWeatherClient(
    private val client: WeatherApiDotComClient,
    private val config: WeatherApiConfiguration
) : WeatherClient {

    override fun getTemperatureByDateAndLocation(dateTime: Instant, location: Location): Double {
        // WeatherAPI free tier supports current weather by location; ignore dateTime.
        val query = "${location.latitude},${location.longitude}"
        val response = client.getCurrent(config.apiKey, query)
        val body = response.body() ?: emptyMap()
        val current = body["current"] as Map<*, *>
        val tempC = current["temp_c"]
        return when (tempC) {
            is Double -> tempC
            is Float -> tempC.toDouble()
            is Int -> tempC.toDouble()
            is Number -> tempC.toDouble()
            else -> throw IllegalStateException("Unexpected response from weatherapi.com: temp_c missing or invalid")
        }
    }
}
