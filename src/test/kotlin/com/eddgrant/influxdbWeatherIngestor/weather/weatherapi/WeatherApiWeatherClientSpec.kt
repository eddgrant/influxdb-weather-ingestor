@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather.weatherapi

import com.eddgrant.influxdbWeatherIngestor.location.Location
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpResponse
import io.mockk.every
import io.mockk.mockk
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class WeatherApiWeatherClientSpec : FunSpec({

    context("it parses temp_c from WeatherAPI current weather response") {
        val mockHttpClient = mockk<WeatherApiDotComClient>()
        val config = WeatherApiConfiguration().apply { apiKey = "test-api-key" }
        val underTest = WeatherApiWeatherClient(mockHttpClient, config)

        val location = Location("51.427195", "-0.108248")
        val now = Clock.System.now()
        val expectedTemp = 12.3

        val responseBody: Map<String, Any> = mapOf(
            "current" to mapOf(
                "temp_c" to expectedTemp
            )
        )

        every { mockHttpClient.getCurrent(config.apiKey, "${location.latitude},${location.longitude}") } returns HttpResponse.ok(responseBody)

        test("returns the expected temperature as a Double") {
            val temp = underTest.getTemperatureByDateAndLocation(now, location)
            temp shouldBe expectedTemp
        }
    }
})
