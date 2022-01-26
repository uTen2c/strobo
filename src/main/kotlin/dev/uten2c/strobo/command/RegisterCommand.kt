package dev.uten2c.strobo.command

/**
 * コマンドを登録する
 * @param name コマンド名
 * @param builder コマンドの動作
 */
fun registerCommand(name: String, builder: CommandBuilder.() -> Unit) {
    CommandManager.addCommand(Command(name, builder))
}
