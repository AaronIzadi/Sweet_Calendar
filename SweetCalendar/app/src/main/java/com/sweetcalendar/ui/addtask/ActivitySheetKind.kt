package com.sweetcalendar.ui.addtask

import com.sweetcalendar.data.TaskScheduleKind

/** Top-level create/edit sheet mode: scheduled task, to-do, or weekly habit. */
enum class ActivitySheetKind {
    TASK,
    TODO,
    HABIT,
    ;

    fun toScheduleKind(): TaskScheduleKind = when (this) {
        TASK -> TaskScheduleKind.TASK
        TODO -> TaskScheduleKind.TODO
        HABIT -> TaskScheduleKind.TASK
    }

    companion object {
        fun fromScheduleKind(kind: TaskScheduleKind): ActivitySheetKind = when (kind) {
            TaskScheduleKind.TASK -> TASK
            TaskScheduleKind.TODO -> TODO
        }
    }
}
