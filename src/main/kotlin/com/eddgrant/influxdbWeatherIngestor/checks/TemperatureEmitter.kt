@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.checks

import com.eddgrant.influxdbWeatherIngestor.location.PostcodesIoClient
import com.eddgrant.influxdbWeatherIngestor.persistence.influxdb.Temperature
import com.eddgrant.influxdbWeatherIngestor.weather.WeatherService
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import io.micronaut.http.HttpStatus
import jakarta.inject.Singleton
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

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
    fun emitTemperature() {
        val dateTime = Clock.System.now()

        val getLocationHttpResponse = postcodesIoClient.findLocationByPostcode(checkConfiguration.postcode)
        if(getLocationHttpResponse.status != HttpStatus.OK) {
            LOGGER.error("Could not determine location from postcode '{}'. Is it a valid postcode?", checkConfiguration.postcode)
            return
        }

        val location = getLocationHttpResponse.body()!!
        val temperature = weatherService.getTemperatureByDateAndLocation(
            dateTime,
            location
        )

        val temperatureMeasurement = Temperature(
            checkConfiguration.source,
            checkConfiguration.postcode,
            temperature,
            dateTime.toJavaInstant()
        )

        runBlocking {
            val job = launch {
                try {
                    influxDBClient.getWriteKotlinApi().writeMeasurement(temperatureMeasurement, WritePrecision.MS)
                } catch (e: Exception) {
                    LOGGER.error(e.message)
                    /*
                     * Propagate the exception back to the caller (who is outside the coroutine scope)
                     * so they can decide what they want to do.
                     */
                    throw e
                }
            }
            job.join()
            LOGGER.info("Temperature measurement sent: Postcode: ${checkConfiguration.postcode}, Temperature: $temperature")
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TemperatureEmitter::class.java)
    }

}