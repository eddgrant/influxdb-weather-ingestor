package com.eddgrant

import com.eddgrant.influxdbWeatherIngestor.checks.RegisterChecksAction
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.Micronaut.run
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.inject.Singleton

@Singleton
class InfluxDBWeatherIngestor(
	private val registerChecksAction: RegisterChecksAction
) : ApplicationEventListener<ServerStartupEvent> {
	override fun onApplicationEvent(event: ServerStartupEvent?) {
		registerChecksAction.register()
	}
}

fun main(args: Array<String>) {
	run(InfluxDBWeatherIngestor::class.java, *args)
}

