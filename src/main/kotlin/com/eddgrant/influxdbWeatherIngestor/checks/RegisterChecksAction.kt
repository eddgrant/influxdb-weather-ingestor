package com.eddgrant.influxdbWeatherIngestor.checks

import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.TaskScheduler
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RegisterChecksAction(
    private val checkConfiguration: CheckConfiguration,
    private val temperatureEmitter: TemperatureEmitter,
    @field:Named(TaskExecutors.SCHEDULED) private val taskScheduler: TaskScheduler
) {
    private var scheduledFuture: java.util.concurrent.ScheduledFuture<*>? = null

    fun register() {
        val checkTemperatureTask = CheckTemperatureTask(temperatureEmitter)
        scheduledFuture = taskScheduler.schedule(
            checkConfiguration.scheduleExpression,
            checkTemperatureTask
        )
        LOGGER.info("Temperature checks scheduled to run on schedule: ${checkConfiguration.scheduleExpression}")
    }

    fun isScheduleActive(): Boolean =
        scheduledFuture != null && !scheduledFuture!!.isCancelled

 companion object {
     private val LOGGER = LoggerFactory.getLogger(RegisterChecksAction::class.java)
 }
}