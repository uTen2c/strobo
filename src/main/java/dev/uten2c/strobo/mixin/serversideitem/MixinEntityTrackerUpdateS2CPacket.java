package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.serversideitem.ServerSideItem;
import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EntityTrackerUpdateS2CPacket.class)
public class MixinEntityTrackerUpdateS2CPacket {

    // エンティティがアイテムを持ってるときとか額縁にアイテムが入ってたりするときに送信されるパケットを偽装してる
    @SuppressWarnings("unchecked")
    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;entriesToPacket(Ljava/util/List;Lnet/minecraft/network/PacketByteBuf;)V"))
    private <T> void write(List<DataTracker.Entry<T>> list, PacketByteBuf packetByteBuf) {
        for (DataTracker.Entry<T> entry : list) {
            T value = entry.get();
            if (value instanceof ItemStack stack) {
                Item item = stack.getItem();
                if (item instanceof ServerSideItem serverSideItem) {
                    var player = ((UuidHolder) packetByteBuf).getPlayerOrNull();
                    if (player != null) {
                        stack = serverSideItem.createVisualStack(stack, player);
                        stack.removeCustomName();
                    }
                }

                entry.set((T) stack);
            }
        }
        DataTracker.entriesToPacket((List<DataTracker.Entry<?>>) (Object) list, packetByteBuf);
    }
}
