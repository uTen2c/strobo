package dev.uten2c.strobo.event.entity

import dev.uten2c.strobo.event.Event
import net.minecraft.entity.LivingEntity

class EntityDeathEvent(val entity: LivingEntity) : Event()