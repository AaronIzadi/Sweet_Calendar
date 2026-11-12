package com.sweetcalendar.data

/**
 * [TASK] — scheduled for a single calendar day ([TaskEntity.jalaliDate]).
 *
 * [TODO] — long-running item with [TaskEntity.jalaliDate] as the deadline; appears on Today
 * until marked complete (same persisted row, not a recurring clone).
 */
enum class TaskScheduleKind {
    TASK,
    TODO,
    ;

    companion object {
        fun fromStored(raw: String?): TaskScheduleKind =
            entries.firstOrNull { it.name == raw } ?: TASK
    }
}
