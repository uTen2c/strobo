package dev.uten2c.strobo.task

fun nextTick(block: () -> Unit) {
    TaskRunner.tasks.add(TaskRunner.Task(1, block))
}

fun afterTicks(ticks: Long, block: () -> Unit) {
    TaskRunner.tasks.add(TaskRunner.Task(ticks, block))
}