package com.eddgrant

import com.eddgrant.influxdbWeatherIngestor.checks.TemperatureEmitter
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.Micronaut.run
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import reactor.core.Disposable

@Singleton
@Context
@Requires(notEnv = ["test"])
class InfluxDBWeatherIngestor(
	private val temperatureEmitter: TemperatureEmitter
) {
	private lateinit var disposable: Disposable

	@PostConstruct
	fun start() {
		LOGGER.info("Starting temperature monitoring subscription.")
		disposable = temperatureEmitter.publishTemperature(
			temperatureEmitter.getTemperatureData()
		).subscribe()
		LOGGER.info("Temperature monitoring subscription created.")
	}

	fun isSubscriptionActive(): Boolean =
		this::disposable.isInitialized && !disposable.isDisposed

	@PreDestroy
	fun stop() {
		LOGGER.info("Shutdown signal received: Cancelling temperature monitoring subscription.")
		if (isSubscriptionActive()) {
			disposable.dispose()
		}
	}

	companion object {
		private val LOGGER = LoggerFactory.getLogger(InfluxDBWeatherIngestor::class.java)
	}
}

fun main(args: Array<String>) {
	run(InfluxDBWeatherIngestor::class.java, *args)
}
