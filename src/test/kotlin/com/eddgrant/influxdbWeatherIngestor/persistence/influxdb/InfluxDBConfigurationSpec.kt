package com.eddgrant.influxdbWeatherIngestor.persistence.influxdb

import com.influxdb.LogLevel
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import java.net.URI

@MicronautTest
@Property(name="influxdb.bucket", value=InfluxDBConfigurationSpec.BUCKET)
@Property(name="influxdb.org", value=InfluxDBConfigurationSpec.ORG)
@Property(name="influxdb.log-level", value=InfluxDBConfigurationSpec.LOG_LEVEL)
@Property(name="influxdb.token", value=InfluxDBConfigurationSpec.TOKEN)
@Property(name="influxdb.url", value=InfluxDBConfigurationSpec.URL)
class InfluxDBConfigurationSpec(
    private val influxDBConfiguration: InfluxDBConfiguration
) : ShouldSpec({

    should("set the bucket") {
        influxDBConfiguration.bucket shouldBe BUCKET
    }

    should("set the org") {
        influxDBConfiguration.org shouldBe ORG
    }

    should("set the log level") {
        influxDBConfiguration.logLevel shouldBe LogLevel.valueOf(LOG_LEVEL)
    }

    should("set the token") {
        influxDBConfiguration.token shouldBe TOKEN
    }

    should("set the url") {
        influxDBConfiguration.url shouldBe URI(URL).toURL()
    }

}) {
    companion object {
        const val BUCKET = "temperature"
        const val ORG = "eddgrant"
        const val LOG_LEVEL = "BASIC"
        const val TOKEN = "my-very-secure-influxdb-token"
        const val URL = "http://influxdb:8086"
    }
}

@MicronautTest
@Property(name="influxdb.bucket", value=InfluxDBConfigurationSpec.BUCKET)
@Property(name="influxdb.org", value=InfluxDBConfigurationSpec.ORG)
@Property(name="influxdb.token", value=InfluxDBConfigurationSpec.TOKEN)
@Property(name="influxdb.url", value=InfluxDBConfigurationSpec.URL)
class InfluxDBConfigurationDefaultValuesSpec(
    private val influxDBConfiguration: InfluxDBConfiguration
) : ShouldSpec({

    should("use the default log level") {
        influxDBConfiguration.logLevel shouldBe LogLevel.NONE
    }
})