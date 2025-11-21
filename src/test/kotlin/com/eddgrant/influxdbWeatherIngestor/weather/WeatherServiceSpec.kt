@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather

import com.eddgrant.influxdbWeatherIngestor.location.Location
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
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
   given("it returns the temperature from the WeatherClient") {
       val expectedTemperature = 15.6

       `when`("it is called") {
           val returnedTemperature = underTest.getTemperatureByDateAndLocation(now, location)
           then("The expected temperature is returned") {
               expectedTemperature shouldBe returnedTemperature
           }
       }
   }
}) {
    @MockBean(WeatherClient::class)
    fun weatherClient(): WeatherClient {
        val mock = mockk<WeatherClient>()
        every { mock.getTemperatureByDateAndLocation(now, location) } returns 15.6
        return mock
    }
}
