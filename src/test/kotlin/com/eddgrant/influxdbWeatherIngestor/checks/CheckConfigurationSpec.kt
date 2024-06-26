package com.eddgrant.influxdbWeatherIngestor.checks

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
@Property(name="checks.schedule-expression", value= CheckConfigurationSpec.SCHEDULE_EXPRESSION)
@Property(name="checks.postcode", value= CheckConfigurationSpec.POSTCODE)
class CheckConfigurationSpec(
    private val checkConfiguration: CheckConfiguration
) : ShouldSpec({

    should("set the schedule expression") {
        checkConfiguration.scheduleExpression shouldBe SCHEDULE_EXPRESSION
    }

    should("set the postcode") {
        checkConfiguration.postcode shouldBe POSTCODE
    }
}) {
    companion object {
        const val SCHEDULE_EXPRESSION = "* * * 1 *"
        const val POSTCODE = "AB12 3CD"
    }
}

@MicronautTest
@Property(name="checks.postcode", value= CheckConfigurationSpec.POSTCODE)
class CheckConfigurationDefaultsSpec(
    private val checkConfiguration: CheckConfiguration
) : ShouldSpec({

    should("use the default schedule expression") {
        checkConfiguration.scheduleExpression shouldBe "* * * * *"
    }
})
