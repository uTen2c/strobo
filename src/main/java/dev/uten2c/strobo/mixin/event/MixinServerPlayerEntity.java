package dev.uten2c.strobo.mixin.event;

import com.mojang.authlib.GameProfile;
import dev.uten2c.strobo.event.player.PlayerDeathEvent;
import dev.uten2c.strobo.event.player.PlayerDropItemEvent;
import dev.uten2c.strobo.event.player.PlayerStartSpectatingEntityEvent;
import dev.uten2c.strobo.event.player.PlayerStopSpectatingEntityEvent;
import dev.uten2c.strobo.util.ItemStackKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    /**
     * @author uTen2c
     * @reason Call PlayerStartSpectatingEntityEvent and PlayerStopSpectatingEntityEvent
     */
    @Overwrite
    public void setCameraEntity(Entity newTargetEntity) {
        Entity currentTarget = this.getCameraEntity();
        newTargetEntity = newTargetEntity == null ? this : newTargetEntity;
        if (currentTarget == newTargetEntity) {
            return;
        }

        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        if (newTargetEntity == this) {
            PlayerStopSpectatingEntityEvent event = new PlayerStopSpectatingEntityEvent(self, currentTarget);
            if (!event.callEvent()) {
                return;
            }
        } else {
            PlayerStartSpectatingEntityEvent event = new PlayerStartSpectatingEntityEvent(self, currentTarget, newTargetEntity);
            if (!event.callEvent()) {
                return;
            }
        }

        cameraEntity = newTargetEntity;
        this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(cameraEntity));
        this.requestTeleport(cameraEntity.getX(), cameraEntity.getY(), cameraEntity.getZ());
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;dropShoulderEntities()V"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        new PlayerDeathEvent((ServerPlayerEntity) (Object) this).callEvent();
    }

    public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
        ItemEntity itemEntity = super.dropItem(stack, throwRandomly, retainOwnership);
        if (itemEntity == null) {
            return null;
        } else {
            // イベント部分
            ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
            PlayerDropItemEvent event = new PlayerDropItemEvent(self, itemEntity);
            if (!event.callEvent()) {
                PlayerInventory inventory = self.getInventory();
                ItemStack current = inventory.getMainHandStack();
                if (retainOwnership && (current == null || current.getCount() == 0)) {
                    inventory.setStack(inventory.selectedSlot, stack);
                    sendSlotPacket(self, stack);
                } else if (retainOwnership && ItemStackKt.isSimilar(current, itemEntity.getStack()) && current.getCount() < current.getMaxCount() && itemEntity.getStack().getCount() == 1) {
                    current.increment(1);
                    sendSlotPacket(self, current);
                } else {
                    inventory.insertStack(stack);
                    playerScreenHandler.sendContentUpdates();
                }
                return null;
            }
            itemEntity = event.getItem();
            // イベント部分　おわり

            this.world.spawnEntity(itemEntity);
            ItemStack itemStack = itemEntity.getStack();
            if (retainOwnership) {
                if (!itemStack.isEmpty()) {
                    this.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), stack.getCount());
                }

                this.incrementStat(Stats.DROP);
            }

            return itemEntity;
        }
    }

    private static void sendSlotPacket(ServerPlayerEntity player, ItemStack stack) {
        player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(
                player.playerScreenHandler.syncId,
                player.playerScreenHandler.getRevision(),
                player.getInventory().selectedSlot + 36,
                stack
        ));
    }
}
