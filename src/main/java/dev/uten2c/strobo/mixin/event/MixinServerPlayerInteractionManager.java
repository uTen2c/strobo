package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.block.BlockBreakEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class MixinServerPlayerInteractionManager {

    @Shadow
    protected ServerWorld world;

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Shadow
    public abstract boolean isCreative();

    // BlockBreakEventを呼び出してる
    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void blockBreakEvent(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.world.getBlockState(pos);

        BlockBreakEvent event;
        boolean isSwordNoBreak = !player.getMainHandStack().getItem().canMine(blockState, world, pos, player);

        if (world.getBlockEntity(pos) == null && !isSwordNoBreak) {
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, Blocks.AIR.getDefaultState()));
        }

        event = new BlockBreakEvent(blockState, pos, player);
        event.setCancelled(isSwordNoBreak);
        event.callEvent();

        if (event.isCancelled()) {
            if (isSwordNoBreak) {
                cir.setReturnValue(false);
            }

            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));

            for (Direction dir : Direction.values()) {
                player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos.offset(dir)));
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                player.networkHandler.sendPacket(blockEntity.toUpdatePacket());
            }
            cir.setReturnValue(false);
        }
    }
}
