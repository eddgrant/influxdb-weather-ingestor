package com.eddgrant.influxdbWeatherIngestor.checks

import io.kotest.core.spec.style.FunSpec
import io.micronaut.scheduling.TaskScheduler
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import io.mockk.mockk
import io.mockk.verify

@MicronautTest
class RegisterChecksActionSpec(
    private val registerChecksAction: RegisterChecksAction,
    private val checkConfiguration: CheckConfiguration,
    private val taskScheduler: TaskScheduler
) : FunSpec({

    test("it schedules the temperature check using the configured cron expression") {
        val expectedExpression = checkConfiguration.scheduleExpression

        registerChecksAction.register()

        verify(exactly = 1) {
            taskScheduler.schedule(expectedExpression, any())
        }
    }
}) {

    @MockBean(TaskScheduler::class)
    fun taskScheduler(): TaskScheduler = mockk(relaxed = true)

    @MockBean(TemperatureEmitter::class)
    fun temperatureEmitter(): TemperatureEmitter = mockk(relaxed = true)
}
