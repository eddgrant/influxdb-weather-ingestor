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

        return ((((((response.body()
            ?.get("data") as List<*>).first() as Map<*, *>)
            .get("coordinates") as List<*>).first() as Map<*, *>)
            .get("dates") as List<*>
            ).first() as Map<*, *>)
            .get("value") as Double
    }
}
