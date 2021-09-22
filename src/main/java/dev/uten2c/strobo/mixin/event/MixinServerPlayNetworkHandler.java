package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.player.*;
import dev.uten2c.strobo.util.EntityKt;
import dev.uten2c.strobo.util.Location;
import dev.uten2c.strobo.util.ServerPlayerEntityKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldView;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

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
    @Final
    static Logger LOGGER;

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

    private double lastPosX = Double.MAX_VALUE;
    private double lastPosY = Double.MAX_VALUE;
    private double lastPosZ = Double.MAX_VALUE;
    private float lastYaw = Float.MAX_VALUE;
    private float lastPitch = Float.MAX_VALUE;
    private boolean justTeleported;
    private int allowedPlayerTicks = 1;
    private int lastTick = 0;

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void onQuit(PlayerManager playerManager, Text message, MessageType type, UUID senderUuid) {
        PlayerQuitEvent event = new PlayerQuitEvent(player, message);
        event.callEvent();
        playerManager.broadcastChatMessage(event.getMessage(), type, senderUuid);
    }

    /**
     * @author uTen2c
     * @reason onPlayerMove  - From Paper
     */
    @Overwrite
    public void onPlayerMove(PlayerMoveC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (isMovementInvalid(packet.getX(0.0D), packet.getY(0.0D), packet.getZ(0.0D), packet.getYaw(0.0F), packet.getPitch(0.0F))) {
            this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_player_movement"));
        } else {
            ServerWorld serverWorld = this.player.getServerWorld();

            if (!this.player.notInAnyWorld && !this.player.isImmobile()) { // CraftBukkit
                if (this.ticks == 0) {
                    this.syncWithPlayerPosition();
                }

                if (this.requestedTeleportPos != null) {
                    if (this.ticks - this.teleportRequestTick > 20) {
                        this.teleportRequestTick = this.ticks;
                        this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch());
                    }
                    this.allowedPlayerTicks = 20; // CraftBukkit
                } else {
                    this.teleportRequestTick = this.ticks;
                    double d0 = clampHorizontal(packet.getX(this.player.getX()));
                    double d1 = clampVertical(packet.getY(this.player.getY()));
                    double d2 = clampHorizontal(packet.getZ(this.player.getZ()));
                    float g = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
                    float h = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));

                    if (this.player.hasVehicle()) {
                        this.player.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
                        this.player.getServerWorld().getChunkManager().updatePosition(this.player);
                        this.allowedPlayerTicks = 20; // CraftBukkit
                    } else {
                        // CraftBukkit - Make sure the move is valid but then reset it for plugins to modify
                        double prevX = this.player.getX();
                        double prevY = this.player.getY();
                        double prevZ = this.player.getZ();
                        float prevYaw = this.player.getYaw();
                        float prevPitch = this.player.getPitch();
                        // CraftBukkit end
                        double toX = this.player.getX();
                        double toY = this.player.getY();
                        double toZ = this.player.getZ();
                        double l = this.player.getY();
                        double d7 = d0 - this.lastTickX;
                        double d8 = d1 - this.lastTickY;
                        double d9 = d2 - this.lastTickZ;
                        double d10 = this.player.getVelocity().lengthSquared();
                        // Paper start - fix large move vectors killing the server
                        double currDeltaX = toX - prevX;
                        double currDeltaY = toY - prevY;
                        double currDeltaZ = toZ - prevZ;
                        double d11 = Math.max(d7 * d7 + d8 * d8 + d9 * d9, (currDeltaX * currDeltaX + currDeltaY * currDeltaY + currDeltaZ * currDeltaZ) - 1);
                        // Paper end - fix large move vectors killing the server
                        // Paper start - fix large move vectors killing the server
                        double otherFieldX = d0 - this.updatedX;
                        double otherFieldY = d1 - this.updatedY;
                        double otherFieldZ = d2 - this.updatedZ;
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
                            this.allowedPlayerTicks += (System.currentTimeMillis() / 50) - this.lastTick;
                            this.allowedPlayerTicks = Math.max(this.allowedPlayerTicks, 1);
                            this.lastTick = (int) (System.currentTimeMillis() / 50);

                            if (r > Math.max(this.allowedPlayerTicks, 5)) {
                                LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), r);
                                r = 1;
                            }

                            if (packet.changesLook() || d11 > 0) {
                                this.allowedPlayerTicks -= 1;
                            } else {
                                this.allowedPlayerTicks = 20;
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

                            if (!this.player.isInTeleportationState() && (!this.player.getServerWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
                                float f2 = this.player.isFallFlying() ? 300.0F : 100.0F;

                                if (d11 - d10 > Math.max(f2, Math.pow(10.0 * (float) r * speed, 2)) && !this.isHost()) {
                                    // CraftBukkit end
                                    LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), d7, d8, d9);
                                    this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYaw(), this.player.getPitch());
                                    return;
                                }
                            }

                            Box box = this.player.getBoundingBox();

                            d7 = d0 - this.updatedX; // Paper - diff on change, used for checking large move vectors above
                            d8 = d1 - this.updatedY; // Paper - diff on change, used for checking large move vectors above
                            d9 = d2 - this.updatedZ; // Paper - diff on change, used for checking large move vectors above
                            boolean flag = d8 > 0.0D;

                            if (this.player.isOnGround() && !packet.isOnGround() && flag) {
                                this.player.jump();
                            }

                            this.player.move(MovementType.PLAYER, new Vec3d(d7, d8, d9));
                            this.player.setOnGround(packet.isOnGround()); // CraftBukkit - SPIGOT-5810, SPIGOT-5835: reset by this.player.move
                            // Paper start - prevent position desync
                            if (this.requestedTeleportPos != null) {
                                return; // ... thanks Mojang for letting move calls teleport across dimensions.
                            }
                            // Paper end - prevent position desync
                            double d12 = d8;

                            d7 = d0 - this.player.getX();
                            d8 = d1 - this.player.getY();
                            if (d8 > -0.5D || d8 < 0.5D) {
                                d8 = 0.0D;
                            }

                            d9 = d2 - this.player.getZ();
                            d11 = d7 * d7 + d8 * d8 + d9 * d9;
                            boolean flag1 = false;

                            if (!this.player.isInTeleportationState() && d11 > 0.0625D && !this.player.isSleeping() && !this.player.interactionManager.isCreative() && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
                                flag1 = true;
                                LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                            }

                            this.player.updatePositionAndAngles(d0, d1, d2, g, h);
                            if (this.player.noClip || this.player.isSleeping() || (!flag1 || !serverWorld.isSpaceEmpty(this.player, box)) && !this.isPlayerNotCollidingWithBlocks(serverWorld, box)) {
                                // CraftBukkit start - fire PlayerMoveEvent
                                // Rest to old location first
                                this.player.updatePositionAndAngles(prevX, prevY, prevZ, prevYaw, prevPitch);

                                Location from = new Location(lastPosX, lastPosY, lastPosZ, lastYaw, lastPitch); // Get the Players previous Event location.
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
                                double delta = Math.pow(this.lastPosX - to.getX(), 2) + Math.pow(this.lastPosY - to.getY(), 2) + Math.pow(this.lastPosZ - to.getZ(), 2);
                                float deltaAngle = Math.abs(this.lastYaw - to.yaw) + Math.abs(this.lastPitch - to.pitch);

                                if ((delta > 1f / 16384 || deltaAngle > 1f) && !this.player.isImmobile()) {
                                    this.lastPosX = to.getX();
                                    this.lastPosY = to.getY();
                                    this.lastPosZ = to.getZ();
                                    this.lastYaw = to.yaw;
                                    this.lastPitch = to.pitch;

                                    // Skip the first time we do this
                                    if (from.getX() != Double.MAX_VALUE) {
                                        Location oldTo = to.clone();
                                        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
                                        event.callEvent();

                                        // If the event is cancelled we move the player back to their old location.
                                        if (event.isCancelled()) {
                                            this.requestTeleportAndDismount(from.x, from.y, from.z, from.yaw, from.pitch);
                                            return;
                                        }

                                        // If a Plugin has changed the To destination then we teleport the Player
                                        // there to avoid any 'Moved wrongly' or 'Moved too quickly' errors.
                                        // We only do this if the Event was not cancelled.
                                        if (!oldTo.equals(event.getTo()) && !event.isCancelled()) {
                                            // this.player.getBukkitEntity().teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN); // Strobo - Paperの実装を変える
                                            ServerPlayerEntityKt.bukkitTp(this.player, event.getTo());
                                            return;
                                        }

                                        // Check to see if the Players Location has some how changed during the call of the event.
                                        // This can happen due to a plugin teleporting the player instead of using .setTo()
                                        if (!from.equals(EntityKt.getLocation(this.player)) && this.justTeleported) {
                                            this.justTeleported = false;
                                            return;
                                        }
                                    }
                                }
                                this.player.updatePositionAndAngles(d0, d1, d2, g, h);

                                // MC-135989, SPIGOT-5564: isRiptiding
                                this.floating = d12 >= -0.03125D && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR && !this.server.isFlightEnabled() && !this.player.getAbilities().allowFlying && !this.player.hasStatusEffect(StatusEffects.LEVITATION) && !this.player.isFallFlying() && this.isEntityOnAir(this.player) && !this.player.isUsingRiptide();
                                // CraftBukkit end
                                this.player.getServerWorld().getChunkManager().updatePosition(this.player);
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
    }

    // From Paper
    @Inject(method = "requestTeleport(DDDFFLjava/util/Set;Z)V", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;ticks:I"))
    private void setLastPos(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, boolean shouldDismount, CallbackInfo ci) {
        justTeleported = true;
        if (requestedTeleportPos != null) {
            lastPosX = requestedTeleportPos.x;
            lastPosY = requestedTeleportPos.y;
            lastPosZ = requestedTeleportPos.z;
            lastYaw = yaw;
            lastPitch = pitch;
        }
    }

    @Inject(method = "onUpdateSelectedSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getHotbarSize()I", shift = At.Shift.AFTER))
    private void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet, CallbackInfo ci) {
        new PlayerItemHeldEvent(player, player.getInventory().selectedSlot, packet.getSelectedSlot()).callEvent();
    }

    @Inject(method = "onPlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private void injectPlayerSwapItemsEvent(PlayerActionC2SPacket packet, CallbackInfo ci) {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, mainHandStack, offHandStack);
        if (!event.callEvent()) {
            ci.cancel();
        }
    }

    @Redirect(method = "onClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSprinting(Z)V"))
    private void sprinting(ServerPlayerEntity player, boolean isSprinting) {
        new PlayerToggleSprintEvent(player, isSprinting).callEvent();
        player.setSprinting(isSprinting);
    }
}
