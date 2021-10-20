package dev.uten2c.strobo.event.entity

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource

/**
 * エンティティがダメージを受けたときに呼び出される
 * @param entity ダメージを受けたエンティティ
 * @param amount ダメージ量
 * @param source DamageSource
 */
class EntityDamageEvent(val entity: LivingEntity, var amount: Float, val source: DamageSource) : CancellableEvent()
