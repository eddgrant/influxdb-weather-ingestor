@file:OptIn(ExperimentalTime::class)

package com.eddgrant.influxdbWeatherIngestor.weather.meteomatics

import com.eddgrant.influxdbWeatherIngestor.location.Location
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import org.junit.jupiter.api.Assumptions.assumeTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@MicronautTest(environments = ["integration-test"])
class MeteomaticsClientIntegrationSpec(private val meteomaticsClient: MeteomaticsClient) : FunSpec({

    context("it can obtain temperature data by date and location") {
        val username = System.getenv("METEOMATICS_USERNAME")
        val password = System.getenv("METEOMATICS_PASSWORD")
        // Skip this test if Meteomatics credentials are not present in the environment
        assumeTrue(!username.isNullOrBlank() && !password.isNullOrBlank())

        val location = Location("51.427195", "-0.108248")
        val now = Clock.System.now().toString()
        val response = meteomaticsClient.getTemperatureByDateAndLocation(
            now,
            location.latitude,
            location.longitude
        )
        response.status shouldBe HttpStatus.OK
        ((((((response.body()
            ?.get("data") as ArrayList<*>).first() as Map<*, *>)
            .get("coordinates") as ArrayList<*>).first() as Map<*, *>)
            .get("dates") as ArrayList<*>).first() as Map<*, *>)
            .get("value") should beInstanceOf<Number>()
    }
})