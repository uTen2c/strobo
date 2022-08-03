package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.serversideitem.RenderType;
import dev.uten2c.strobo.serversideitem.ServerSideItem;
import dev.uten2c.strobo.serversideitem.ServerSideItemConverter;
import dev.uten2c.strobo.util.ExtendedPacketByteBuf;
import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PacketByteBuf.class)
public abstract class MixinPacketByteBuf implements UuidHolder, ExtendedPacketByteBuf {
    @Shadow
    public abstract PacketByteBuf writeItemStack(ItemStack stack);

    @Inject(method = "writeItemStack", at = @At("HEAD"), cancellable = true)
    private void redirect(ItemStack stack, CallbackInfoReturnable<PacketByteBuf> cir) {
        if (stack.getItem() instanceof ServerSideItem) {
            cir.setReturnValue(writeItemStack(stack, RenderType.Inventory.INSTANCE));
        }
    }

    // パケットにItemStackが書き込まれるときに表示用アイテムに置き換えてる
    @Override
    public @NotNull PacketByteBuf writeItemStack(@NotNull ItemStack original, @NotNull RenderType renderType) {
        var stack = ServerSideItemConverter.createPacketStack(getPlayerOrNull(), original, renderType);
        return writeItemStack(stack);
    }
}
