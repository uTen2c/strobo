@file:JvmName("TaskUtils")

package dev.uten2c.strobo.task

data class Task(internal var delay: Long, val runnable: Runnable, var cancelled: Boolean = false)

/**
 * 次のティックの終わりに実行
 * @param runnable 実行する内容
 */
fun nextTick(runnable: Runnable) {
    TaskRunner.tasks.add(Task(1, runnable))
}

/**
 * 現在のティックの終わりに実行
 * @param runnable 実行する内容
 */
fun runSync(runnable: Runnable) {
    TaskRunner.tasks.add(Task(0, runnable))
}

/**
 * 指定ティック後に実行
 * @param ticks 待つティック数
 * @param runnable 実行する内容
 */
fun waitAndRun(ticks: Long, runnable: Runnable): Task {
    val task = Task(ticks, runnable)
    TaskRunner.tasks.add(task)
    return task
}

@Deprecated("renamed", ReplaceWith("waitAndRun(ticks, block)"))
fun afterTicks(ticks: Long, block: () -> Unit) {
    waitAndRun(ticks, block)
}
