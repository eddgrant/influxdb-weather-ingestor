@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.checks

import com.eddgrant.influxdbWeatherIngestor.location.PostcodesIoClient
import com.eddgrant.influxdbWeatherIngestor.persistence.influxdb.Temperature
import com.eddgrant.influxdbWeatherIngestor.weather.WeatherService
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import io.kotest.core.spec.style.BehaviorSpec
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.kotest5.MicronautKotest5Extension.getMock
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import io.mockk.*
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@MicronautTest
@Property(name = "checks.check-interval", value = "100ms")
class TemperatureEmitterSpec(
    private val temperatureEmitter: TemperatureEmitter,
    private val influxDBClient: InfluxDBClientKotlin
) : BehaviorSpec({

    given("the publish pipeline") {
        val influxDBClientMock = getMock(influxDBClient)

        `when`("publishTemperature receives a measurement") {
            val measurement = Temperature(
                source = "test",
                postcode = "AB12 3CD",
                provider = "test-provider",
                value = 15.0,
                time = Clock.System.now().toJavaInstant()
            )

            coEvery {
                influxDBClientMock.getWriteKotlinApi().writeMeasurement(measurement, WritePrecision.MS)
            } returns Unit

            then("it writes to InfluxDB") {
                StepVerifier.create(temperatureEmitter.publishTemperature(Flux.just(measurement)))
                    .verifyComplete()
            }
        }

        `when`("the InfluxDB write fails") {
            val measurement = Temperature(
                source = "test",
                postcode = "AB12 3CD",
                provider = "test-provider",
                value = 15.0,
                time = Clock.System.now().toJavaInstant()
            )

            coEvery {
                influxDBClientMock.getWriteKotlinApi().writeMeasurement(measurement, WritePrecision.MS)
            } throws RuntimeException("Unable to write measurement.")

            then("it swallows the error and completes") {
                StepVerifier.create(temperatureEmitter.publishTemperature(Flux.just(measurement)))
                    .verifyComplete()
            }
        }
    }
}) {
    @MockBean(WeatherService::class)
    fun weatherService(): WeatherService = mockk<WeatherService>()

    @MockBean(InfluxDBClientKotlin::class)
    fun influxDBClient(): InfluxDBClientKotlin = mockk<InfluxDBClientKotlin>(relaxed = true)

    @MockBean(PostcodesIoClient::class)
    fun postcodesIoClient(): PostcodesIoClient = mockk<PostcodesIoClient>()
}
