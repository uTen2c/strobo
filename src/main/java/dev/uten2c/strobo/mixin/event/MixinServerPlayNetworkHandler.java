package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.player.*;
import dev.uten2c.strobo.util.EntityKt;
import dev.uten2c.strobo.util.Location;
import dev.uten2c.strobo.util.ServerPlayerEntityKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler implements ServerPlayPacketListener {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    public abstract void disconnect(Text reason);

    @Shadow
    private int ticks;

    @Shadow
    public abstract void syncWithPlayerPosition();

    @Shadow
    @Nullable
    private Vec3d requestedTeleportPos;
    @Shadow
    private int teleportRequestTick;

    @Shadow
    private double lastTickX;
    @Shadow
    private double lastTickY;
    @Shadow
    private double lastTickZ;

    @Shadow
    public abstract void requestTeleport(double x, double y, double z, float yaw, float pitch);

    @Shadow
    private int movePacketsCount;
    @Shadow
    private int lastTickMovePacketsCount;

    @Shadow
    protected abstract boolean isHost();

    @Shadow
    private double updatedX;
    @Shadow
    private double updatedY;
    @Shadow
    private double updatedZ;

    @Shadow
    protected abstract boolean isPlayerNotCollidingWithBlocks(WorldView world, Box box);

    @Shadow
    private boolean floating;
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    protected abstract boolean isEntityOnAir(Entity entity);

    @Shadow
    private static double clampHorizontal(double d) {
        return 0;
    }

    @Shadow
    private static double clampVertical(double d) {
        return 0;
    }

    @Shadow
    public abstract void requestTeleportAndDismount(double x, double y, double z, float yaw, float pitch);

    @Shadow
    private static boolean isMovementInvalid(double x, double y, double z, float yaw, float pitch) {
        return false;
    }

    @Shadow
    @Final
    static Logger LOGGER;

    private double strobo$lastPosX = Double.MAX_VALUE;
    private double strobo$lastPosY = Double.MAX_VALUE;
    private double strobo$lastPosZ = Double.MAX_VALUE;
    private float strobo$lastYaw = Float.MAX_VALUE;
    private float strobo$lastPitch = Float.MAX_VALUE;
    private boolean strobo$justTeleported;
    private int strobo$allowedPlayerTicks = 1;
    private int strobo$lastTick = 0;

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
    private void onDisconnected(PlayerManager playerManager, Text message, RegistryKey<MessageType> typeKey) {
        var event = new PlayerQuitEvent(player, message);
        event.callEvent();
        playerManager.broadcast(event.getMessage(), typeKey);
    }

    // PlayerMoveEventを呼び出してる。PaperSpigotから移植
    // @Overwrite
    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    public void callPlayerMoveEvent(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getWorld());
        if (isMovementInvalid(packet.getX(0.0D), packet.getY(0.0D), packet.getZ(0.0D), packet.getYaw(0.0F), packet.getPitch(0.0F))) {
            this.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
        } else {
            var serverWorld = this.player.getWorld();

            if (!this.player.notInAnyWorld && !this.player.isImmobile()) { // CraftBukkit
                if (this.ticks == 0) {
                    this.syncWithPlayerPosition();
                }

                if (this.requestedTeleportPos != null) {
                    if (this.ticks - this.teleportRequestTick > 20) {
                        this.teleportRequestTick = this.ticks;
                        this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch());
                    }
                    this.strobo$allowedPlayerTicks = 20; // CraftBukkit
                } else {
                    this.teleportRequestTick = this.ticks;
                    var d0 = clampHorizontal(packet.getX(this.player.getX()));
                    var d1 = clampVertical(packet.getY(this.player.getY()));
                    var d2 = clampHorizontal(packet.getZ(this.player.getZ()));
                    var g = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
                    var h = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));

                    if (this.player.hasVehicle()) {
                        this.player.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
                        this.player.getWorld().getChunkManager().updatePosition(this.player);
                        this.strobo$allowedPlayerTicks = 20; // CraftBukkit
                    } else {
                        // CraftBukkit - Make sure the move is valid but then reset it for plugins to modify
                        var prevX = this.player.getX();
                        var prevY = this.player.getY();
                        var prevZ = this.player.getZ();
                        var prevYaw = this.player.getYaw();
                        var prevPitch = this.player.getPitch();
                        // CraftBukkit end
                        var toX = this.player.getX();
                        var toY = this.player.getY();
                        var toZ = this.player.getZ();
                        var l = this.player.getY();
                        var d7 = d0 - this.lastTickX;
                        var d8 = d1 - this.lastTickY;
                        var d9 = d2 - this.lastTickZ;
                        var d10 = this.player.getVelocity().lengthSquared();
                        // Paper start - fix large move vectors killing the server
                        var currDeltaX = toX - prevX;
                        var currDeltaY = toY - prevY;
                        var currDeltaZ = toZ - prevZ;
                        var d11 = Math.max(d7 * d7 + d8 * d8 + d9 * d9, (currDeltaX * currDeltaX + currDeltaY * currDeltaY + currDeltaZ * currDeltaZ) - 1);
                        // Paper end - fix large move vectors killing the server
                        // Paper start - fix large move vectors killing the server
                        var otherFieldX = d0 - this.updatedX;
                        var otherFieldY = d1 - this.updatedY;
                        var otherFieldZ = d2 - this.updatedZ;
                        d11 = Math.max(d11, (otherFieldX * otherFieldX + otherFieldY * otherFieldY + otherFieldZ * otherFieldZ) - 1);
                        // Paper end - fix large move vectors killing the server

                        if (this.player.isSleeping()) {
                            if (d11 > 1.0D) {
                                this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
                            }

                        } else {
                            ++this.movePacketsCount;
                            int r = this.movePacketsCount - this.lastTickMovePacketsCount;

                            // CraftBukkit start - handle custom speeds and skipped ticks
                            this.strobo$allowedPlayerTicks += (System.currentTimeMillis() / 50) - this.strobo$lastTick;
                            this.strobo$allowedPlayerTicks = Math.max(this.strobo$allowedPlayerTicks, 1);
                            this.strobo$lastTick = (int) (System.currentTimeMillis() / 50);

                            if (r > Math.max(this.strobo$allowedPlayerTicks, 5)) {
                                LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), r);
                                r = 1;
                            }

                            if (packet.changesLook() || d11 > 0) {
                                this.strobo$allowedPlayerTicks -= 1;
                            } else {
                                this.strobo$allowedPlayerTicks = 20;
                            }
                            double speed;
                            if (this.player.getAbilities().flying) {
                                speed = this.player.getAbilities().getFlySpeed() * 20f;
                            } else {
                                speed = this.player.getAbilities().getWalkSpeed() * 10f;
                            }
                            // Paper start - Prevent moving into unloaded chunks // Strobo 使わない
                            // if (player.level.paperConfig.preventMovingIntoUnloadedChunks && (this.player.getX() != toX || this.player.getZ() != toZ) && worldserver.getChunkIfLoadedImmediately((int) Math.floor(toX) >> 4, (int) Math.floor(toZ) >> 4) == null) { // Paper - use getIfLoadedImmediately
                            //     this.internalTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYRot(), this.player.getXRot(), Collections.emptySet(), true);
                            //     return;
                            // }
                            // Paper end

                            if (!this.player.isInTeleportationState() && (!this.player.getWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
                                var f2 = this.player.isFallFlying() ? 300.0F : 100.0F;

                                if (d11 - d10 > Math.max(f2, Math.pow(10.0 * (float) r * speed, 2)) && !this.isHost()) {
                                    // CraftBukkit end
                                    LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), d7, d8, d9);
                                    this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYaw(), this.player.getPitch());
                                    ci.cancel();
                                }
                            }

                            var box = this.player.getBoundingBox();

                            d7 = d0 - this.updatedX; // Paper - diff on change, used for checking large move vectors above
                            d8 = d1 - this.updatedY; // Paper - diff on change, used for checking large move vectors above
                            d9 = d2 - this.updatedZ; // Paper - diff on change, used for checking large move vectors above
                            var flag = d8 > 0.0D;

                            if (this.player.isOnGround() && !packet.isOnGround() && flag) {
                                this.player.jump();
                            }

                            this.player.move(MovementType.PLAYER, new Vec3d(d7, d8, d9));
                            this.player.setOnGround(packet.isOnGround()); // CraftBukkit - SPIGOT-5810, SPIGOT-5835: reset by this.player.move
                            // Paper start - prevent position desync
                            if (this.requestedTeleportPos != null) {
                                ci.cancel(); // ... thanks Mojang for letting move calls teleport across dimensions.
                            }
                            // Paper end - prevent position desync
                            var d12 = d8;

                            d7 = d0 - this.player.getX();
                            d8 = d1 - this.player.getY();
                            if (d8 > -0.5D || d8 < 0.5D) {
                                d8 = 0.0D;
                            }

                            d9 = d2 - this.player.getZ();
                            d11 = d7 * d7 + d8 * d8 + d9 * d9;
                            var flag1 = false;

                            if (!this.player.isInTeleportationState() && d11 > 0.0625D && !this.player.isSleeping() && !this.player.interactionManager.isCreative() && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
                                flag1 = true;
                                LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                            }

                            this.player.updatePositionAndAngles(d0, d1, d2, g, h);
                            if (this.player.noClip || this.player.isSleeping() || (!flag1 || !serverWorld.isSpaceEmpty(this.player, box)) && !this.isPlayerNotCollidingWithBlocks(serverWorld, box)) {
                                // CraftBukkit start - fire PlayerMoveEvent
                                // Rest to old location first
                                this.player.updatePositionAndAngles(prevX, prevY, prevZ, prevYaw, prevPitch);

                                Location from = new Location(strobo$lastPosX, strobo$lastPosY, strobo$lastPosZ, strobo$lastYaw, strobo$lastPitch); // Get the Players previous Event location.
                                Location to = EntityKt.getLocation(player); // Start off the To location as the Players current location.

                                // If the packet contains movement information then we update the To location with the correct XYZ.
                                if (packet.changesPosition()) {
                                    to.setX(packet.x);
                                    to.setY(packet.y);
                                    to.setZ(packet.z);
                                }

                                // If the packet contains look information then we update the To location with the correct Yaw & Pitch.
                                if (packet.changesLook()) {
                                    to.setYaw(packet.yaw);
                                    to.setPitch(packet.pitch);
                                }

                                // Prevent 40 event-calls for less than a single pixel of movement >.>
                                var delta = Math.pow(this.strobo$lastPosX - to.getX(), 2) + Math.pow(this.strobo$lastPosY - to.getY(), 2) + Math.pow(this.strobo$lastPosZ - to.getZ(), 2);
                                var deltaAngle = Math.abs(this.strobo$lastYaw - to.yaw) + Math.abs(this.strobo$lastPitch - to.pitch);

                                if ((delta > 1f / 16384 || deltaAngle > 1f) && !this.player.isImmobile()) {
                                    this.strobo$lastPosX = to.getX();
                                    this.strobo$lastPosY = to.getY();
                                    this.strobo$lastPosZ = to.getZ();
                                    this.strobo$lastYaw = to.yaw;
                                    this.strobo$lastPitch = to.pitch;

                                    // Skip the first time we do this
                                    if (from.getX() != Double.MAX_VALUE) {
                                        Location oldTo = to.clone();
                                        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
                                        event.callEvent();

                                        // If the event is cancelled we move the player back to their old location.
                                        if (event.isCancelled()) {
                                            this.requestTeleportAndDismount(from.x, from.y, from.z, from.yaw, from.pitch);
                                            ci.cancel();
                                        }

                                        // If a Plugin has changed the To destination then we teleport the Player
                                        // there to avoid any 'Moved wrongly' or 'Moved too quickly' errors.
                                        // We only do this if the Event was not cancelled.
                                        if (!oldTo.equals(event.getTo()) && !event.isCancelled()) {
                                            // this.player.getBukkitEntity().teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN); // Strobo - Paperの実装を変える
                                            ServerPlayerEntityKt.bukkitTp(this.player, event.getTo());
                                            ci.cancel();
                                        }

                                        // Check to see if the Players Location has some how changed during the call of the event.
                                        // This can happen due to a plugin teleporting the player instead of using .setTo()
                                        if (!from.equals(EntityKt.getLocation(this.player)) && this.strobo$justTeleported) {
                                            this.strobo$justTeleported = false;
                                            ci.cancel();
                                        }
                                    }
                                }
                                this.player.updatePositionAndAngles(d0, d1, d2, g, h);

                                // MC-135989, SPIGOT-5564: isRiptiding
                                this.floating = d12 >= -0.03125D && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR && !this.server.isFlightEnabled() && !this.player.getAbilities().allowFlying && !this.player.hasStatusEffect(StatusEffects.LEVITATION) && !this.player.isFallFlying() && this.isEntityOnAir(this.player) && !this.player.isUsingRiptide();
                                // CraftBukkit end
                                this.player.getWorld().getChunkManager().updatePosition(this.player);
                                this.player.handleFall(this.player.getY() - l, packet.isOnGround());
                                // this.player.setOnGround(packet.isOnGround()); // CraftBukkit - moved up
                                if (flag) {
                                    this.player.fallDistance = 0.0F;
                                }

                                this.player.increaseTravelMotionStats(this.player.getX() - toX, this.player.getY() - toY, this.player.getZ() - toZ);
                                this.updatedX = this.player.getX();
                                this.updatedY = this.player.getY();
                                this.updatedZ = this.player.getZ();
                            } else {
                                this.requestTeleport(toX, toY, toZ, g, h);
                            }
                        }
                    }
                }
            }
        }
        ci.cancel();
    }

    // PaperSpigotから移植
    @Inject(method = "requestTeleport(DDDFFLjava/util/Set;Z)V", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;ticks:I"))
    private void setLastPos(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, boolean shouldDismount, CallbackInfo ci) {
        strobo$justTeleported = true;
        if (requestedTeleportPos != null) {
            strobo$lastPosX = requestedTeleportPos.x;
            strobo$lastPosY = requestedTeleportPos.y;
            strobo$lastPosZ = requestedTeleportPos.z;
            strobo$lastYaw = yaw;
            strobo$lastPitch = pitch;
        }
    }

    @Inject(method = "onUpdateSelectedSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getHotbarSize()I", shift = At.Shift.AFTER))
    private void callPlayerItemHeldEvent(UpdateSelectedSlotC2SPacket packet, CallbackInfo ci) {
        new PlayerItemHeldEvent(player, player.getInventory().selectedSlot, packet.getSelectedSlot()).callEvent();
    }

    @Inject(method = "onPlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private void callPlayerSwapHandItemsEvent(PlayerActionC2SPacket packet, CallbackInfo ci) {
        var mainHandStack = player.getMainHandStack();
        var offHandStack = player.getOffHandStack();
        var event = new PlayerSwapHandItemsEvent(player, mainHandStack, offHandStack);
        if (!event.callEvent()) {
            ci.cancel();
        }
    }

    @Redirect(method = "onClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSprinting(Z)V"))
    private void callPlayerToggleSprintEvent(ServerPlayerEntity player, boolean isSprinting) {
        new PlayerToggleSprintEvent(player, isSprinting).callEvent();
        player.setSprinting(isSprinting);
    }
}
