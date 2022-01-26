package dev.uten2c.strobo.event.entity

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource

/**
 * エンティティがエンティティにダメージを与えたときに呼び出される
 * @param entity 攻撃を受けたエンティティ
 * @param amount ダメージ量
 * @param source DamageSource
 * @param attacker 攻撃したエンティティ
 */
class EntityDamageByEntityEvent(
    val entity: LivingEntity,
    var amount: Float,
    val source: DamageSource,
    val attacker: Entity,
) : CancellableEvent()
