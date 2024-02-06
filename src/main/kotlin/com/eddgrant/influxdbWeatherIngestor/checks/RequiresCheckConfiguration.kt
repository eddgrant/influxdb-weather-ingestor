package com.eddgrant.influxdbWeatherIngestor.checks

import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Requirements(Requires(beans = [CheckConfiguration::class]), Requires(property = "checks.postcode"))
annotation class RequiresCheckConfiguration