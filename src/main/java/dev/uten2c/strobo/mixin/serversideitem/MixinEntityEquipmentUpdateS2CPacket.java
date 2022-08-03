package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.serversideitem.RenderType;
import dev.uten2c.strobo.util.ExtendedPacketByteBuf;
import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityEquipmentUpdateS2CPacket.class)
public abstract class MixinEntityEquipmentUpdateS2CPacket implements UuidHolder {
    @Shadow
    @Final
    private int id;

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;"))
    private PacketByteBuf swapItem(PacketByteBuf instance, ItemStack stack) {
        return ((ExtendedPacketByteBuf) instance).writeItemStack(stack, new RenderType.WithEntity(id));
    }
}
