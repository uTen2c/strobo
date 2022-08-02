package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.serversideitem.RenderType;
import dev.uten2c.strobo.serversideitem.ServerSideItem;
import dev.uten2c.strobo.serversideitem.ServerSideItemConverter;
import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PacketByteBuf.class)
public abstract class MixinPacketByteBuf implements UuidHolder {
    // パケットにItemStackが書き込まれるときに表示用アイテムに置き換えてる
    @ModifyVariable(method = "writeItemStack", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private ItemStack swapStack(ItemStack stack) {
        var item = stack.getItem();
        if (item instanceof ServerSideItem serverSideItem) {
            var player = getPlayerOrNull();
            if (player != null) {
                if (player.interactionManager.getGameMode().isCreative()) {
                    return ServerSideItemConverter.createCreativeVisualStack(serverSideItem, stack, player, RenderType.INVENTORY);
                } else {
                    return serverSideItem.createVisualStack(stack, player, RenderType.INVENTORY);
                }
            }
        }
        return stack;
    }
}
