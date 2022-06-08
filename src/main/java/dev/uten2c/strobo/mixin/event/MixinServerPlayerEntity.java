package dev.uten2c.strobo.mixin.event;

import com.mojang.authlib.GameProfile;
import dev.uten2c.strobo.event.player.*;
import dev.uten2c.strobo.util.ItemStackKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

    @Shadow
    public abstract Entity getCameraEntity();

    @Shadow
    private Entity cameraEntity;

    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @Shadow
    public abstract void requestTeleport(double destX, double destY, double destZ);

    @Shadow
    public abstract boolean startRiding(Entity entity, boolean force);

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    // PlayerStartSpectatingEntityEventとPlayerStopSpectatingEntityEventを呼び出してる
    // @Overwrite
    @Inject(method = "setCameraEntity", at = @At("HEAD"), cancellable = true)
    public void callPlayerStartSpectatingEntityEventAndPlayerStopSpectatingEntityEvent(Entity newTargetEntity, CallbackInfo ci) {
        var currentTarget = this.getCameraEntity();
        newTargetEntity = newTargetEntity == null ? this : newTargetEntity;
        if (currentTarget == newTargetEntity) {
            ci.cancel();
        }

        var self = (ServerPlayerEntity) (Object) this;
        if (newTargetEntity == this) {
            var event = new PlayerStopSpectatingEntityEvent(self, currentTarget);
            if (!event.callEvent()) {
                ci.cancel();
            }
        } else {
            var event = new PlayerStartSpectatingEntityEvent(self, currentTarget, newTargetEntity);
            if (!event.callEvent()) {
                ci.cancel();
            }
        }

        cameraEntity = newTargetEntity;
        this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(cameraEntity));
        this.requestTeleport(cameraEntity.getX(), cameraEntity.getY(), cameraEntity.getZ());
        ci.cancel();
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;dropShoulderEntities()V"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        new PlayerDeathEvent((ServerPlayerEntity) (Object) this).callEvent();
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        var inventory = getInventory();
        var stack = inventory.getMainHandStack();
        var event = new PlayerDropSelectedItemEvent((ServerPlayerEntity) (Object) this, stack, entireStack);
        if (!event.callEvent()) {
            cir.cancel();
        }
    }

    public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
        var itemEntity = super.dropItem(stack, throwRandomly, retainOwnership);
        if (itemEntity == null) {
            return null;
        } else {
            // イベント部分
            var self = (ServerPlayerEntity) (Object) this;
            var event = new PlayerDropItemEvent(self, itemEntity);
            if (!event.callEvent()) {
                var inventory = self.getInventory();
                var current = inventory.getMainHandStack();
                if (retainOwnership && (current == null || current.getCount() == 0)) {
                    inventory.setStack(inventory.selectedSlot, stack);
                    strobo$sendSlotPacket(self, stack);
                } else if (retainOwnership && ItemStackKt.isSimilar(current, itemEntity.getStack()) && current.getCount() < current.getMaxCount() && itemEntity.getStack().getCount() == 1) {
                    current.increment(1);
                    strobo$sendSlotPacket(self, current);
                } else {
                    inventory.insertStack(stack);
                    playerScreenHandler.sendContentUpdates();
                }
                return null;
            }
            itemEntity = event.getItem();
            // イベント部分　おわり

            this.world.spawnEntity(itemEntity);
            var itemStack = itemEntity.getStack();
            if (retainOwnership) {
                if (!itemStack.isEmpty()) {
                    this.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), stack.getCount());
                }

                this.incrementStat(Stats.DROP);
            }

            return itemEntity;
        }
    }

    private static void strobo$sendSlotPacket(ServerPlayerEntity player, ItemStack stack) {
        player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(
                player.playerScreenHandler.syncId,
                player.playerScreenHandler.getRevision(),
                player.getInventory().selectedSlot + 36,
                stack
        ));
    }
}
