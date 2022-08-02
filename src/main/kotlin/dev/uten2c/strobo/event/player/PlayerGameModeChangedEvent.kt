package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode

/**
 * ゲームモードの変更後に呼び出される
 * @param player プレイヤー
 * @param previousGameMode 変更前のゲームモード
 * @param newGameMode 変更後のゲームモード
 */
class PlayerGameModeChangedEvent(
    val player: ServerPlayerEntity,
    val previousGameMode: GameMode?,
    val newGameMode: GameMode,
) : Event()
