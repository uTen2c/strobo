package dev.uten2c.strobo.task

import dev.uten2c.strobo.event.listenEvent
import dev.uten2c.strobo.event.server.ServerEndTickEvent

object TaskRunner {

    internal val tasks = ArrayList<Task>()

    fun setup() {
        listenEvent<ServerEndTickEvent> {
            tasks.removeAll {
                if (it.delay <= 0) {
                    it.task.run()
                    true
                } else {
                    it.delay--
                    false
                }
            }
        }
    }

    internal data class Task(var delay: Long, val task: Runnable)
}
