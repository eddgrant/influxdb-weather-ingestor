@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather.weatherapi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import org.junit.jupiter.api.Assumptions.assumeTrue
import kotlin.time.ExperimentalTime

@MicronautTest(environments = ["integration-test"])
class WeatherApiDotComClientIntegrationSpec(
    private val weatherApiDotComClient: WeatherApiDotComClient
) : FunSpec({

    context("it can obtain current temperature from weatherapi.com when API key present") {
        val apiKey = System.getenv("WEATHERAPI_API_KEY") ?: System.getenv("WEATHERAPI_APIKEY")
        // Skip this test if the API key is not present in the environment
        assumeTrue(!apiKey.isNullOrBlank())

        val query = "51.427195,-0.108248" // London coordinates
        val response = weatherApiDotComClient.getCurrent(apiKey!!, query)

        test("returns HTTP 200 and temp_c is a number") {
            response.status shouldBe HttpStatus.OK
            val body = response.body()!!
            val current = body["current"] as Map<*, *>
            current["temp_c"] should beInstanceOf<Number>()
        }
    }
})
