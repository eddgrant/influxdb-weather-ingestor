package com.eddgrant.influxdbWeatherIngestor.checks

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import java.time.Duration

@MicronautTest
@Property(name="checks.check-interval", value="30s")
@Property(name="checks.postcode", value=CheckConfigurationSpec.POSTCODE)
class CheckConfigurationSpec(
    private val checkConfiguration: CheckConfiguration
) : ShouldSpec({

    should("set the check interval") {
        checkConfiguration.checkInterval shouldBe Duration.ofSeconds(30)
    }

    should("set the postcode") {
        checkConfiguration.postcode shouldBe POSTCODE
    }
}) {
    companion object {
        const val POSTCODE = "AB12 3CD"
    }
}

@MicronautTest
@Property(name="checks.postcode", value=CheckConfigurationSpec.POSTCODE)
class CheckConfigurationDefaultsSpec(
    private val checkConfiguration: CheckConfiguration
) : ShouldSpec({

    should("use the default check interval of 1 minute") {
        checkConfiguration.checkInterval shouldBe Duration.ofMinutes(1)
    }
})
