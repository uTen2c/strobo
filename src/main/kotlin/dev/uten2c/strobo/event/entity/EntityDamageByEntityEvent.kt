package dev.uten2c.strobo.event.entity

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource

class EntityDamageByEntityEvent(val entity: LivingEntity, var amount: Float, val source: DamageSource, val attacker: Entity) : CancellableEvent()