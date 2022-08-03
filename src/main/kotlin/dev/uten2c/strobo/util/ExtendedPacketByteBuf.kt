package dev.uten2c.strobo.util

import dev.uten2c.strobo.serversideitem.RenderType
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf

internal interface ExtendedPacketByteBuf {
    fun writeItemStack(original: ItemStack, renderType: RenderType): PacketByteBuf
}
