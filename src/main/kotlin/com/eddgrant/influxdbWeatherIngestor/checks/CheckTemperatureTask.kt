package com.eddgrant.influxdbWeatherIngestor.checks

import kotlinx.coroutines.Runnable
import org.slf4j.LoggerFactory

class CheckTemperatureTask(
    private val temperatureEmitter: TemperatureEmitter
) : Runnable {
    override fun run() {
        try {
            LOGGER.info("Checking the temperature")
            temperatureEmitter.emitTemperature()
        } catch (e: Exception) {
            LOGGER.error("Temperature check failed: {}", e.message, e)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CheckTemperatureTask::class.java)
    }
}