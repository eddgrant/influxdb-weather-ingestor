package com.eddgrant.influxdbWeatherIngestor.weather.weatherapi

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client

@Client(WeatherApiDotComClient.serviceId)
interface WeatherApiDotComClient {

    @Get(uri = "/v1/current.json?key={apiKey}&q={query}", produces = [MediaType.APPLICATION_JSON])
    fun getCurrent(
        @PathVariable apiKey: String,
        @PathVariable query: String
    ): HttpResponse<Map<String, Any>>

    companion object {
        const val serviceId = "weatherapi"
    }
}
