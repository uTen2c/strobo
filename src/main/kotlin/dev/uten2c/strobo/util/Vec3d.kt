package dev.uten2c.strobo.util

import net.minecraft.util.math.Vec3d

operator fun Vec3d.plus(vec3d: Vec3d): Vec3d = this.add(vec3d)
operator fun Vec3d.minus(vec3d: Vec3d): Vec3d = this.subtract(vec3d)
operator fun Vec3d.times(vec3d: Vec3d): Vec3d = this.multiply(vec3d)
operator fun Vec3d.times(number: Number): Vec3d = this.multiply(number.toDouble())
