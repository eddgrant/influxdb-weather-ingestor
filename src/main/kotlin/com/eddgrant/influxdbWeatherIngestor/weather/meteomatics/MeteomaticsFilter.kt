package com.eddgrant.influxdbWeatherIngestor.weather.meteomatics

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import org.reactivestreams.Publisher

@Filter(serviceId = [MeteomaticsClient.serviceId])
@Requires(beans = [MeteomaticsConfiguration::class])
class MeteomaticsAuthenticationFilter(private val meteomaticsConfiguration: MeteomaticsConfiguration) : HttpClientFilter {

    override fun doFilter(request: MutableHttpRequest<*>, chain: ClientFilterChain): Publisher<out HttpResponse<*>> =
        chain.proceed(request.basicAuth(meteomaticsConfiguration.username, meteomaticsConfiguration.password))
}