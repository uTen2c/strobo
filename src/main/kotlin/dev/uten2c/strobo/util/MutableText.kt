package dev.uten2c.strobo.util

import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

/**
 * [Style.withColor]のエイリアス
 * @param color 色
 * @return 適用後の[MutableText]
 */
fun MutableText.color(color: TextColor): MutableText = setStyle(style.withColor(color))

/**
 * [Style.withColor]のエイリアス
 * @param formatting 色
 * @return 適用後の[MutableText]
 */
fun MutableText.color(formatting: Formatting): MutableText = setStyle(style.withColor(formatting))

/**
 * [Style.withColor]のエイリアス
 * @param rgb 色
 * @return 適用後の[MutableText]
 */
fun MutableText.color(rgb: Int): MutableText = setStyle(style.withColor(TextColor.fromRgb(rgb)))

/**
 * [Style.withColor]のエイリアス
 * @param red 赤
 * @param green 緑
 * @param blue 青
 * @return 適用後の[MutableText]
 */
fun MutableText.color(red: Int, green: Int, blue: Int): MutableText {
    var rgb = red
    rgb = (rgb shl 8) + green
    rgb = (rgb shl 8) + blue
    return setStyle(style.withColor(TextColor.fromRgb(rgb)))
}

/**
 * [Style.withBold]のエイリアス
 * @param bold 太字か
 * @return 適用後の[MutableText]
 */
fun MutableText.bold(bold: Boolean): MutableText = setStyle(style.withBold(bold))

/**
 * [Style.withItalic]のエイリアス
 * @param italic 斜体か
 * @return 適用後の[MutableText]
 */
fun MutableText.italic(italic: Boolean): MutableText = setStyle(style.withItalic(italic))

/**
 * [Style.withUnderline]のエイリアス
 * @param underline 下線があるか
 * @return 適用後の[MutableText]
 */
fun MutableText.underline(underline: Boolean): MutableText = setStyle(style.withUnderline(underline))

/**
 * [Style.withFont]のエイリアス
 * @param font フォントID
 * @return 適用後の[MutableText]
 */
fun MutableText.font(font: String): MutableText = setStyle(style.withFont(Identifier(font)))

/**
 * [Style.withFont]のエイリアス
 * @param font フォントID
 * @return 適用後の[MutableText]
 */
fun MutableText.font(font: Identifier): MutableText = setStyle(style.withFont(font))

/**
 * [Style.withStrikethrough]のエイリアス
 * @param strikethrough 打ち消し線があるか
 * @return 適用後の[MutableText]
 */
fun MutableText.strikethrough(strikethrough: Boolean): MutableText = setStyle(style.withStrikethrough(strikethrough))

/**
 * [Style.obfuscated]のエイリアス
 * @param obfuscated 難読化されているか
 * @return 適用後の[MutableText]
 */
fun MutableText.obfuscated(obfuscated: Boolean): MutableText = setStyle(style.obfuscated(obfuscated))

/**
 * [Style.withClickEvent]のエイリアス
 * @param clickEvent クリックイベント
 * @return 適用後の[MutableText]
 */
fun MutableText.clickEvent(clickEvent: ClickEvent?): MutableText = setStyle(style.withClickEvent(clickEvent))

/**
 * [Style.withHoverEvent]のエイリアス
 * @param hoverEvent ホバーイベント
 * @return 適用後の[MutableText]
 */
fun MutableText.hoverEvent(hoverEvent: HoverEvent?): MutableText = setStyle(style.withHoverEvent(hoverEvent))

/**
 * [Style.withInsertion]のエイリアス
 * Shift押しながらクリックしたときにチャットに挿入されるやつ
 * @param insertion 挿入イベント
 * @return 適用後の[MutableText]
 */
fun MutableText.insertion(insertion: String?): MutableText = setStyle(style.withInsertion(insertion))
