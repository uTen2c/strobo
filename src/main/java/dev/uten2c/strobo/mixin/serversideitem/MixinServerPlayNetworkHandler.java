package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.serversideitem.ServerSideItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

    // クリエイティブインベントリーでServerSideItemが表示用アイテムに置き換わってしまうのを防いでる
    @Redirect(method = "onCreativeInventoryAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/CreativeInventoryActionC2SPacket;getItemStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack swapStack(CreativeInventoryActionC2SPacket packet) {
        ItemStack stack = packet.getItemStack();
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains(ServerSideItem.TAG_KEY)) {
            Identifier id = new Identifier(tag.getString(ServerSideItem.TAG_KEY));
            Item item = Registry.ITEM.get(id);
            if (item instanceof ServerSideItem) {
                return convertStack(stack);
            }
        }
        return stack;
    }

    @SuppressWarnings("deprecation")
    private ItemStack convertStack(ItemStack stack) {
        ItemStack copy = stack.copy();
        Identifier id = new Identifier(copy.getOrCreateNbt().getString(ServerSideItem.TAG_KEY));
        Item item = Registry.ITEM.get(id);
        copy.item = item;
        NbtCompound tag = copy.getNbt();
        if (tag != null) {
            tag.remove(ServerSideItem.TAG_KEY);
            ItemStack defaultVisualStack = ((ServerSideItem) item).createVisualStack(item.getDefaultStack());
            NbtCompound displayTag1 = copy.getSubNbt("display");
            NbtCompound displayTag2 = defaultVisualStack.getSubNbt("display");
            if (displayTag1 != null && displayTag2 != null) {
                NbtElement nameTag1 = displayTag1.get("Name");
                if (nameTag1 != null && nameTag1.equals(displayTag2.get("Name"))) {
                    displayTag1.remove("Name");
                }
                if (displayTag1.isEmpty()) {
                    tag.remove("display");
                }
            }
            if (tag.isEmpty()) {
                copy.setNbt(null);
            }
        }
        return copy;
    }
}
