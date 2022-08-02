package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.block.BlockBreakEvent;
import dev.uten2c.strobo.event.player.PlayerGameModeChangedEvent;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class MixinServerPlayerInteractionManager {

    @Shadow
    protected ServerWorld world;

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    // BlockBreakEventを呼び出してる
    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void blockBreakEvent(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        var blockState = this.world.getBlockState(pos);
        var isSwordNoBreak = !player.getMainHandStack().getItem().canMine(blockState, world, pos, player);

        if (world.getBlockEntity(pos) == null && !isSwordNoBreak) {
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, Blocks.AIR.getDefaultState()));
        }

        var event = new BlockBreakEvent(blockState, pos, player);
        event.setCancelled(isSwordNoBreak);
        event.callEvent();

        if (event.isCancelled()) {
            if (isSwordNoBreak) {
                cir.setReturnValue(false);
            }

            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));

            for (var dir : Direction.values()) {
                player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos.offset(dir)));
            }

            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                player.networkHandler.sendPacket(blockEntity.toUpdatePacket());
            }
            cir.setReturnValue(false);
        }
    }

    // PlayerGameModeChangedEventを呼び出してる
    @Inject(method = "setGameMode", at = @At("TAIL"))
    private void callPlayerGameModeChangeEvent(GameMode gameMode, GameMode previousGameMode, CallbackInfo ci) {
        new PlayerGameModeChangedEvent(player, previousGameMode, gameMode).callEvent();
    }
}
