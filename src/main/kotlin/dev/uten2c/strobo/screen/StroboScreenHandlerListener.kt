package dev.uten2c.strobo.screen

import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.server.network.ServerPlayerEntity

internal class StroboScreenHandlerListener(
    val player: ServerPlayerEntity,
    private val original: ScreenHandlerListener,
) :
    ScreenHandlerListener {
    override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {
        original.onSlotUpdate(handler, slotId, stack)
    }

    override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {
        original.onPropertyUpdate(handler, property, value)
    }
}
