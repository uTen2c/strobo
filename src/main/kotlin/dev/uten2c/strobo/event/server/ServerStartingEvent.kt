package dev.uten2c.strobo.event.server

import dev.uten2c.strobo.event.Event
import net.minecraft.server.MinecraftServer

class ServerStartingEvent(val server: MinecraftServer) : Event()