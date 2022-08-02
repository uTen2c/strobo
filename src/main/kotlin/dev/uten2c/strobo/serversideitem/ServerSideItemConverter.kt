package dev.uten2c.strobo.serversideitem

import dev.uten2c.strobo.event.listenEvent
import dev.uten2c.strobo.event.player.PlayerGameModeChangedEvent
import dev.uten2c.strobo.mixin.accessor.ItemStackAccessor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.nio.charset.StandardCharsets
import java.util.Base64

internal object ServerSideItemConverter {
    private const val INTERNAL_ID_KEY = "@item"
    private const val INTERNAL_NBT_KEY = "@nbt"

    fun setup() {
        listenEvent<PlayerGameModeChangedEvent> { e ->
            e.player.playerScreenHandler.syncState()
        }
    }

    @JvmStatic
    fun shouldConvert(stack: ItemStack): Boolean {
        val nbt = stack.copy().orCreateNbt
        if (INTERNAL_ID_KEY !in nbt) {
            return false
        }
        val id = Identifier(nbt.getString(INTERNAL_ID_KEY))
        val item = Registry.ITEM[id]
        return item is ServerSideItem
    }

    @JvmStatic
    @Suppress("CAST_NEVER_SUCCEEDS")
    fun convert(stack: ItemStack): ItemStack {
        val copy = stack.copy()
        val nbtWithSign = copy.nbt ?: return copy
        val id = Identifier(nbtWithSign.getString(INTERNAL_ID_KEY))
        val item = Registry.ITEM.get(id)
        val nbt = if (nbtWithSign.contains(INTERNAL_NBT_KEY)) {
            val signature = nbtWithSign.getString(INTERNAL_NBT_KEY)
            decodeNbt(signature)
        } else {
            null
        }

        (copy as ItemStackAccessor).setItem(item)
        copy.nbt = nbt

        return copy
    }

    @JvmStatic
    fun createCreativeVisualStack(
        item: ServerSideItem,
        original: ItemStack,
        player: ServerPlayerEntity,
        renderType: RenderType,
    ): ItemStack {
        val visualStack = item.createVisualStack(original, player, renderType)
        val nbt = visualStack.orCreateNbt
        nbt.putString(INTERNAL_ID_KEY, Registry.ITEM.getId(original.item).toString())
        val nbtString = original.nbt?.let(::encodeNbt)
        if (nbtString != null) {
            nbt.putString(INTERNAL_NBT_KEY, nbtString)
        }
        return visualStack
    }

    private fun encodeNbt(nbt: NbtCompound): String {
        val string = NbtOrderedStringFormatter().apply(nbt)
        return Base64.getEncoder().encodeToString(string.encodeToByteArray())
    }

    private fun decodeNbt(base64: String): NbtCompound {
        val decoded = Base64.getDecoder().decode(base64).toString(StandardCharsets.UTF_8)
        return StringNbtReader.parse(decoded)
    }
}
