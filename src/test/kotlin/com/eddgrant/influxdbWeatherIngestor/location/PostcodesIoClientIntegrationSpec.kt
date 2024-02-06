package com.eddgrant.influxdbWeatherIngestor.location

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest(environments = ["integration-test"])
class PostcodesIoClientIntegrationSpec(private val postcodesIoClient: PostcodesIoClient) : BehaviorSpec({

    given("A postcode") {
        `when`("the postcode's location is sought") {
            val location = postcodesIoClient.findLocationByPostcode("AB12 3CD")
            then("the associated location is returned") {
                location.latitude shouldBe "51.427195"
                location.longitude shouldBe "-0.108248"
            }
        }
    }
})