package dev.uten2c.strobo.util

import net.minecraft.network.packet.s2c.play.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

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

    if (title != null) {
        networkHandler.sendPacket(TitleS2CPacket(title))
    }

    if (subtitle != null) {
        networkHandler.sendPacket(SubtitleS2CPacket(subtitle))
    }
}

fun ServerPlayerEntity.clearTitle() = networkHandler.sendPacket(ClearTitleS2CPacket(null))

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