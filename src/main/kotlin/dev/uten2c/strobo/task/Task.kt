package dev.uten2c.strobo.task

fun nextTick(block: () -> Unit) {
    TaskRunner.tasks.add(TaskRunner.Task(1, block))
}

fun runSync(block: () -> Unit) {
    TaskRunner.tasks.add(TaskRunner.Task(0, block))
}

fun waitAndRun(ticks: Long, block: () -> Unit) {
    TaskRunner.tasks.add(TaskRunner.Task(ticks, block))
}

@Deprecated("renamed", ReplaceWith("waitAndRun(ticks, block)"))
fun afterTicks(ticks: Long, block: () -> Unit) {
    waitAndRun(ticks, block)
}
