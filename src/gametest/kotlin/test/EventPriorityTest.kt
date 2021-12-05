package test

import dev.uten2c.strobo.event.EventPriority
import dev.uten2c.strobo.event.listenEvent
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.minecraft.test.GameTest
import net.minecraft.test.TestContext
import test.event.TestEvent

class EventPriorityTest : StroboGameTest() {

    @GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 1)
    fun priorityTest(context: TestContext) {
        var count = 0
        EventPriority.values().forEachIndexed { i, priority ->
            listenEvent<TestEvent>(priority) {
                if (count++ != i) {
                    context.throwGameTestException("イベントの優先度が正しくありません")
                } else if (priority == EventPriority.MONITOR) {
                    context.complete()
                }
            }
        }
        TestEvent().callEvent()
    }
}
