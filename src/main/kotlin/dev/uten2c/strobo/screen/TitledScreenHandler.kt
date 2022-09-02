package dev.uten2c.strobo.screen

import dev.uten2c.strobo.util.emptyText
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

/**
 * タイトルを動的に変更可能な[StroboScreenHandler]の拡張
 */
abstract class TitledScreenHandler(rows: Int, syncId: Int, protected val player: ServerPlayerEntity) :
    StroboScreenHandler(rows, syncId) {
    var title: Text = emptyText()
        protected set(value) {
            val shouldUpdate = field != value
            field = value
            if (shouldUpdate) {
                updateTitle()
            }
        }

    private fun updateTitle() {
        if (player.currentScreenHandler != this) {
            return
        }
        player.networkHandler.sendPacket(OpenScreenS2CPacket(syncId, type, title))
        player.networkHandler.sendPacket(InventoryS2CPacket(syncId, nextRevision(), stacks, cursorStack))
    }
}
