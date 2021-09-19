package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.entity.EntityDamageByEntityEvent;
import dev.uten2c.strobo.event.entity.EntityDamageEvent;
import dev.uten2c.strobo.event.entity.EntityDeathEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void callEntityDamageEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        EntityDamageEvent damageEvent = new EntityDamageEvent(entity, amount, source);
        if (!damageEvent.callEvent()) {
            cir.setReturnValue(false);
        }
        Entity attacker = source.getAttacker();
        if (attacker != null) {
            EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(entity, amount, source, attacker);
            if (!damageByEntityEvent.callEvent()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setPose(Lnet/minecraft/entity/EntityPose;)V", shift = At.Shift.AFTER))
    private void callDeathEvent(DamageSource source, CallbackInfo ci) {
        new EntityDeathEvent((LivingEntity) (Object) this).callEvent();
    }
}
