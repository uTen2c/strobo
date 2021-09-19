package dev.uten2c.strobo.util

import net.minecraft.util.math.Vec3d

class Location(x: Double, y: Double, z: Double, @JvmField val yaw: Float, @JvmField val pitch: Float) : Vec3d(x, y, z) {

    val vec = Vec3d(x, y, z)

    fun setX(x: Double): Location = Location(x, y, z, yaw, pitch)

    fun setY(y: Double): Location = Location(x, y, z, yaw, pitch)

    fun setZ(z: Double): Location = Location(x, y, z, yaw, pitch)

    fun setYaw(yaw: Float): Location = Location(x, y, z, yaw, pitch)

    fun setPitch(pitch: Float): Location = Location(x, y, z, yaw, pitch)

    override fun add(vec: Vec3d): Location = add(vec.x, vec.y, vec.z)

    override fun add(x: Double, y: Double, z: Double): Location = Location(this.x + x, this.y + y, this.z + z, yaw, pitch)

    fun set(x: Double, y: Double, z: Double): Location = Location(x, y, z, yaw, pitch)

    fun set(yaw: Float, pitch: Float): Location = Location(x, y, z, yaw, pitch)

    fun clone(): Location = Location(x, y, z, yaw, pitch)

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is Location -> this.x == other.x && this.y == other.y && this.z == other.z && this.yaw == other.yaw && this.pitch == other.pitch
        else -> super.equals(other)
    }

    override fun toString(): String {
        return "Location(x: $x, y: $y, z: $z, yaw: $yaw, pitch $pitch)"
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + yaw.hashCode()
        result = 31 * result + pitch.hashCode()
        return result
    }

    companion object {
        val ZERO = Location(0.0, 0.0, 0.0, 0f, 0f)
    }
}