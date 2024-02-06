package com.eddgrant.influxdbWeatherIngestor.weather.meteomatics

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client

@Client(MeteomaticsClient.serviceId)
interface MeteomaticsClient {

    @Get(uri = "/{dateAndTime}/t_2m:C/{latitude},{longitude}/json?model=mix",
        produces = [MediaType.APPLICATION_JSON])
    fun getTemperatureByDateAndLocation(
        @PathVariable dateAndTime: String,
        @PathVariable latitude: String,
        @PathVariable longitude: String
    ) : HttpResponse<Map<String, Any>>

    companion object {
        const val serviceId = "meteomatics"
    }

}