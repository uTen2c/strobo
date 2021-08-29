package dev.uten2c.strobo.util

import net.minecraft.text.LiteralText

fun text(string: String) = LiteralText(string)

fun emptyText(): LiteralText = LiteralText("")