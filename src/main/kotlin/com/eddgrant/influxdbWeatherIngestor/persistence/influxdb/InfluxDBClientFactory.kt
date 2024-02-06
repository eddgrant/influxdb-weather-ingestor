package com.eddgrant.influxdbWeatherIngestor.persistence.influxdb

import com.influxdb.client.InfluxDBClientOptions
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires

@Requires(bean = InfluxDBConfiguration::class)
@Factory
class InfluxDBClientFactory(
    private val influxDBConfiguration: InfluxDBConfiguration
) {

    @Bean
    fun influxDBClient() : InfluxDBClientKotlin {
        val influxDBClientOptions = InfluxDBClientOptions
            .builder()
            .authenticateToken(influxDBConfiguration.token.toCharArray())
            .bucket(influxDBConfiguration.bucket)
            .logLevel(influxDBConfiguration.logLevel)
            .org(influxDBConfiguration.org)
            .url(influxDBConfiguration.url.toString())
            .build()
        return InfluxDBClientKotlinFactory.create(influxDBClientOptions)
    }
}