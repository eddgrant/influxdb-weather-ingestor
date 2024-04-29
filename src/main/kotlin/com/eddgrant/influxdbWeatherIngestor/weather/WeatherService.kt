package com.eddgrant.influxdbWeatherIngestor.weather

import com.eddgrant.influxdbWeatherIngestor.location.Location
import com.eddgrant.influxdbWeatherIngestor.weather.meteomatics.MeteomaticsClient
import jakarta.inject.Singleton
import kotlinx.datetime.Instant
import org.slf4j.LoggerFactory

@Singleton
class WeatherService(private val meteomaticsClient: MeteomaticsClient) {

    fun getTemperatureByDateAndLocation(dateTime: Instant, location: Location) : Double {
        val response = meteomaticsClient.getTemperatureByDateAndLocation(
            dateTime.toString(),
            location.latitude,
            location.longitude)

        val temperature = ((((((response.body()
            ?.get("data") as List<*>).first() as Map<*, *>)
            .get("coordinates") as List<*>).first() as Map<*, *>)
            .get("dates") as List<*>).first() as Map<*, *>)
            .get("value") as Double
        LOGGER.debug("Temperature for location: {} at date/time: {} is {}", location, dateTime, temperature)
        return temperature
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WeatherService::class.java)
    }
}