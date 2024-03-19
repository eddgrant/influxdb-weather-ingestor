package com.eddgrant.influxdbWeatherIngestor.location

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest(environments = ["integration-test"])
class PostcodesIoClientIntegrationSpec(private val postcodesIoClient: PostcodesIoClient) : BehaviorSpec({

    given("A valid UK postcode postcode") {
        val postcode = "SO30 4LA"
        `when`("the postcode's location is sought") {
            val httpResponse = postcodesIoClient.findLocationByPostcode(postcode)

            then("the HTTP response status code is OK") {
                httpResponse.status shouldBe HttpStatus.OK
            }
            and("the associated location is returned") {
                val location = httpResponse.body()!!
                location.latitude shouldBe "50.913193"
                location.longitude shouldBe "-1.308451"
            }
        }
    }

    given("An invalid valid UK postcode postcode") {
        val postcode = "this-postcode-does-not-exist"
        `when`("the postcode's location is sought") {
            val httpResponse = postcodesIoClient.findLocationByPostcode(postcode)

            then("the HTTP response status code is NOT_FOUND") {
                httpResponse.status shouldBe HttpStatus.NOT_FOUND
            }
            and("no location is returned") {
                val location = httpResponse.body()
                location shouldBe null
            }
        }
    }
})