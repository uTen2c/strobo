@file:JvmName("TaskUtils")

package dev.uten2c.strobo.task

import java.util.function.Consumer
import java.util.function.Predicate

data class Task(internal var delay: Long, val runnable: Runnable, var cancelled: Boolean = false)

data class TimerTask(
    val delay: Long,
    val period: Long,
    val predicate: Predicate<Long>,
    val consumer: Consumer<Long>,
    var cancelled: Boolean = false
) {
    private var delayCount = 0L
    private var periodCount = 0L
    private var count = 0L

    fun tick(): Boolean {
        if (delay <= ++delayCount) {
            if (period <= ++periodCount) {
                periodCount = 0
                consumer.accept(++count)
                return cancelled || predicate.test(count)
            }
        }
        return false
    }
}

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

fun runTimer(delay: Long, period: Long, consumer: Consumer<Long>): TimerTask =
    runTimer(delay, period, Long.MAX_VALUE, consumer)

fun runTimer(delay: Long, period: Long, times: Long, consumer: Consumer<Long>): TimerTask =
    runTimer(delay, period, { i -> times <= i }, consumer)

fun runTimer(delay: Long, period: Long, predicate: Predicate<Long>, consumer: Consumer<Long>): TimerTask {
    val task = TimerTask(delay, period, predicate, consumer)
    TaskRunner.timerTasks.add(task)
    return task
}

@Deprecated("renamed", ReplaceWith("waitAndRun(ticks, block)"))
fun afterTicks(ticks: Long, block: () -> Unit) {
    waitAndRun(ticks, block)
}
