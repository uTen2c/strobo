package dev.uten2c.strobo.util

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ChunkTicketType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

/**
 * エンティティの見ている向きのベクトルを取得
 */
val Entity.direction: Vec3d
    get() {
        val x = -MathHelper.sin(yaw * 0.017453292f) * MathHelper.cos(pitch * 0.017453292f)
        val y = -MathHelper.sin(pitch * 0.017453292f)
        val z = MathHelper.cos(yaw * 0.017453292f) * MathHelper.cos(pitch * 0.017453292f)
        return Vec3d(x.toDouble(), y.toDouble(), z.toDouble()).normalize()
    }

/**
 * エンティティの座標系を取得
 */
val Entity.location: Location
    get() = Location(x, y, z, yaw, pitch)

/**
 * PaperSpigot内で使用されているテレポート処理をエミュレートする
 * @param location 座標
 */
fun Entity.bukkitTp(location: Location) {
    val x = location.x
    val y = location.y
    val z = location.z
    val yaw = location.yaw
    val pitch = location.pitch

    if (this is ServerPlayerEntity) {
        val chunkPos = ChunkPos(BlockPos(x, y, z))
        serverWorld.chunkManager.addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, id)
        stopRiding()
        if (isSleeping) {
            wakeUp(true, true)
        }
        networkHandler.requestTeleport(x, y, z, yaw, pitch, emptySet())
        setHeadYaw(yaw)
    } else {
        val f = MathHelper.wrapDegrees(yaw)
        var g = MathHelper.wrapDegrees(pitch)
        g = MathHelper.clamp(g, -90.0f, 90.0f)
        refreshPositionAndAngles(x, y, z, f, g)
        headYaw = f
    }

    if (this !is LivingEntity || !isFallFlying) {
        velocity = velocity.multiply(1.0, 0.0, 1.0)
        isOnGround = true
    }

    if (this is PathAwareEntity) {
        navigation.stop()
    }
}
