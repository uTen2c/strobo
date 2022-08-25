package dev.uten2c.strobo.screen

import com.google.common.collect.HashBiMap
import dev.uten2c.strobo.Strobo
import dev.uten2c.strobo.util.customModelData
import dev.uten2c.strobo.util.text
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.collection.DefaultedList
import net.minecraft.screen.slot.Slot as MinecraftSlot

/**
 * [ScreenHandler]をサーバーサイドでいい感じにできるように拡張している
 * @param type ScreenHandlerのタイプ
 * @param syncId クライアントとやり取りするためのID
 */
abstract class StroboScreenHandler(type: ScreenHandlerType<*>, syncId: Int) : ScreenHandler(type, syncId) {

    private val blankInventory = BlankInventory()
    private val blankSlot = ImmutableSlot(blankInventory, 0)
    private val updateTasks = HashMap<ServerPlayerEntity, Job>()

    val rows = getRowsByType(type)
    val size = rows * 9

    /**
     * [ScreenHandler]をサーバーサイドでいい感じにできるように拡張している
     * @param rows チェストタイプの[ScreenHandler]における行数
     * @param syncId クライアントとやり取りするためのID
     */
    constructor(rows: Int, syncId: Int) : this(getTypeByRows(rows), syncId)

    override fun canUse(player: PlayerEntity?): Boolean = true

    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (index < rows * 9) {
                if (!insertItemCheckCanInsert(itemStack2, rows * 9, slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItemCheckCanInsert(itemStack2, 0, rows * 9, false)) {
                return ItemStack.EMPTY
            }
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return itemStack!!
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) {
        super.onSlotClick(slotIndex, button, actionType, player)
        if (player !is ServerPlayerEntity) {
            return
        }

        sendContentUpdates()
    }

    override fun close(player: PlayerEntity) {
        if (player is ServerPlayerEntity) {
            updateTasks[player]?.cancel()
            updateTasks.remove(player)
            val packet = InventoryS2CPacket(
                player.playerScreenHandler.syncId,
                player.playerScreenHandler.nextRevision(),
                player.playerScreenHandler.stacks,
                player.playerScreenHandler.cursorStack,
            )
            player.networkHandler.sendPacket(packet)
        }
        super.close(player)
    }

    override fun addListener(listener: ScreenHandlerListener) {
        super.addListener(listener)
        if (listener is StroboScreenHandlerListener) {
            updateTasks[listener.player] = Strobo.scope.launch {
                while (true) {
                    updateScreenHandler(listener.player)
                    delay(50)
                }
            }
        }
    }

    /**
     * プレイヤーのインベントリー領域にあたるスロットを追加する
     * @param playerInventory 対象のプレイヤーのインベントリー
     * @param canEdit プレイヤーインベントリーを操作できるか
     */
    protected fun initPlayerInventorySlots(playerInventory: PlayerInventory, canEdit: Boolean) {
        repeat(3) { y ->
            repeat(9) { x ->
                addSlot(
                    object : Slot(playerInventory, x + y * 9 + 9) {
                        override fun canInsert(stack: ItemStack?): Boolean = canEdit
                        override fun canTakeItems(playerEntity: PlayerEntity?): Boolean = canEdit
                    },
                )
            }
        }
        repeat(9) { x ->
            addSlot(
                object : Slot(playerInventory, x) {
                    override fun canInsert(stack: ItemStack?): Boolean = canEdit
                    override fun canTakeItems(playerEntity: PlayerEntity?): Boolean = canEdit
                },
            )
        }
    }

    private fun updateScreenHandler(player: ServerPlayerEntity) {
        val list = if (cursorStack.isEmpty) {
            stacks
        } else {
            val array = slots.map {
                if (it.inventory == player.inventory && it.index == player.inventory.selectedSlot) {
                    return@map it.stack
                }
                if (!it.canInsert(cursorStack) && !it.hasStack()) {
                    return@map disabledSlotStack
                }
                it.stack
            }.toTypedArray()
            DefaultedList.copyOf(ItemStack.EMPTY, *array)
        }
        val packet = InventoryS2CPacket(syncId, nextRevision(), list, cursorStack)
        player.networkHandler.sendPacket(packet)
    }

    /**
     * 操作不可のスロットを追加
     */
    protected fun addBlankSlot(): MinecraftSlot = addSlot(blankSlot)

    /**
     * バニラの[ScreenHandler.insertItem]は対象のスロットがからの場合は[Slot.canInsert]を判定するが、元々同じ種類のアイテムが入っていた場合は[Slot.canInsert]を判定しないでそのまま個数を書き換えてしまうのでその修正
     */
    protected fun insertItemCheckCanInsert(
        stack: ItemStack,
        startIndex: Int,
        endIndex: Int,
        fromLast: Boolean,
    ): Boolean {
        var result = false
        var index = startIndex
        if (fromLast) {
            index = endIndex - 1
        }
        var slot: MinecraftSlot
        var itemStack: ItemStack
        if (stack.isStackable) {
            while (!stack.isEmpty) {
                if (fromLast) {
                    if (index < startIndex) {
                        break
                    }
                } else if (index >= endIndex) {
                    break
                }
                slot = slots[index]
                itemStack = slot.stack
                if (!slot.canInsert(itemStack)) {
                    break
                }
                if (!itemStack.isEmpty && ItemStack.canCombine(stack, itemStack)) {
                    val j = itemStack.count + stack.count
                    if (j <= stack.maxCount) {
                        stack.count = 0
                        itemStack.count = j
                        slot.markDirty()
                        result = true
                    } else if (itemStack.count < stack.maxCount) {
                        stack.decrement(stack.maxCount - itemStack.count)
                        itemStack.count = stack.maxCount
                        slot.markDirty()
                        result = true
                    }
                }
                if (fromLast) {
                    --index
                } else {
                    ++index
                }
            }
        }
        if (!stack.isEmpty) {
            index = if (fromLast) {
                endIndex - 1
            } else {
                startIndex
            }
            while (true) {
                if (fromLast) {
                    if (index < startIndex) {
                        break
                    }
                } else if (index >= endIndex) {
                    break
                }
                slot = slots[index]
                itemStack = slot.stack
                if (itemStack.isEmpty && slot.canInsert(stack)) {
                    if (stack.count > slot.maxItemCount) {
                        slot.stack = stack.split(slot.maxItemCount)
                    } else {
                        slot.stack = stack.split(stack.count)
                    }
                    slot.markDirty()
                    result = true
                    break
                }
                if (fromLast) {
                    --index
                } else {
                    ++index
                }
            }
        }
        return result
    }

    companion object {
        private val typeMap = HashBiMap.create(
            mapOf(
                1 to ScreenHandlerType.GENERIC_9X1,
                2 to ScreenHandlerType.GENERIC_9X2,
                3 to ScreenHandlerType.GENERIC_9X3,
                4 to ScreenHandlerType.GENERIC_9X4,
                5 to ScreenHandlerType.GENERIC_9X5,
                6 to ScreenHandlerType.GENERIC_9X6,
            ),
        )

        /**
         * 操作不可を表すアイテム
         * 黒いステンドグラスパネルでCustomModelDataは1
         * リソースパックでこのアイテムを透明にする必要がある
         */
        val disabledSlotStack
            get() = ItemStack(Items.BLACK_STAINED_GLASS_PANE).apply {
                setCustomName(text(""))
                customModelData = 1
            }

        private fun getTypeByRows(rows: Int): ScreenHandlerType<*> = typeMap.getOrDefault(
            rows,
            ScreenHandlerType.GENERIC_9X1,
        )

        private fun getRowsByType(type: ScreenHandlerType<*>): Int = typeMap.inverse().getOrDefault(type, 1)
    }

    /**
     * [MinecraftSlot]から座標データを省いたもの
     * サーバーサイドでは座標を変えることができないので設定する必要がない
     * @param inventory インベントリー
     * @param index インデックス
     */
    protected open class Slot(inventory: Inventory, index: Int) : MinecraftSlot(inventory, index, 0, 0)

    /**
     * 操作できないスロット
     * @param inventory インベントリー
     * @param index インデックス
     */
    protected open class ImmutableSlot(inventory: Inventory, index: Int) : Slot(inventory, index) {
        override fun canInsert(stack: ItemStack?): Boolean = false
        override fun canTakeItems(playerEntity: PlayerEntity?): Boolean = false
    }

    /**
     * 操作不可領域に用いる[SimpleInventory]の拡張
     */
    protected class BlankInventory : SimpleInventory(1) {
        override fun setStack(slot: Int, stack: ItemStack?) = super.setStack(0, stack)
        override fun getStack(slot: Int): ItemStack = super.getStack(0)
    }
}
