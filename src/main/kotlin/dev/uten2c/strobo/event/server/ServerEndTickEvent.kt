package dev.uten2c.strobo.event.server

import dev.uten2c.strobo.event.Event
import net.minecraft.server.MinecraftServer

class ServerEndTickEvent(val server: MinecraftServer) : Event()