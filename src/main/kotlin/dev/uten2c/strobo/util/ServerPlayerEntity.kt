package dev.uten2c.strobo.util

import dev.uten2c.strobo.screen.TitledScreenHandler
import dev.uten2c.strobo.task.waitAndRun
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket
import net.minecraft.network.packet.s2c.play.EntityS2CPacket
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.OptionalInt
import java.util.UUID

private val hiddenPlayerMap = HashMap<UUID, HashSet<UUID>>()
private val states = object : HashMap<ServerPlayerEntity, ListState>() {
    override fun get(key: ServerPlayerEntity): ListState {
        if (key !in this) {
            put(key, ListState(null, null, null))
        }
        return super.get(key)!!
    }
}

private data class ListState(var header: Text?, var footer: Text?, var name: Text?)

/**
 * プレイヤーリストの名前を設定する
 */
var ServerPlayerEntity.listName: Text?
    get() = states[this].name
    set(value) {
        states[this].name = value
    }

/**
 * プレイヤーリストの名前の変更をプレイヤー全体に伝える
 */
fun ServerPlayerEntity.updateListName() {
    val packet = PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, this)
    server.playerManager.sendToAll(packet)
}

/**
 * プレイヤーリストのヘッダー部分に表示されるメッセージを設定する
 */
var ServerPlayerEntity.playerListHeader: Text?
    get() = states[this].header
    set(value) {
        states[this].header = value
        updatePlayerListHeaderAndFooter()
    }

/**
 * プレイヤーリストのフッター部分に表示されるメッセージを設定する
 */
var ServerPlayerEntity.playerListFooter: Text?
    get() = states[this].footer
    set(value) {
        states[this].footer = value
        updatePlayerListHeaderAndFooter()
    }

/**
 * プレイヤーリストのヘッダー及びフッターのメッセージの変更をプレイヤーに伝える
 */
fun ServerPlayerEntity.updatePlayerListHeaderAndFooter() {
    val packet = PlayerListHeaderS2CPacket(
        playerListHeader ?: Text.empty(),
        playerListFooter ?: Text.empty(),
    )
    networkHandler.sendPacket(packet)
}

/**
 * プレイヤーにタイトルを表示させる
 */
fun ServerPlayerEntity.sendTitle(title: Text?, subtitle: Text?, fadeIn: Int, stay: Int, fadeOut: Int) {
    networkHandler.sendPacket(TitleFadeS2CPacket(fadeIn, stay, fadeOut))
    networkHandler.sendPacket(SubtitleS2CPacket(subtitle ?: emptyText()))
    networkHandler.sendPacket(TitleS2CPacket(title ?: emptyText()))
}

/**
 * プレイヤーに表示されているタイトルを消す
 */
fun ServerPlayerEntity.clearTitle() = networkHandler.sendPacket(ClearTitleS2CPacket(false))

/**
 * プレイヤーをリスポーンさせる
 */
fun ServerPlayerEntity.respawn() {
    val manager = server?.playerManager
    if (health <= 0 && manager?.getPlayer(uuid) != null) {
        networkHandler.player = manager.respawnPlayer(this, false)
    }
}

/**
 * プレイヤーの飛行速度を設定する
 */
var ServerPlayerEntity.flySpeed: Float
    get() = abilities.flySpeed
    set(value) {
        abilities.flySpeed = value / 2f
        sendAbilitiesUpdate()
    }

/**
 * PaperSpigot内で使用されているテレポート処理をエミュレートする
 * @param location 座標
 */
fun ServerPlayerEntity.bukkitTp(location: Location): Boolean {
    if (health == 0f || isRemoved) {
        return false
    }

    if (networkHandler == null) {
        return false
    }

    if (hasPassengers()) {
        return false
    }

    stopRiding()

    if (isSleeping) {
        wakeUp(true, false)
    }

    if (currentScreenHandler != playerScreenHandler) {
        closeHandledScreen()
    }

    networkHandler.requestTeleport(location.x, location.y, location.z, location.yaw, location.pitch)
    return true
}

