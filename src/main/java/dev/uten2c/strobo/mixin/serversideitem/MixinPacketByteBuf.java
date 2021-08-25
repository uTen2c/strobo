package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.serversideitem.ServerSideItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PacketByteBuf.class)
public class MixinPacketByteBuf {

    @ModifyVariable(method = "writeItemStack", at = @At("HEAD"), ordinal = 0)
    private ItemStack swapStack(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ServerSideItem serverSideItem) {
            return serverSideItem.createVisualStack(stack);
        }
        return stack;
    }
}
