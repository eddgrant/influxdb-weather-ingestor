package com.eddgrant.influxdbWeatherIngestor.location

import io.micronaut.cache.annotation.Cacheable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import jakarta.validation.constraints.NotBlank

@Client("postcodes-io")
interface PostcodesIoClient {

    @Get("/postcodes/{postcode}")
    @Cacheable("postcodes")
    fun findLocationByPostcode(@NotBlank postcode: String) : HttpResponse<Location>
}