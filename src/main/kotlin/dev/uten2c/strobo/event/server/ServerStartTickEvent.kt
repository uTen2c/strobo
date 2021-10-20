package dev.uten2c.strobo.event.server

import dev.uten2c.strobo.event.Event
import net.minecraft.server.MinecraftServer

/**
 * サーバーのメインスレッド処理の一番最初に呼び出される
 * @param server [MinecraftServer]のインスタンス
 */
class ServerStartTickEvent(val server: MinecraftServer) : Event()
