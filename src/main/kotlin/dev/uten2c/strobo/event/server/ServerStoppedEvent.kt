package dev.uten2c.strobo.event.server

import dev.uten2c.strobo.event.Event
import net.minecraft.server.MinecraftServer

/**
 * サーバー停止後に呼び出される
 * @param server [MinecraftServer]のインスタンス
 */
class ServerStoppedEvent(val server: MinecraftServer) : Event()
