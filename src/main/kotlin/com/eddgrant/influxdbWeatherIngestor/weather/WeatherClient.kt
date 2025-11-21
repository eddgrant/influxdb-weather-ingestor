@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather

import com.eddgrant.influxdbWeatherIngestor.location.Location
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Abstraction for obtaining temperature data from a weather provider.
 */
interface WeatherClient {
    fun getTemperatureByDateAndLocation(dateTime: Instant, location: Location): Double
}
