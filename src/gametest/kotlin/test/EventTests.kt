package test

import dev.uten2c.strobo.event.block.BlockBreakEvent
import dev.uten2c.strobo.event.entity.EntityDamageByEntityEvent
import dev.uten2c.strobo.event.entity.EntityDamageEvent
import dev.uten2c.strobo.event.entity.EntityDeathEvent
import dev.uten2c.strobo.event.listenEvent
import dev.uten2c.strobo.event.player.PlayerAttemptPickupItemEvent
import dev.uten2c.strobo.event.player.PlayerDeathEvent
import dev.uten2c.strobo.event.player.PlayerDropItemEvent
import dev.uten2c.strobo.event.player.PlayerItemHeldEvent
import dev.uten2c.strobo.util.isSimilar
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest.EMPTY_STRUCTURE
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.test.GameTest
import net.minecraft.test.TestContext
import net.minecraft.util.math.BlockPos
import java.util.UUID

class EventTests : StroboGameTest() {

    @GameTest(structureName = EMPTY_STRUCTURE)
    fun blockBreakEvent(context: TestContext) {
        val pos = BlockPos(0, 1, 0)
        val absolutePos = context.getAbsolutePos(pos)
        context.setBlockState(pos, Blocks.DIAMOND_BLOCK)
        listenEvent<BlockBreakEvent> { e ->
            if (e.pos == absolutePos) {
                e.eventListener.unlisten()
                if (e.state != context.getBlockState(pos)) {
                    context.wrappedFail("壊したブロックとイベントのブロックが異なっている", pos)
                    return@listenEvent
                }
                e.isCancelled = true
                context.setBlockState(pos, Blocks.GOLD_BLOCK)
            }
        }

        context.createMockServerPlayer().interactionManager.tryBreakBlock(absolutePos)

        context.addInstantFinalTask {
            context.checkBlock(pos, { block -> block == Blocks.GOLD_BLOCK }, "金ブロックを期待してる")
        }
    }

    @GameTest(structureName = EMPTY_STRUCTURE, tickLimit = 1)
    fun entityDamageByEntityEventTest(context: TestContext) {
        val mockPlayer = context.createMockPlayer()
        val entity = context.spawnEntity(EntityType.PIG, BlockPos(0, 1, 0))

        listenEvent<EntityDamageByEntityEvent> { e ->
            if (e.entity.uuid == entity.uuid) {
                e.eventListener.unlisten()
                if (e.attacker.uuid != mockPlayer.uuid) {
                    context.wrappedFail("攻撃したエンティティが異なる", entity)
                    entity.discard()
                    return@listenEvent
                }
                if (e.source.attacker != mockPlayer) {
                    context.wrappedFail("ダメージソースが異なる", entity)
                    entity.discard()
                    return@listenEvent
                }
                e.isCancelled = true
                e.entity.health = 1f

                context.addInstantFinalTask {
                    val health = entity.health
                    entity.discard()
                    if (health == 1f) {
                        context.complete()
                    } else {
                        context.throwGameTestException("イベントが正常に処理されていない")
                    }
                }
            }
        }

        mockPlayer.attack(entity)
    }

    @GameTest(structureName = EMPTY_STRUCTURE, tickLimit = 1)
    fun entityDamageEventTest(context: TestContext) {
        val entity = context.spawnEntity(EntityType.PIG, BlockPos(0, 1, 0))
        val source = DamageSource.ANVIL
        val damageAmount = 1f
        listenEvent<EntityDamageEvent> { e ->
            if (e.entity.uuid == entity.uuid) {
                e.eventListener.unlisten()
                if (e.source != source) {
                    context.wrappedFail("ダメージソースが異なる", entity)
                    entity.discard()
                    return@listenEvent
                }
                if (e.amount != damageAmount) {
                    context.wrappedFail("ダメージ量が異なる", entity)
                    entity.discard()
                    return@listenEvent
                }

                context.addInstantFinalTask {
                    if (entity.health == entity.maxHealth - damageAmount) {
                        entity.discard()
                        context.complete()
                    } else {
                        context.throwGameTestException("イベントが正常に処理されていない")
                    }
                }
            }
        }

        entity.damage(source, damageAmount)
    }

    @GameTest(structureName = EMPTY_STRUCTURE, tickLimit = 1)
    fun entityDeathEventTest(context: TestContext) {
        val entity = context.spawnEntity(EntityType.SILVERFISH, BlockPos(0, 1, 0))
        listenEvent<EntityDeathEvent> { e ->
            if (e.entity == entity) {
                e.eventListener.unlisten()
                context.complete()
            }
        }

        entity.kill()
    }

    @GameTest(structureName = EMPTY_STRUCTURE, tickLimit = 1)
    fun playerAttemptPickupItemEvent(context: TestContext) {
        val mockPlayer = context.createMockServerPlayer()
        val itemEntity = context.spawnItem(
            Items.DIAMOND,
            mockPlayer.pos.x.toFloat(),
            mockPlayer.pos.y.toFloat(),
            mockPlayer.pos.z.toFloat(),
        )
        itemEntity.stack.count = 2
        listenEvent<PlayerAttemptPickupItemEvent> { e ->
            if (e.player == mockPlayer) {
                e.eventListener.unlisten()

                if (e.entity != itemEntity) {
                    context.wrappedFail("異なるItemEntity", e.entity)
                    itemEntity.discard()
                    return@listenEvent
                }
                if (e.remaining != 0) {
                    context.wrappedFail("異なる残り個数", e.entity)
                    itemEntity.discard()
                    return@listenEvent
                }

                context.addInstantFinalTask {
                    context.complete()
                }
            }
        }

        itemEntity.onPlayerCollision(mockPlayer)
    }

    @GameTest(structureName = EMPTY_STRUCTURE, tickLimit = 1)
    fun playerDeathEventTest(context: TestContext) {
        val mockPlayer = context.createMockServerPlayer()
        listenEvent<PlayerDeathEvent> { e ->
            if (e.player == mockPlayer) {
                e.eventListener.unlisten()
                context.complete()
            }
        }

        mockPlayer.kill()
    }

    @GameTest(structureName = EMPTY_STRUCTURE, tickLimit = 1)
    fun playerDropItemEventTest(context: TestContext) {
        val mockPlayer = context.createMockServerPlayer()
        val defaultStack = Items.DIAMOND.defaultStack.apply {
            orCreateNbt.putUuid("uuid", UUID.randomUUID())
        }
        listenEvent<PlayerDropItemEvent> { e ->
            if (e.player == mockPlayer) {
                e.eventListener.unlisten()
                if (!e.item.stack.isSimilar(defaultStack)) {
                    context.wrappedFail("イベントのItemEntityと異なる")
                    e.item.discard()
                    return@listenEvent
                }

                context.complete()
            }
        }

        mockPlayer.dropItem(defaultStack, false, true)
    }

    @GameTest(structureName = EMPTY_STRUCTURE, tickLimit = 1)
    fun playerItemHeldEventTest(context: TestContext) {
        val mockPlayer = context.createMockServerPlayer()
        listenEvent<PlayerItemHeldEvent> { e ->
            if (e.player == mockPlayer) {
                e.eventListener.unlisten()
                if (e.prev != 0) {
                    context.wrappedFail("以前のスロット番号が期待と異なる")
                    return@listenEvent
                }
                if (e.new != 2) {
                    context.wrappedFail("新たなスロット番号が期待と異なる")
                    return@listenEvent
                }
                context.complete()
            }
        }

        mockPlayer.networkHandler.onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket(2))
    }
}
