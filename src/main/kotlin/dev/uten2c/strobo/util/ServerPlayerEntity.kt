package dev.uten2c.strobo.util

import dev.uten2c.strobo.task.afterTicks
import net.minecraft.network.packet.s2c.play.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

private val hiddenPlayerMap = HashMap<UUID, HashSet<UUID>>()
private val states = object : HashMap<ServerPlayerEntity, ListState>() {
    override fun get(key: ServerPlayerEntity): ListState {
        if (key !in this) {
            put(key, ListState(null, null, null))
        }
        return super.get(key)!!
    }
}

private data class ListState(var header: Text?, var footer: Text?, var name: Text?)

var ServerPlayerEntity.playerListHeader: Text?
    get() = states[this].header
    set(value) {
        states[this].header = value
        updatePlayerListHeaderAndFooter()
    }

var ServerPlayerEntity.playerListFooter: Text?
    get() = states[this].footer
    set(value) {
        states[this].footer = value
        updatePlayerListHeaderAndFooter()
    }

fun ServerPlayerEntity.updatePlayerListHeaderAndFooter() {
    val packet = PlayerListHeaderS2CPacket(
        playerListHeader ?: LiteralText.EMPTY,
        playerListFooter ?: LiteralText.EMPTY
    )
    networkHandler.sendPacket(packet)
}

fun ServerPlayerEntity.sendTitle(title: Text?, subtitle: Text?, fadeIn: Int, stay: Int, fadeOut: Int) {
    networkHandler.sendPacket(TitleFadeS2CPacket(fadeIn, stay, fadeOut))
    networkHandler.sendPacket(SubtitleS2CPacket(subtitle ?: emptyText()))
    networkHandler.sendPacket(TitleS2CPacket(title ?: emptyText()))
}

fun ServerPlayerEntity.clearTitle() = networkHandler.sendPacket(ClearTitleS2CPacket(false))

fun ServerPlayerEntity.respawn() {
    val manager = server?.playerManager
    if (health <= 0 && manager?.getPlayer(uuid) != null) {
        networkHandler.player = manager.respawnPlayer(this, false)
    }
}

var ServerPlayerEntity.flySpeed: Float
    get() = abilities.flySpeed
    set(value) {
        abilities.flySpeed = value / 2f
        sendAbilitiesUpdate()
    }

fun ServerPlayerEntity.bukkitTp(location: Location): Boolean {
    if (health == 0f || isRemoved) {
        return false
    }

    if (networkHandler == null) {
        return false
    }

    if (hasPassengers()) {
        return false
    }

    stopRiding()

    if (isSleeping) {
        wakeUp(true, false)
    }

    if (currentScreenHandler != playerScreenHandler) {
        closeHandledScreen()
    }

    networkHandler.requestTeleport(location.x, location.y, location.z, location.yaw, location.pitch)
    return true
}

fun ServerPlayerEntity.showPlayer(player: ServerPlayerEntity) {
    if (this == player) {
        return
    }

    val set = hiddenPlayerMap.getOrPut(uuid) { HashSet() }
    set.remove(player.uuid)

    if (!player.isDisconnected) {
        networkHandler.sendPacket(PlayerSpawnS2CPacket(player))
        val y = (player.headYaw * 256f / 360f).toInt().toByte()
        val p = (player.pitch * 256f / 360f).toInt().toByte()
        networkHandler.sendPacket(EntityS2CPacket.Rotate(player.id, y, p, player.isOnGround))
        repeat(3) {
            afterTicks(it.toLong()) {
                networkHandler.sendPacket(EntityTrackerUpdateS2CPacket(player.id, player.dataTracker, true))
            }
        }
    }
}

fun ServerPlayerEntity.hidePlayer(player: ServerPlayerEntity) {
    if (this == player) {
        return
    }

    val set = hiddenPlayerMap.getOrPut(uuid) { HashSet() }
    set.add(player.uuid)
    val packet = EntitiesDestroyS2CPacket(player.id)
    networkHandler.sendPacket(packet)
}

fun ServerPlayerEntity.isHidden(player: ServerPlayerEntity): Boolean = hiddenPlayerMap[uuid]?.contains(player.uuid) ?: false
