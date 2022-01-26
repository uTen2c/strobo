package dev.uten2c.strobo

import dev.uten2c.strobo.event.listenEvent
import dev.uten2c.strobo.event.server.ServerStartingEvent

internal object GlobalListeners {

    fun setup() {
        listenEvent<ServerStartingEvent> {
            Strobo.server = it.server
        }
    }
}
