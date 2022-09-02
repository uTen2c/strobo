package dev.uten2c.strobo.util;

import java.util.OptionalInt;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IServerPlayerEntity {
    /**
     * クライアントに{@link CloseScreenS2CPacket}パケットを送らずにScreenHandlerを開く。<br/>
     * これによりマウスカーソルの位置を保持したまま画面を遷移させることができる。
     */
    @NotNull OptionalInt openHandledScreenWithoutClosePacket(@Nullable NamedScreenHandlerFactory factory);
}
