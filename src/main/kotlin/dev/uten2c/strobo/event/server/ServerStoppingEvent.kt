package dev.uten2c.strobo.event.server

import dev.uten2c.strobo.event.Event
import net.minecraft.server.MinecraftServer

/**
 * サーバー停止時に呼び出される
 * @param server [MinecraftServer]のインスタンス
 */
class ServerStoppingEvent(val server: MinecraftServer) : Event()
