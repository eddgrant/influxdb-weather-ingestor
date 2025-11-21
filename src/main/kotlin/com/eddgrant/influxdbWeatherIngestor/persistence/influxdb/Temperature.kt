package com.eddgrant.influxdbWeatherIngestor.persistence.influxdb

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.time.Instant

@Measurement(name = "Temperature")
data class Temperature(
    @Column(tag = true) val source: String,
    @Column(tag = true) val postcode: String,
    @Column val value: Double,
    @Column(timestamp = true) val time: Instant
)