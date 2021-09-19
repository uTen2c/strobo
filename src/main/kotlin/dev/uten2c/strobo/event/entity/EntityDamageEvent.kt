package dev.uten2c.strobo.event.entity

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource

class EntityDamageEvent(val entity: LivingEntity, var amount: Float, val source: DamageSource) : CancellableEvent()