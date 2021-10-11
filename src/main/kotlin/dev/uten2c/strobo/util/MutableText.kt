package dev.uten2c.strobo.util

import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

fun MutableText.color(color: TextColor): MutableText = setStyle(style.withColor(color))

fun MutableText.color(formatting: Formatting): MutableText = setStyle(style.withColor(formatting))

fun MutableText.color(rgb: Int): MutableText = setStyle(style.withColor(TextColor.fromRgb(rgb)))

fun MutableText.color(red: Int, green: Int, blue: Int): MutableText {
    var rgb = red
    rgb = (rgb shl 8) + green
    rgb = (rgb shl 8) + blue
    return setStyle(style.withColor(TextColor.fromRgb(rgb)))
}

fun MutableText.bold(bold: Boolean): MutableText = setStyle(style.withBold(bold))

fun MutableText.italic(bold: Boolean): MutableText = setStyle(style.withItalic(bold))

fun MutableText.underline(bold: Boolean): MutableText = setStyle(style.withUnderline(bold))

fun MutableText.font(font: String): MutableText = setStyle(style.withFont(Identifier(font)))

fun MutableText.font(font: Identifier): MutableText = setStyle(style.withFont(font))

fun MutableText.strikethrough(strikethrough: Boolean): MutableText = setStyle(style.withStrikethrough(strikethrough))

fun MutableText.obfuscated(obfuscated: Boolean): MutableText = setStyle(style.obfuscated(obfuscated))

fun MutableText.clickEvent(clickEvent: ClickEvent?): MutableText = setStyle(style.withClickEvent(clickEvent))

fun MutableText.hoverEvent(hoverEvent: HoverEvent?): MutableText = setStyle(style.withHoverEvent(hoverEvent))

fun MutableText.insertion(insertion: String?): MutableText = setStyle(style.withInsertion(insertion))
