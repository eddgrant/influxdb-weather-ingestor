package com.eddgrant.influxdbWeatherIngestor.checks

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

//@RequiresCheckConfiguration
class CheckTemperatureTask(
    private val temperatureEmitter: TemperatureEmitter
) : Runnable {
    override fun run() {
        LOGGER.info("Checking the temperature")
        runBlocking {
            temperatureEmitter.emitTemperature()
        }
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(CheckTemperatureTask::class.java)
    }
}