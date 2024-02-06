package com.eddgrant.influxdbWeatherIngestor.checks

import com.eddgrant.influxdbWeatherIngestor.location.PostcodesIoClient
import com.eddgrant.influxdbWeatherIngestor.persistence.influxdb.Temperature
import com.eddgrant.influxdbWeatherIngestor.weather.WeatherService
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import jakarta.inject.Singleton
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.slf4j.LoggerFactory

/**
 * Scheduled Task
 */
@Singleton
//@RequiresCheckConfiguration
class TemperatureEmitter(
    private val checkConfiguration: CheckConfiguration,
    private val postcodesIoClient: PostcodesIoClient,
    private val weatherService: WeatherService,
    private val influxDBClient: InfluxDBClientKotlin
) {
    suspend fun emitTemperature() {
        LOGGER.info("I am in emitTemperature()")
        val dateTime = Clock.System.now()

        val temperature = weatherService.getTemperatureByDateAndLocation(
            dateTime,
            postcodesIoClient.findLocationByPostcode(checkConfiguration.postcode)
        )

        LOGGER.info("Temperature is: $temperature")

        val temperatureMeasurement = Temperature(
            checkConfiguration.postcode,
            temperature,
            dateTime.toJavaInstant()
        )

        LOGGER.info("Point object created")
        influxDBClient.getWriteKotlinApi().writeMeasurement(temperatureMeasurement, WritePrecision.MS)

        // TODO: Find out why we're never getting to this line... Is it a timeout? Something else?
        LOGGER.info("Temperature measurement sent: Postcode: ${checkConfiguration.postcode}, Temperature: $temperature")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TemperatureEmitter::class.java)
    }

}