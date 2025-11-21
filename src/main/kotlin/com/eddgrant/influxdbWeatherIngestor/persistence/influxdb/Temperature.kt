package com.eddgrant.influxdbWeatherIngestor.persistence.influxdb

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.time.Instant

@Measurement(name = "Temperature")
data class Temperature(
    @Column(tag = true) val source: String,
    @Column(tag = true) val postcode: String,
    @Column(tag = true) val provider: String,
    @Column val value: Double,
    @Column(timestamp = true) val time: Instant
) {
    /**
     * Returns a compact, single-line representation of this measurement suitable for logging.
     */
    override fun toString(): String = "Temperature[source=$source, postcode=$postcode, provider=$provider, value=$value, time=$time]"
}