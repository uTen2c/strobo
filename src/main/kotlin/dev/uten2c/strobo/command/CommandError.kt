package dev.uten2c.strobo.command

import com.mojang.brigadier.Message
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType

/**
 * [String]だけのシンプルなエラーを生成する
 * @param message エラーメッセージ
 */
class CommandError(override val message: String) : CommandSyntaxException(SimpleCommandExceptionType { message }, Message { message })
