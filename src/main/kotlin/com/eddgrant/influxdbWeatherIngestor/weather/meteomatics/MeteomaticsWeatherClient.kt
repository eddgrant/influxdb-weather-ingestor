@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather.meteomatics

import com.eddgrant.influxdbWeatherIngestor.location.Location
import com.eddgrant.influxdbWeatherIngestor.weather.WeatherClient
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Singleton
@Requires(property = "weather.provider", value = "meteomatics", defaultValue = "meteomatics")
class MeteomaticsWeatherClient(private val meteomaticsClient: MeteomaticsClient) : WeatherClient {

    override fun getTemperatureByDateAndLocation(dateTime: Instant, location: Location): Double {
        val response = meteomaticsClient.getTemperatureByDateAndLocation(
            dateTime.toString(),
            location.latitude,
            location.longitude
        )

        val body = response.body()
            ?: throw IllegalStateException("Meteomatics API returned empty response body")

        val data = (body["data"] as? List<*>)?.firstOrNull() as? Map<*, *>
            ?: throw IllegalStateException("Meteomatics response missing 'data' array or first element")

        val coordinate = (data["coordinates"] as? List<*>)?.firstOrNull() as? Map<*, *>
            ?: throw IllegalStateException("Meteomatics response missing 'coordinates' array or first element")

        val date = (coordinate["dates"] as? List<*>)?.firstOrNull() as? Map<*, *>
            ?: throw IllegalStateException("Meteomatics response missing 'dates' array or first element")

        val value = date["value"]
            ?: throw IllegalStateException("Meteomatics response missing 'value' field")

        return when (value) {
            is Double -> value
            is Number -> value.toDouble()
            else -> throw IllegalStateException("Meteomatics response 'value' is not a number: $value")
        }
    }
}
