package dev.uten2c.strobo.serialize

import dev.uten2c.strobo.util.Location
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object LocationSerializer : KSerializer<Location> {

    override val descriptor: SerialDescriptor = LocationSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Location {
        val surrogate = decoder.decodeSerializableValue(LocationSurrogate.serializer())
        return Location(surrogate.x, surrogate.y, surrogate.z, surrogate.yaw, surrogate.pitch)
    }

    override fun serialize(encoder: Encoder, value: Location) {
        val surrogate = LocationSurrogate(value.x, value.y, value.z, value.yaw, value.pitch)
        encoder.encodeSerializableValue(LocationSurrogate.serializer(), surrogate)
    }
}

@Serializable
internal data class LocationSurrogate(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float)
