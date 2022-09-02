package dev.uten2c.strobo.mixin.screen;

import com.mojang.authlib.GameProfile;
import dev.uten2c.strobo.screen.StroboScreenHandlerListener;
import dev.uten2c.strobo.util.IServerPlayerEntity;
import java.util.OptionalInt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements IServerPlayerEntity {
    @Mutable
    @Shadow
    @Final
    private ScreenHandlerListener screenHandlerListener;

    @Shadow public abstract void closeScreenHandler();

    @Shadow protected abstract void incrementScreenHandlerSyncId();

    @Shadow private int screenHandlerSyncId;

    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Shadow protected abstract void onScreenHandlerOpened(ScreenHandler screenHandler);

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createFilterer(Lnet/minecraft/server/network/ServerPlayerEntity;)Lnet/minecraft/server/filter/TextStream;"))
    private void swapScreenHandlerListener(MinecraftServer server, ServerWorld world, GameProfile profile, PlayerPublicKey publicKey, CallbackInfo ci) {
        screenHandlerListener = new StroboScreenHandlerListener(
                (ServerPlayerEntity) (Object) MixinServerPlayerEntity.this,
                screenHandlerListener
        );
    }

    /**
     * アップデートのとき{@link ServerPlayerEntity#openHandledScreen}と合わせて
     */
    @Override
    public @NotNull OptionalInt openHandledScreenWithoutClosePacket(@Nullable NamedScreenHandlerFactory factory) {
        if (factory == null) {
            return OptionalInt.empty();
        }
        if (this.currentScreenHandler != this.playerScreenHandler) {
            this.closeScreenHandler(); // openHandledScreenとここだけが違う
        }
        this.incrementScreenHandlerSyncId();
        var screenHandler = factory.createMenu(this.screenHandlerSyncId, this.getInventory(), this);
        if (screenHandler == null) {
            if (this.isSpectator()) {
                this.sendMessage((Text.translatable("container.spectatorCantOpen")).formatted(Formatting.RED), true);
            }
            return OptionalInt.empty();
        } else {
            this.networkHandler.sendPacket(new OpenScreenS2CPacket(screenHandler.syncId, screenHandler.getType(), factory.getDisplayName()));
            this.onScreenHandlerOpened(screenHandler);
            this.currentScreenHandler = screenHandler;
            return OptionalInt.of(this.screenHandlerSyncId);
        }
    }
}
