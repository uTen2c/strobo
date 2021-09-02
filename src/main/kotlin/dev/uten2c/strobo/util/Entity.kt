package dev.uten2c.strobo.util

import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

val Entity.direction: Vec3d
    get() {
        val x = -MathHelper.sin(yaw * 0.017453292f) * MathHelper.cos(pitch * 0.017453292f)
        val y = -MathHelper.sin(pitch * 0.017453292f)
        val z = MathHelper.cos(yaw * 0.017453292f) * MathHelper.cos(pitch * 0.017453292f)
        return Vec3d(x.toDouble(), y.toDouble(), z.toDouble()).normalize()
    }