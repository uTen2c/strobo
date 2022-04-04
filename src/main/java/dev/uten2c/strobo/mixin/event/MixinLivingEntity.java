package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.entity.EntityDamageEvent;
import dev.uten2c.strobo.event.entity.EntityDeathEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    protected float lastDamageTaken;

    @Shadow
    public abstract boolean blockedByShield(DamageSource source);

    @Shadow
    protected abstract void damageShield(float amount);

    private float strobo$eventDamageAmount;
    private float strobo$lastDamageTaken;
    private int strobo$timeUntilRegen;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        strobo$eventDamageAmount = amount;
        strobo$lastDamageTaken = lastDamageTaken;
        strobo$timeUntilRegen = timeUntilRegen;
    }

    // applyDamageの後で行う
    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private void disableDamageShield(LivingEntity instance, float amount) {
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", ordinal = 0), cancellable = true)
    private void callEntityDamageEvent0(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        var entity = ((LivingEntity) (Object) this);
        var event = new EntityDamageEvent(entity, amount, source);
        if (!event.callEvent()) {
            cir.setReturnValue(false);
        }
        strobo$eventDamageAmount = event.getAmount();
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", ordinal = 1), cancellable = true)
    private void callEntityDamageEvent1(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        var entity = ((LivingEntity) (Object) this);
        var event = new EntityDamageEvent(entity, amount, source);
        if (!event.callEvent()) {
            lastDamageTaken = strobo$lastDamageTaken;
            timeUntilRegen = strobo$timeUntilRegen;
            cir.setReturnValue(false);
        }
        strobo$eventDamageAmount = event.getAmount();
    }

    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), index = 1)
    private float applyEventDamage(float amount) {
        return strobo$eventDamageAmount;
    }

    // 上で無効化していた盾へのダメージを入れる
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isFallingBlock()Z"))
    private void damageShield(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (0 < strobo$eventDamageAmount && blockedByShield(source)) {
            damageShield(strobo$eventDamageAmount);
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setPose(Lnet/minecraft/entity/EntityPose;)V", shift = At.Shift.AFTER))
    private void callDeathEvent(DamageSource source, CallbackInfo ci) {
        new EntityDeathEvent((LivingEntity) (Object) this).callEvent();
    }
}
