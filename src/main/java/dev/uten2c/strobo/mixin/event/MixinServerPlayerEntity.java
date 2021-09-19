package dev.uten2c.strobo.mixin.event;

import com.mojang.authlib.GameProfile;
import dev.uten2c.strobo.event.player.PlayerStartSpectatingEntityEvent;
import dev.uten2c.strobo.event.player.PlayerStopSpectatingEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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
}
