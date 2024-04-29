package com.eddgrant

import com.eddgrant.influxdbWeatherIngestor.checks.RegisterChecksAction
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.Micronaut.run
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class InfluxDBWeatherIngestor(
	private val registerChecksAction: RegisterChecksAction
) : ApplicationEventListener<ServerStartupEvent> {
	override fun onApplicationEvent(event: ServerStartupEvent?) {
		try {
			registerChecksAction.register()
		}
		catch (interruptedException: InterruptedException) {
			LOGGER.error(interruptedException.message)
		}
	}

	companion object {
		private val LOGGER = LoggerFactory.getLogger(InfluxDBWeatherIngestor::class.java)
	}
}

fun main(args: Array<String>) {
	run(InfluxDBWeatherIngestor::class.java, *args)
}

