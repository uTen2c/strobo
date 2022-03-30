package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.mixin.accessor.ItemStackAccessor;
import dev.uten2c.strobo.serversideitem.ServerSideItem;
import dev.uten2c.strobo.util.UuidHolder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    @Final
    public ClientConnection connection;

    // クリエイティブインベントリーでServerSideItemが表示用アイテムに置き換わってしまうのを防いでる
    @Redirect(method = "onCreativeInventoryAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/CreativeInventoryActionC2SPacket;getItemStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack swapStack(CreativeInventoryActionC2SPacket packet) {
        ItemStack stack = packet.getItemStack();
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains(ServerSideItem.TAG_KEY)) {
            Identifier id = new Identifier(tag.getString(ServerSideItem.TAG_KEY));
            Item item = Registry.ITEM.get(id);
            if (item instanceof ServerSideItem) {
                return convertStack(stack, player);
            }
        }
        return stack;
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"))
    private void setUuid(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> listener, CallbackInfo ci) {
        if (packet instanceof UuidHolder uuidHolder) {
            uuidHolder.setUuid(((UuidHolder) connection).getUuid());
        }
    }

    private ItemStack convertStack(ItemStack stack, ServerPlayerEntity player) {
        ItemStack copy = stack.copy();
        Identifier id = new Identifier(copy.getOrCreateNbt().getString(ServerSideItem.TAG_KEY));
        Item item = Registry.ITEM.get(id);
        ((ItemStackAccessor) (Object) copy).setItem(item);
        NbtCompound tag = copy.getNbt();
        if (tag != null) {
            tag.remove(ServerSideItem.TAG_KEY);
            ItemStack defaultVisualStack = ((ServerSideItem) item).createVisualStack(item.getDefaultStack(), player);
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
