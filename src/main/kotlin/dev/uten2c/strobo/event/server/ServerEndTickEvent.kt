package dev.uten2c.strobo.event.server

import dev.uten2c.strobo.event.Event
import net.minecraft.server.MinecraftServer

/**
 * サーバーのメインスレッド処理の一番最後で呼び出される
 * @param server [MinecraftServer]のインスタンス
 */
class ServerEndTickEvent(val server: MinecraftServer) : Event()
