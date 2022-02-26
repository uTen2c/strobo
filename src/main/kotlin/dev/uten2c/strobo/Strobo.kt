package dev.uten2c.strobo

import dev.uten2c.strobo.command.StroboCommand
import dev.uten2c.strobo.event.Event
import dev.uten2c.strobo.event.EventListener
import dev.uten2c.strobo.option.StroboOptions
import dev.uten2c.strobo.task.TaskRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import net.fabricmc.api.ModInitializer
import net.minecraft.server.MinecraftServer
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

object Strobo : ModInitializer {
    @JvmField
    val options = StroboOptions()

    internal val logger = LoggerFactory.getLogger("Strobo")
    internal val eventListeners = HashMap<KClass<out Event>, HashSet<EventListener>>()
    internal val scope = CoroutineScope(Dispatchers.Default + Job())
    lateinit var server: MinecraftServer
        internal set

    override fun onInitialize() {
        GlobalListeners.setup()
        TaskRunner.setup()
        StroboCommand.register()
    }
}
