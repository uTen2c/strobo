package dev.uten2c.strobo.event.entity

import dev.uten2c.strobo.event.Event
import net.minecraft.entity.LivingEntity

/**
 * エンティティが死亡したときに呼び出される
 * @param entity 死んだエンティティ
 */
class EntityDeathEvent(val entity: LivingEntity) : Event()
