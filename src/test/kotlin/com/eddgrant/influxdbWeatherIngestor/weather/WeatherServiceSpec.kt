@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather

import com.eddgrant.influxdbWeatherIngestor.location.Location
import com.eddgrant.influxdbWeatherIngestor.weather.meteomatics.MeteomaticsClient
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

val location = Location("my-lat", "my-long")
val now = Clock.System.now()

@MicronautTest
@MockKExtension.ConfirmVerification
class WeatherServiceSpec(
    private val underTest: WeatherService
) : BehaviorSpec({
   given("it returns the temperature from the Meteomatics API response") {
       val expectedTemperature = 15.6

       `when`("it is called") {
           val returnedTemperature = underTest.getTemperatureByDateAndLocation(now, location)
           then("The expected temperature is returned") {
               expectedTemperature shouldBe returnedTemperature
           }
       }
   }
}) {
    @MockBean(MeteomaticsClient::class)
    fun meteomaticsClient(): MeteomaticsClient {
        val expectedTemperature = 15.6
        val expectedResponseBody = mapOf<String, Any>(
            "data" to listOf<Any>(
                mapOf<Any, Any>(
                    "coordinates" to listOf<Any>(
                        mapOf<Any, Any>(
                            "dates" to listOf<Any>(
                                mapOf<Any, Any>(
                                    "value" to expectedTemperature
                                )
                            )
                        )
                    )
                )
            )
        )
        val mock = mockk<MeteomaticsClient>()
        every {
            mock.getTemperatureByDateAndLocation(
                now.toString(),
                location.latitude,
                location.longitude
            )
        } returns HttpResponse.ok(expectedResponseBody)
        return mock
    }
}
