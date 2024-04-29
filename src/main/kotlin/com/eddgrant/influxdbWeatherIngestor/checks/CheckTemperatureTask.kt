package com.eddgrant.influxdbWeatherIngestor.checks

import kotlinx.coroutines.Runnable
import org.slf4j.LoggerFactory

//@RequiresCheckConfiguration
class CheckTemperatureTask(
    private val temperatureEmitter: TemperatureEmitter
) : Runnable {
    override fun run() {
        LOGGER.info("Checking the temperature")
        temperatureEmitter.emitTemperature()
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(CheckTemperatureTask::class.java)
    }
}