package com.eddgrant.influxdbWeatherIngestor.location

import io.micronaut.core.type.Argument
import io.micronaut.serde.*
import jakarta.inject.Singleton

@Singleton
class LocationSerde : Serde<Location> {
    override fun deserialize(
        decoder: Decoder,
        context: Deserializer.DecoderContext,
        type: Argument<in Location>
    ): Location {

        fun skipToKey(decoder: Decoder, soughtKey: String) {
            while (decoder.decodeKey() != soughtKey) {
                decoder.skipValue()
            }
        }

        decoder.decodeObject().use { root ->
            skipToKey(root, "result")
            root.decodeObject().use { result ->
                skipToKey(result, "longitude")
                val longitude = result.decodeString()
                skipToKey(result, "latitude")
                val latitude = result.decodeString()

                /**
                 * For some reason we have to traverse all values in the Decoder
                 * otherwise it throws an IllegalStateException on closure.
                 */
                while(result.decodeKey() != null) {
                    result.skipValue()
                }
                return Location(latitude, longitude)
            }
        }
    }

    override fun serialize(
        encoder: Encoder,
        context: Serializer.EncoderContext,
        type: Argument<out Location>,
        value: Location
    ) {
        TODO("Not yet implemented")
    }
}