@Deprecated("完全でない実装")
fun ServerPlayerEntity.showPlayer(player: ServerPlayerEntity) {
    if (this == player) {
        return
    }

    val set = hiddenPlayerMap.getOrPut(uuid) { HashSet() }
    set.remove(player.uuid)

    if (!player.isDisconnected) {
        networkHandler.sendPacket(PlayerSpawnS2CPacket(player))
        val y = (player.headYaw * 256f / 360f).toInt().toByte()
        val p = (player.pitch * 256f / 360f).toInt().toByte()
        networkHandler.sendPacket(EntityS2CPacket.Rotate(player.id, y, p, player.isOnGround))
        repeat(3) {
            waitAndRun(it.toLong()) {
                networkHandler.sendPacket(EntityTrackerUpdateS2CPacket(player.id, player.dataTracker, true))
            }
        }
    }
}

@Deprecated("完全でない実装")
fun ServerPlayerEntity.hidePlayer(player: ServerPlayerEntity) {
    if (this == player) {
        return
    }

    val set = hiddenPlayerMap.getOrPut(uuid) { HashSet() }
    set.add(player.uuid)
    val packet = EntitiesDestroyS2CPacket(player.id)
    networkHandler.sendPacket(packet)
}

@Deprecated("完全でない実装")
fun ServerPlayerEntity.isHidden(player: ServerPlayerEntity): Boolean =
    hiddenPlayerMap[uuid]?.contains(player.uuid) ?: false

/**
 * [ServerPlayerEntity.openHandledScreen]のシンタックスシュガー
 */
fun ServerPlayerEntity.openHandledScreen(
    title: Text,
    factory: (syncId: Int, inv: PlayerInventory) -> ScreenHandler,
): Int? {
    val handlerFactory = SimpleNamedScreenHandlerFactory({ syncId, inv, _ -> factory(syncId, inv) }, title)
    val result = openHandledScreen(handlerFactory)
    return result.intOrNull
}

/**
 * クライアントに[CloseScreenS2CPacket]パケットを送らずにScreenHandlerを開く。<br/>
 * これによりマウスカーソルの位置を保持したまま画面を遷移させることができる。
 */
fun ServerPlayerEntity.openHandledScreenWithoutClosePacket(factory: NamedScreenHandlerFactory): Int? {
    val result = (this as IServerPlayerEntity).openHandledScreenWithoutClosePacket(factory)
    return result.intOrNull
}

/**
 * [openHandledScreenWithoutClosePacket]のシンタックスシュガー
 */
fun ServerPlayerEntity.openHandledScreenWithoutClosePacket(
    title: Text,
    factory: (syncId: Int, inv: PlayerInventory) -> ScreenHandler,
): Int? {
    val handlerFactory = SimpleNamedScreenHandlerFactory({ syncId, inv, _ -> factory(syncId, inv) }, title)
    return openHandledScreenWithoutClosePacket(handlerFactory)
}

/**
 * [TitledScreenHandler]を開く
 */
fun ServerPlayerEntity.openTitledScreen(
    factory: (syncId: Int, playerInventory: PlayerInventory) -> TitledScreenHandler,
): Int? {
    val handlerFactory = object : NamedScreenHandlerFactory {
        private lateinit var title: Text

        override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
            val screenHandler = factory(syncId, inv)
            this.title = screenHandler.title
            return screenHandler
        }

        override fun getDisplayName(): Text {
            return title
        }
    }
    return if (currentScreenHandler != playerScreenHandler) {
        openHandledScreenWithoutClosePacket(handlerFactory)
    } else {
        openHandledScreen(handlerFactory).intOrNull
    }
}

private val OptionalInt.intOrNull: Int?
    get() = if (isPresent) asInt else null
