@file:JvmName("TaskUtils")

package dev.uten2c.strobo.task

fun nextTick(runnable: Runnable) {
    TaskRunner.tasks.add(TaskRunner.Task(1, runnable))
}

fun runSync(runnable: Runnable) {
    TaskRunner.tasks.add(TaskRunner.Task(0, runnable))
}

fun waitAndRun(ticks: Long, runnable: Runnable) {
    TaskRunner.tasks.add(TaskRunner.Task(ticks, runnable))
}

@Deprecated("renamed", ReplaceWith("waitAndRun(ticks, block)"))
fun afterTicks(ticks: Long, block: () -> Unit) {
    waitAndRun(ticks, block)
}
