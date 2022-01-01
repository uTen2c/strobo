package dev.uten2c.strobo.task

import dev.uten2c.strobo.event.listenEvent
import dev.uten2c.strobo.event.server.ServerEndTickEvent

object TaskRunner {

    internal val tasks = ArrayList<Task>()
    internal val timerTasks = ArrayList<TimerTask>()

    internal fun setup() {
        listenEvent<ServerEndTickEvent> {
            tasks.removeAll {
                if (it.cancelled) {
                    return@removeAll true
                }

                if (it.delay <= 0) {
                    it.runnable.run()
                    true
                } else {
                    it.delay--
                    false
                }
            }
            timerTasks.removeAll(TimerTask::tick)
        }
    }
}
