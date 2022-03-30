package dev.uten2c.strobo.mixin.serversideitem;

import dev.uten2c.strobo.util.UuidHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PacketEncoder.class)
public class MixinPacketEncoder {
    private UUID strobo$uuid;

    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;Lio/netty/buffer/ByteBuf;)V", at = @At("HEAD"))
    private void getUuid(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf, CallbackInfo ci) {
        if (packet instanceof UuidHolder) {
            strobo$uuid = ((UuidHolder) packet).getUuid();
        }
    }

    @Redirect(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;Lio/netty/buffer/ByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeVarInt(I)Lnet/minecraft/network/PacketByteBuf;"))
    private PacketByteBuf setUuid(PacketByteBuf buf, int value) {
        ((UuidHolder) buf).setUuid(strobo$uuid);
        return buf.writeVarInt(value);
    }
}
