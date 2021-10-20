package dev.uten2c.strobo.event.server

import dev.uten2c.strobo.event.Event
import net.minecraft.server.MinecraftServer

/**
 * サーバーの起動時に呼び出させる
 * @param server [MinecraftServer]のインスタンス
 */
class ServerStartingEvent(val server: MinecraftServer) : Event()
