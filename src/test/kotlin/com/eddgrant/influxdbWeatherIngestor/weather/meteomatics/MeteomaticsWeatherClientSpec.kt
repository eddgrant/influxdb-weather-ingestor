@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather.meteomatics

import com.eddgrant.influxdbWeatherIngestor.location.Location
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpResponse
import io.mockk.every
import io.mockk.mockk
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MeteomaticsWeatherClientSpec : FunSpec({

    context("it parses the nested Meteomatics response into a Double temperature") {
        val mockHttpClient = mockk<MeteomaticsClient>()
        val underTest = MeteomaticsWeatherClient(mockHttpClient)

        val location = Location("51.427195", "-0.108248")
        val now = Clock.System.now()
        val expectedTemp = 7.4

        val responseBody: Map<String, Any> = mapOf(
            "data" to listOf(
                mapOf(
                    "coordinates" to listOf(
                        mapOf(
                            "dates" to listOf(
                                mapOf(
                                    "value" to expectedTemp
                                )
                            )
                        )
                    )
                )
            )
        )

        every {
            mockHttpClient.getTemperatureByDateAndLocation(
                now.toString(),
                location.latitude,
                location.longitude
            )
        } returns HttpResponse.ok(responseBody)

        test("returns the expected temperature as a Double") {
            val temp = underTest.getTemperatureByDateAndLocation(now, location)
            temp shouldBe expectedTemp
        }
    }
})
