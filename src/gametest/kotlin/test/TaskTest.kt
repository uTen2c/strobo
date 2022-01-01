package test

import dev.uten2c.strobo.task.runTimer
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.minecraft.test.GameTest
import net.minecraft.test.TestContext

class TaskTest : StroboGameTest() {

    @GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
    fun timer(context: TestContext) {
        var i = 0
        val map = HashMap<Long, Int>()
        runTimer(0, 5, 3) {
            map[it] = i
        }
        context.runAtEveryTick {
            i++
        }
        context.runAtTick(16) {
            context.addInstantFinalTask {
                if (map == mapOf(1L to 5, 2L to 10, 3L to 15)) {
                    context.complete()
                } else {
                    context.throwGameTestException("Not match: $map")
                }
            }
        }
    }
}
