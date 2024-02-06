package com.eddgrant.influxdbWeatherIngestor.checks

import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.TaskScheduler
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
//@RequiresCheckConfiguration
class RegisterChecksAction(
    private val checkConfiguration: CheckConfiguration,
    private val temperatureEmitter: TemperatureEmitter,
    @Named(TaskExecutors.SCHEDULED) private val taskScheduler: TaskScheduler
) {
    fun register() {
        val checkTemperatureTask = CheckTemperatureTask(temperatureEmitter)
        taskScheduler.schedule(
            checkConfiguration.scheduleExpression,
            checkTemperatureTask
        )
        LOGGER.info("Temperature checks scheduled to run on schedule: ${checkConfiguration.scheduleExpression}")
    }
 companion object {
     val LOGGER = LoggerFactory.getLogger(RegisterChecksAction::class.java)
 }
}