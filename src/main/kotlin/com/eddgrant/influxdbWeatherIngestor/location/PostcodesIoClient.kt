package com.eddgrant.influxdbWeatherIngestor.location

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import jakarta.validation.constraints.NotBlank

@Client("postcodes-io")
interface PostcodesIoClient {

    @Get("/postcodes/{postcode}")
    fun findLocationByPostcode(@NotBlank postcode: String) : HttpResponse<Location>
}