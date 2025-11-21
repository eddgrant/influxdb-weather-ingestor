@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.checks

import com.eddgrant.influxdbWeatherIngestor.location.Location
import com.eddgrant.influxdbWeatherIngestor.location.PostcodesIoClient
import com.eddgrant.influxdbWeatherIngestor.persistence.influxdb.Temperature
import com.eddgrant.influxdbWeatherIngestor.weather.WeatherService
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.kotest5.MicronautKotest5Extension.getMock
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import io.mockk.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@MicronautTest
class TemperatureEmitterSpec(
    private val temperatureEmitter: TemperatureEmitter,
    private val postcodesIoClient: PostcodesIoClient,
    private val weatherService: WeatherService,
    private val influxDBClient: InfluxDBClientKotlin,
    private val checkConfiguration: CheckConfiguration,
    @param:Value($$"${weather.provider}") private val provider: String
) : BehaviorSpec({

    val postCode = "AB12 3CD"
    val temperature = 10.0
    val location = Location("my-lat", "my-long")

    given("The InfluxDB client is unable to write to InfluxDB") {
        val now = Clock.System.now()
        mockkObject(Clock.System)
        every { Clock.System.now() } returns now

        val temperatureMeasurement = Temperature(
            checkConfiguration.source,
            postCode,
            provider,
            temperature,
            now.toJavaInstant()
        )

        `when`("we attempt to emit the Temperature measurement") {

            val postcodesIoClientMock = getMock(postcodesIoClient)
            val weatherServiceMock = getMock(weatherService)
            val influxDBClientMock = getMock(influxDBClient)

            every { postcodesIoClientMock.findLocationByPostcode(checkConfiguration.postcode) } returns HttpResponse.ok(location)
            every { weatherServiceMock.getTemperatureByDateAndLocation(now, location) } returns temperature
            coEvery {
                influxDBClientMock.getWriteKotlinApi().writeMeasurement(temperatureMeasurement, WritePrecision.MS)
            } throws RuntimeException("Unable to write measurement.")

            val exception = shouldThrow<RuntimeException> {
                temperatureEmitter.emitTemperature()
            }

            then("it should raise any exception thrown by the InfluxDB client") {
                exception.message shouldBe "Unable to write measurement."

                verify {
                    postcodesIoClientMock.findLocationByPostcode(postCode)
                    weatherServiceMock.getTemperatureByDateAndLocation(now, location)
                    influxDBClientMock.getWriteKotlinApi()
                }

                coVerify {
                    influxDBClientMock.getWriteKotlinApi().writeMeasurement(temperatureMeasurement, WritePrecision.MS)
                }
            }
        }
    }
}) {
    @MockBean(WeatherService::class)
    fun weatherService() : WeatherService = mockk<WeatherService>()

    @MockBean(InfluxDBClientKotlin::class)
    fun influxDBClient(): InfluxDBClientKotlin = mockk<InfluxDBClientKotlin>()

    @MockBean(PostcodesIoClient::class)
    fun postcodesIoClient(): PostcodesIoClient = mockk<PostcodesIoClient>()
}
