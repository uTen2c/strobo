package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class PlayerQuitEvent(val player: ServerPlayerEntity, var message: Text) : Event()
