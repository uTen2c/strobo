package dev.uten2c.strobo.command

internal data class Command(val name: String, val builder: CommandBuilder.() -> Unit)
