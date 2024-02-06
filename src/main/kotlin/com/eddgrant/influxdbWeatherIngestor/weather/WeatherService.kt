package com.eddgrant.influxdbWeatherIngestor.weather

import com.eddgrant.influxdbWeatherIngestor.location.Location
import com.eddgrant.influxdbWeatherIngestor.weather.meteomatics.MeteomaticsClient
import jakarta.inject.Singleton
import kotlinx.datetime.Instant

@Singleton
class WeatherService(private val meteomaticsClient: MeteomaticsClient) {

    fun getTemperatureByDateAndLocation(dateTime: Instant, location: Location) : Double {
        val response = meteomaticsClient.getTemperatureByDateAndLocation(
            dateTime.toString(),
            location.latitude,
            location.longitude)

        return ((((((response.body()
            ?.get("data") as List<*>).first() as Map<*, *>)
            .get("coordinates") as List<*>).first() as Map<*,*>)
            .get("dates") as List<*>).first() as Map<*,*>)
            .get("value") as Double
    }
}