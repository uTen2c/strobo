package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.serversideitem.ServerSideItemConverter;
import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.class_7648;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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
        var stack = packet.getItemStack();
        if (ServerSideItemConverter.shouldConvert(stack)) {
            return ServerSideItemConverter.convert(stack);
        }
        return stack;
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;Lnet/minecraft/class_7648;)V", at = @At("HEAD"))
    private void setUuid(Packet<?> packet, class_7648 arg, CallbackInfo ci) {
        if (packet instanceof UuidHolder uuidHolder) {
            uuidHolder.setUuid(((UuidHolder) connection).getUuid());
        }
    }
}
