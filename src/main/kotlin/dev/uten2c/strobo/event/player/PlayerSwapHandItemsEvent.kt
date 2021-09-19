package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class PlayerSwapHandItemsEvent(val player: ServerPlayerEntity, val mainHandStack: ItemStack, val offHandStack: ItemStack) : CancellableEvent()