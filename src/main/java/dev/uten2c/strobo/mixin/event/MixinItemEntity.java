package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.player.PlayerAttemptPickupItemEvent;
import dev.uten2c.strobo.util.IPlayerInventory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    private int pickupDelay;

    // PlayerAttemptPickupItemEventを呼び出してる
    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", shift = At.Shift.AFTER), cancellable = true)
    private void callPlayerAttemptPickupItemEvent(PlayerEntity player, CallbackInfo ci) {
        var stack = getStack();
        var count = stack.getCount();
        var canHold = ((IPlayerInventory) player.getInventory()).canHold(stack);
        var remaining = count - canHold;
        var flyAtPlayer = false;

        if (pickupDelay <= 0) {
            var event = new PlayerAttemptPickupItemEvent((ServerPlayerEntity) player, (ItemEntity) (Object) this, remaining);
            event.callEvent();

            flyAtPlayer = event.getFlyAtPlayer();
            if (event.isCancelled()) {
                if (flyAtPlayer) {
                    player.sendPickup((ItemEntity) (Object) this, count);
                }

                ci.cancel();
            }
        }
    }
}
