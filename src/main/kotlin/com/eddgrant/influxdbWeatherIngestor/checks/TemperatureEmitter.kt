@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.checks

import com.eddgrant.influxdbWeatherIngestor.location.PostcodesIoClient
import com.eddgrant.influxdbWeatherIngestor.persistence.influxdb.Temperature
import com.eddgrant.influxdbWeatherIngestor.weather.WeatherService
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpStatus
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@Singleton
class TemperatureEmitter(
    private val checkConfiguration: CheckConfiguration,
    private val postcodesIoClient: PostcodesIoClient,
    private val weatherService: WeatherService,
    private val influxDBClient: InfluxDBClientKotlin,
    @param:Value("\${weather.provider}") private val provider: String
) {

    fun getTemperatureData(): Flux<Temperature> {
        return Flux.interval(checkConfiguration.checkInterval)
            .concatMap {
                mono(Dispatchers.IO) {
                    LOGGER.info("Checking the temperature")
                    val dateTime = Clock.System.now()

                    val response = postcodesIoClient.findLocationByPostcode(checkConfiguration.postcode)
                    if (response.status != HttpStatus.OK) {
                        throw RuntimeException("Could not determine location from postcode '${checkConfiguration.postcode}'. Is it a valid postcode?")
                    }

                    val location = response.body()!!
                    val temperature = weatherService.getTemperatureByDateAndLocation(dateTime, location)

                    Temperature(
                        source = checkConfiguration.source,
                        postcode = checkConfiguration.postcode,
                        provider = provider,
                        value = temperature,
                        time = dateTime.toJavaInstant()
                    )
                }
                .onErrorResume { e ->
                    LOGGER.error("Temperature check failed. Will retry on next interval.", e)
                    Mono.empty()
                }
            }
    }

    fun publishTemperature(temperatureData: Flux<Temperature>): Flux<Void> {
        return temperatureData
            .concatMap { measurement ->
                mono(Dispatchers.IO) {
                    influxDBClient.getWriteKotlinApi()
                        .writeMeasurement(measurement, WritePrecision.MS)
                    LOGGER.info("Temperature measurement sent: Postcode: ${measurement.postcode}, Temperature: ${measurement.value}")
                    LOGGER.debug("Measurement data: {}", measurement)
                }
                .then(Mono.empty<Void>())
                .onErrorResume { e ->
                    LOGGER.error("Failed to write temperature measurement to InfluxDB. Will retry on next interval.", e)
                    Mono.empty()
                }
            }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TemperatureEmitter::class.java)
    }
}
