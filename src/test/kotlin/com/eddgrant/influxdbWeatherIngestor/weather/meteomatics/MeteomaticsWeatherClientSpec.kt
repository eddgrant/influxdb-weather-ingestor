@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather.meteomatics

import com.eddgrant.influxdbWeatherIngestor.location.Location
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.micronaut.http.HttpResponse
import io.mockk.every
import io.mockk.mockk
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MeteomaticsWeatherClientSpec : FunSpec({

    val mockHttpClient = mockk<MeteomaticsClient>()
    val underTest = MeteomaticsWeatherClient(mockHttpClient)
    val location = Location("51.427195", "-0.108248")
    val now = Clock.System.now()

    context("it parses the nested Meteomatics response into a Double temperature") {
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

    context("it handles integer temperature values") {
        val responseBody: Map<String, Any> = mapOf(
            "data" to listOf(
                mapOf(
                    "coordinates" to listOf(
                        mapOf(
                            "dates" to listOf(
                                mapOf("value" to 7)
                            )
                        )
                    )
                )
            )
        )

        every {
            mockHttpClient.getTemperatureByDateAndLocation(
                now.toString(), location.latitude, location.longitude
            )
        } returns HttpResponse.ok(responseBody)

        test("converts integer to Double") {
            underTest.getTemperatureByDateAndLocation(now, location) shouldBe 7.0
        }
    }

    context("it throws a meaningful error for malformed responses") {
        test("throws when response body is null") {
            every {
                mockHttpClient.getTemperatureByDateAndLocation(
                    now.toString(), location.latitude, location.longitude
                )
            } returns HttpResponse.ok()

            val ex = shouldThrow<IllegalStateException> {
                underTest.getTemperatureByDateAndLocation(now, location)
            }
            ex.message shouldContain "empty response body"
        }

        test("throws when 'data' is missing") {
            every {
                mockHttpClient.getTemperatureByDateAndLocation(
                    now.toString(), location.latitude, location.longitude
                )
            } returns HttpResponse.ok(mapOf("unexpected" to "structure"))

            val ex = shouldThrow<IllegalStateException> {
                underTest.getTemperatureByDateAndLocation(now, location)
            }
            ex.message shouldContain "data"
        }

        test("throws when 'coordinates' is missing") {
            every {
                mockHttpClient.getTemperatureByDateAndLocation(
                    now.toString(), location.latitude, location.longitude
                )
            } returns HttpResponse.ok(mapOf("data" to listOf(mapOf("no-coordinates" to true))))

            val ex = shouldThrow<IllegalStateException> {
                underTest.getTemperatureByDateAndLocation(now, location)
            }
            ex.message shouldContain "coordinates"
        }
    }
})
