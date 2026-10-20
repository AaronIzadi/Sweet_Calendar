package com.sweetcalendar.ui.components

import java.util.concurrent.ConcurrentHashMap

/**
 * Remembers a one-shot check pop when a task is marked done before list sections reorder.
 */
object TaskCompletionPop {
    private val pendingPopIds = ConcurrentHashMap.newKeySet<Long>()

    fun schedulePop(taskId: Long) {
        pendingPopIds.add(taskId)
    }

    fun consumePop(taskId: Long): Boolean = pendingPopIds.remove(taskId)
}
