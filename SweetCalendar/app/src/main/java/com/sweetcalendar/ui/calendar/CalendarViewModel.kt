package com.sweetcalendar.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.export.TaskExportRepository
import com.sweetcalendar.export.TaskExportRunner
import com.sweetcalendar.reminder.TaskReminderScheduler
import com.sweetcalendar.data.local.EventCacheEntity
import com.sweetcalendar.repository.EventRepository
import com.sweetcalendar.repository.TaskRepository
import com.sweetcalendar.repository.gregorianEventStorageKey
import com.sweetcalendar.repository.jalaliEventStorageKey
import com.sweetcalendar.repository.normalizeCachedJalaliDateKey
import com.sweetcalendar.recurrence.RecurrenceEditScope
import com.sweetcalendar.recurrence.RecurrenceFreq
import com.sweetcalendar.recurrence.recurrenceFrequency
import com.sweetcalendar.recurrence.parsedDate
import com.sweetcalendar.recurrence.tasksByDateInRange
import com.sweetcalendar.recurrence.tasksOnDate
import com.sweetcalendar.recurrence.isVirtualOccurrence
import com.sweetcalendar.recurrence.withRecurrence
import com.sweetcalendar.ui.addtask.TaskFormData
import com.sweetcalendar.ui.components.TaskCategory
import com.sweetcalendar.widget.SweetWidgets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayEvent(
    val description: String,
    val additionalDescription: String = "",
    val isHoliday: Boolean
)

data class DayCellInfo(
    val date: JalaliDate,
    val displayDay: Int,
    val hasTask: Boolean,
    val hasOccasion: Boolean,
    val isHoliday: Boolean,
    val taskCategories: List<TaskCategory> = emptyList()
)

data class CalendarUiState(
    val calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    val visibleMonth: JalaliDate = JalaliDate.today().firstOfMonth(),
    val visibleGregorianMonth: GregorianDate = GregorianDate.today().firstOfMonth(),
    val selectedDate: JalaliDate = JalaliDate.today(),
    val monthCells: List<DayCellInfo?> = emptyList(),
    val selectedDayTasks: List<TaskEntity> = emptyList(),
    val selectedDayEvents: List<DayEvent> = emptyList(),
    val todayEvents: List<DayEvent> = emptyList(),
    val isLoadingEvents: Boolean = false,
    val eventsLoadFailed: Boolean = false,
    val showHolidays: Boolean = true,
    val weekStartsOn: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    application: Application,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository,
    taskExportRepository: TaskExportRepository
) : AndroidViewModel(application) {

    val taskExportRunner: TaskExportRunner = TaskExportRunner(application, taskExportRepository)

    private val reminderScheduler = TaskReminderScheduler(application)
    private val visiblePersianMonth = MutableStateFlow(JalaliDate.today().firstOfMonth())
    private val visibleGregorianMonth = MutableStateFlow(GregorianDate.today().firstOfMonth())
    private val selectedDate = MutableStateFlow(JalaliDate.today())
    private val isLoadingEvents = MutableStateFlow(false)
    private val eventsLoadFailed = MutableStateFlow(false)
    private val showHolidays = MutableStateFlow(true)
    private val weekStartsOn = MutableStateFlow(0)
    private val calendarSystem = MutableStateFlow(CalendarSystem.PERSIAN)

    private val calendarMeta = combine(
        isLoadingEvents,
        eventsLoadFailed,
        showHolidays,
        weekStartsOn,
        calendarSystem
    ) { loading, failed, show, week, system ->
        CalendarMeta(loading, failed, show, week, system)
    }

    private data class CalendarMeta(
        val loading: Boolean,
        val failed: Boolean,
        val showHolidays: Boolean,
        val weekStartsOn: Int,
        val calendarSystem: CalendarSystem
    )

    private data class MonthContext(
        val persianMonth: JalaliDate,
        val gregorianMonth: GregorianDate,
        val selected: JalaliDate,
        val meta: CalendarMeta
    )

    private val monthContextFlow = combine(
        visiblePersianMonth,
        visibleGregorianMonth,
        selectedDate,
        calendarMeta
    ) { pMonth, gMonth, selected, meta ->
        MonthContext(pMonth, gMonth, selected, meta)
    }

    private val monthEventsFlow = combine(visiblePersianMonth, visibleGregorianMonth, calendarSystem) { pMonth, gMonth, system ->
        Triple(system, pMonth, gMonth)
    }.flatMapLatest { (system, pMonth, gMonth) ->
        when (system) {
            CalendarSystem.PERSIAN -> eventRepository.observeForMonth(pMonth.year, pMonth.month)
            CalendarSystem.GREGORIAN -> eventRepository.observeForGregorianMonth(gMonth.year, gMonth.month)
        }
    }

    val allTasks: StateFlow<List<TaskEntity>> = taskRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedTasks: StateFlow<List<TaskEntity>> = taskRepository.observeDeleted()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            taskRepository.purgeExpiredDeleted()
        }
    }

    val uiState: StateFlow<CalendarUiState> = combine(
        monthContextFlow,
        taskRepository.observeAll(),
        monthEventsFlow
    ) { context, allTasks, monthEvents ->
        val pMonth = context.persianMonth
        val gMonth = context.gregorianMonth
        val selected = context.selected
        val meta = context.meta
        val eventsByDayKey = if (meta.showHolidays) {
            groupMonthEventsByDayKey(monthEvents, meta.calendarSystem)
        } else {
            emptyMap()
        }
        val tasksByDate = when (meta.calendarSystem) {
            CalendarSystem.PERSIAN -> {
                val lastDay = pMonth.daysInMonth()
                val rangeStart = JalaliDate(pMonth.year, pMonth.month, 1)
                val rangeEnd = JalaliDate(pMonth.year, pMonth.month, lastDay)
                allTasks.tasksByDateInRange(rangeStart, rangeEnd)
            }
            CalendarSystem.GREGORIAN -> {
                val lastDay = gMonth.daysInMonth()
                val rangeStart = GregorianDate(gMonth.year, gMonth.month, 1).toJalali()
                val rangeEnd = GregorianDate(gMonth.year, gMonth.month, lastDay).toJalali()
                val start = minOf(rangeStart, rangeEnd)
                val end = maxOf(rangeStart, rangeEnd)
                allTasks.tasksByDateInRange(start, end)
            }
        }

        val cells = when (meta.calendarSystem) {
            CalendarSystem.PERSIAN -> buildPersianMonthCells(pMonth, meta.weekStartsOn, eventsByDayKey, tasksByDate)
            CalendarSystem.GREGORIAN -> buildGregorianMonthCells(gMonth, meta.weekStartsOn, eventsByDayKey, tasksByDate)
        }

        val selectedEvents = eventsForSelectedDay(eventsByDayKey, selected, meta.calendarSystem).map {
            DayEvent(
                description = it.description,
                additionalDescription = it.additionalDescription,
                isHoliday = it.isHoliday
            )
        }

        CalendarUiState(
            calendarSystem = meta.calendarSystem,
            visibleMonth = pMonth,
            visibleGregorianMonth = gMonth,
            selectedDate = selected,
            monthCells = cells,
            selectedDayEvents = selectedEvents,
            isLoadingEvents = meta.loading,
            eventsLoadFailed = meta.failed,
            showHolidays = meta.showHolidays,
            weekStartsOn = meta.weekStartsOn
        )
    }.let { base ->
        combine(base, selectedDate, taskRepository.observeAll()) { state, selected, allTasks ->
            state.copy(selectedDayTasks = allTasks.tasksOnDate(selected))
        }
    }.let { withTasks ->
        combine(withTasks, showHolidays, calendarSystem) { state, show, system ->
            Triple(state, show, system)
        }.flatMapLatest { (state, show, system) ->
            if (!show) {
                return@flatMapLatest flowOf(state.copy(todayEvents = emptyList()))
            }
            val todayFlow = when (system) {
                CalendarSystem.PERSIAN -> {
                    val key = jalaliEventStorageKey(JalaliDate.today())
                    eventRepository.observeForDate(key, EventCacheEntity.SOURCE_TIME_IR)
                }
                CalendarSystem.GREGORIAN -> {
                    val key = gregorianEventStorageKey(GregorianDate.fromJalali(JalaliDate.today()))
                    eventRepository.observeForGregorianDate(key)
                }
            }
            todayFlow.map { rawToday ->
                state.copy(
                    todayEvents = rawToday
                        .filter { it.description.isNotBlank() }
                        .map {
                            DayEvent(
                                description = it.description,
                                additionalDescription = it.additionalDescription,
                                isHoliday = it.isHoliday
                            )
                        }
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun rescheduleAllActive() {
        viewModelScope.launch {
            taskRepository.getActiveReminders().forEach { scheduleReminderSafely(it) }
        }
    }

    private fun scheduleReminderSafely(task: TaskEntity) {
        try {
            reminderScheduler.schedule(task)
        } catch (_: Exception) {
            // Keep the app usable even when the OS rejects alarm scheduling.
        }
    }

    init {
        refreshEventsForVisibleMonth()
        refreshWidgets()
        rescheduleAllActive()
    }

    fun goToPreviousMonth() {
        when (calendarSystem.value) {
            CalendarSystem.PERSIAN -> visiblePersianMonth.value = visiblePersianMonth.value.plusMonths(-1)
            CalendarSystem.GREGORIAN -> visibleGregorianMonth.value = visibleGregorianMonth.value.plusMonths(-1)
        }
        refreshEventsForVisibleMonth()
    }

    fun goToNextMonth() {
        when (calendarSystem.value) {
            CalendarSystem.PERSIAN -> visiblePersianMonth.value = visiblePersianMonth.value.plusMonths(1)
            CalendarSystem.GREGORIAN -> visibleGregorianMonth.value = visibleGregorianMonth.value.plusMonths(1)
        }
        refreshEventsForVisibleMonth()
    }

    fun goToToday() {
        val today = JalaliDate.today()
        selectedDate.value = today
        visiblePersianMonth.value = today.firstOfMonth()
        visibleGregorianMonth.value = GregorianDate.fromJalali(today).firstOfMonth()
        refreshEventsForVisibleMonth()
    }

    fun selectDate(date: JalaliDate) {
        selectedDate.value = date
    }

    fun setShowHolidays(enabled: Boolean) {
        showHolidays.value = enabled
        if (enabled) refreshEventsForVisibleMonth()
    }

    fun setWeekStartsOn(dayIndex: Int) {
        weekStartsOn.value = dayIndex.coerceIn(0, 6)
    }

    fun setCalendarSystem(system: CalendarSystem) {
        if (calendarSystem.value == system) return
        calendarSystem.value = system
        val selected = selectedDate.value
        if (system == CalendarSystem.GREGORIAN) {
            visibleGregorianMonth.value = GregorianDate.fromJalali(selected).firstOfMonth()
        } else {
            visiblePersianMonth.value = selected.firstOfMonth()
        }
        refreshEventsForVisibleMonth()
    }

    fun applyPreferences(showHolidaysPref: Boolean, weekStartsOnPref: Int, calendarSystemPref: CalendarSystem) {
        showHolidays.value = showHolidaysPref
        weekStartsOn.value = weekStartsOnPref.coerceIn(0, 6)
        if (calendarSystem.value != calendarSystemPref) {
            setCalendarSystem(calendarSystemPref)
        } else if (showHolidaysPref) {
            refreshEventsForVisibleMonth()
        }
    }

    fun retryEventsLoad() {
        refreshEventsForVisibleMonth(forceRefresh = true)
    }

    private fun refreshWidgets() {
        SweetWidgets.updateAll(getApplication())
    }

    fun addTask(form: TaskFormData) {
        if (form.title.isBlank()) return
        viewModelScope.launch {
            val jalaliDate = form.jalaliDate.formatIso()
            val id = taskRepository.addTask(
                title = form.title,
                notes = form.notes,
                jalaliDate = jalaliDate,
                reminderTime = form.reminderTime,
                category = form.category,
                priority = form.priority,
                recurrenceFreq = form.recurrenceFreq,
                recurrenceCount = form.recurrenceCount,
            )
            val task = TaskEntity(
                id = id,
                title = form.title,
                notes = form.notes,
                jalaliDate = jalaliDate,
                reminderTime = form.reminderTime,
                category = form.category,
                priority = form.priority,
                repeatWeekly = form.recurrenceFreq == RecurrenceFreq.WEEKLY,
                recurrenceFreq = form.recurrenceFreq.name,
                recurrenceCount = form.recurrenceCount,
            )
            scheduleReminderSafely(task)
            refreshWidgets()
        }
    }

    fun needsRecurrenceScopeForEdit(task: TaskEntity): Boolean =
        taskRepository.needsRecurrenceScopeForEdit(task)

    fun needsRecurrenceScopeForDelete(task: TaskEntity): Boolean =
        taskRepository.needsRecurrenceScopeForDelete(task)

    fun updateTask(
        task: TaskEntity,
        form: TaskFormData,
        scope: RecurrenceEditScope = RecurrenceEditScope.ENTIRE_SERIES,
    ) {
        if (form.title.isBlank()) return
        viewModelScope.launch {
            val updated = taskRepository.applyRecurrenceEdit(task, form, scope)
            reminderScheduler.cancel(task.id)
            if (!updated.isDone) {
                scheduleReminderSafely(updated)
            }
            refreshWidgets()
        }
    }

    fun materializeOccurrence(task: TaskEntity, onResult: (TaskEntity) -> Unit) {
        viewModelScope.launch {
            onResult(taskRepository.materializeOccurrence(task))
        }
    }

    fun toggleTaskDone(task: TaskEntity) {
        viewModelScope.launch {
            val target = if (task.isVirtualOccurrence()) {
                taskRepository.materializeOccurrence(task)
            } else {
                task
            }
            val done = !target.isDone
            taskRepository.setDone(target.id, done)
            if (done) {
                reminderScheduler.cancel(target.id)
                val recurring = if (
                    target.recurrenceFrequency() != RecurrenceFreq.NONE && target.seriesId == null
                ) {
                    target.withRecurrence(target.recurrenceFrequency()).also { linked ->
                        taskRepository.updateTask(linked)
                    }
                } else {
                    target
                }
                val toSpawn = recurring.withRecurrence(recurring.recurrenceFrequency())
                taskRepository.spawnNextAfterComplete(toSpawn)?.let { next ->
                    val newId = taskRepository.insertTask(next.withRecurrence(next.recurrenceFrequency()))
                    scheduleReminderSafely(next.copy(id = newId))
                }
            } else {
                scheduleReminderSafely(target.copy(isDone = false))
            }
            refreshWidgets()
        }
    }

    fun deleteTask(
        task: TaskEntity,
        scope: RecurrenceEditScope = RecurrenceEditScope.THIS_INSTANCE,
    ) {
        viewModelScope.launch {
            val seriesId = task.seriesId
            if (taskRepository.needsRecurrenceScopeForDelete(task) && seriesId != null) {
                when (scope) {
                    RecurrenceEditScope.ENTIRE_SERIES ->
                        taskRepository.getActiveBySeriesId(seriesId).forEach {
                            reminderScheduler.cancel(it.id)
                        }
                    RecurrenceEditScope.THIS_AND_FOLLOWING -> {
                        reminderScheduler.cancel(task.id)
                        taskRepository.getActiveBySeriesId(seriesId)
                            .filter { it.parsedDate() >= task.parsedDate() }
                            .forEach { reminderScheduler.cancel(it.id) }
                    }
                    RecurrenceEditScope.THIS_INSTANCE ->
                        reminderScheduler.cancel(task.id)
                }
                taskRepository.applyRecurrenceDelete(task, scope)
            } else {
                reminderScheduler.cancel(task.id)
                taskRepository.deleteTask(task)
            }
            refreshWidgets()
        }
    }

    fun restoreTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.restoreTask(task)
            if (!task.isDone && task.reminderTime != null) {
                scheduleReminderSafely(task)
            }
            refreshWidgets()
        }
    }

    fun permanentlyDeleteTask(task: TaskEntity) {
        viewModelScope.launch {
            reminderScheduler.cancel(task.id)
            taskRepository.permanentlyDeleteTask(task)
            refreshWidgets()
        }
    }

    private fun refreshEventsForVisibleMonth(forceRefresh: Boolean = false) {
        if (!showHolidays.value) return
        viewModelScope.launch {
            isLoadingEvents.value = true
            eventsLoadFailed.value = false
            val result = runCatching {
                when (calendarSystem.value) {
                    CalendarSystem.PERSIAN -> {
                        val month = visiblePersianMonth.value
                        eventRepository.ensureMonthCached(month.year, month.month, forceRefresh)
                        val today = JalaliDate.today()
                        eventRepository.ensureMonthCached(today.year, today.month, forceRefresh)
                    }
                    CalendarSystem.GREGORIAN -> {
                        val month = visibleGregorianMonth.value
                        eventRepository.ensureGregorianMonthCached(month.year, month.month, forceRefresh)
                        val gToday = GregorianDate.fromJalali(JalaliDate.today())
                        eventRepository.ensureGregorianMonthCached(gToday.year, gToday.month, forceRefresh)
                    }
                }
            }
            eventsLoadFailed.value = result.isFailure
            isLoadingEvents.value = false
        }
    }

    private fun buildPersianMonthCells(
        month: JalaliDate,
        weekStartsOn: Int,
        eventsByDayKey: Map<String, List<EventCacheEntity>>,
        tasksByDate: Map<String, List<TaskEntity>>
    ): List<DayCellInfo?> {
        val daysInMonth = month.daysInMonth()
        val leadingBlanks = (month.firstOfMonth().weekdayIndex() - weekStartsOn + 7) % 7
        val cells = mutableListOf<DayCellInfo?>()
        repeat(leadingBlanks) { cells.add(null) }
        for (d in 1..daysInMonth) {
            val date = JalaliDate(month.year, month.month, d)
            val dayEvents = eventsByDayKey[jalaliEventStorageKey(date)].orEmpty()
            cells.add(buildDayCell(date, d, dayEvents, tasksByDate))
        }
        return cells
    }

    private fun buildGregorianMonthCells(
        month: GregorianDate,
        weekStartsOn: Int,
        eventsByDayKey: Map<String, List<EventCacheEntity>>,
        tasksByDate: Map<String, List<TaskEntity>>
    ): List<DayCellInfo?> {
        val daysInMonth = month.daysInMonth()
        val leadingBlanks = (month.firstOfMonth().weekdayIndex() - weekStartsOn + 7) % 7
        val cells = mutableListOf<DayCellInfo?>()
        repeat(leadingBlanks) { cells.add(null) }
        for (d in 1..daysInMonth) {
            val gregorian = GregorianDate(month.year, month.month, d)
            val jalali = gregorian.toJalali()
            val dayEvents = eventsByDayKey[gregorianEventStorageKey(gregorian)].orEmpty()
            cells.add(buildDayCell(jalali, d, dayEvents, tasksByDate))
        }
        return cells
    }

    private fun buildDayCell(
        date: JalaliDate,
        displayDay: Int,
        dayEvents: List<EventCacheEntity>,
        tasksByDate: Map<String, List<TaskEntity>>
    ): DayCellInfo {
        val iso = date.formatIso()
        val dayTasks = tasksByDate[iso].orEmpty()
        val categories = dayTasks
            .map { TaskCategory.fromString(it.category) }
            .distinct()
            .take(3)
        return DayCellInfo(
            date = date,
            displayDay = displayDay,
            hasTask = dayTasks.isNotEmpty(),
            hasOccasion = dayEvents.any { !it.isHoliday },
            isHoliday = dayEvents.any { it.isHoliday },
            taskCategories = categories
        )
    }

    private fun groupMonthEventsByDayKey(
        monthEvents: List<EventCacheEntity>,
        calendarSystem: CalendarSystem,
    ): Map<String, List<EventCacheEntity>> {
        val source = when (calendarSystem) {
            CalendarSystem.PERSIAN -> EventCacheEntity.SOURCE_TIME_IR
            CalendarSystem.GREGORIAN -> EventCacheEntity.SOURCE_NAGER
        }
        return monthEvents
            .asSequence()
            .filter { it.source == source && it.description.isNotBlank() }
            .mapNotNull { event ->
                val key = when (calendarSystem) {
                    CalendarSystem.PERSIAN -> normalizeCachedJalaliDateKey(event.jalaliDate)
                    CalendarSystem.GREGORIAN -> event.gregorianDate.takeIf { it.isNotBlank() }
                }
                key?.let { it to event }
            }
            .groupBy({ it.first }, { it.second })
    }

    private fun eventsForSelectedDay(
        eventsByDayKey: Map<String, List<EventCacheEntity>>,
        selected: JalaliDate,
        calendarSystem: CalendarSystem,
    ): List<EventCacheEntity> = when (calendarSystem) {
        CalendarSystem.PERSIAN ->
            eventsByDayKey[jalaliEventStorageKey(selected)].orEmpty()
        CalendarSystem.GREGORIAN -> {
            val gregorian = GregorianDate.fromJalali(selected)
            eventsByDayKey[gregorianEventStorageKey(gregorian)].orEmpty()
        }
    }

    class Factory(
        private val application: Application,
        private val taskRepository: TaskRepository,
        private val eventRepository: EventRepository,
        private val taskExportRepository: TaskExportRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(
                application,
                taskRepository,
                eventRepository,
                taskExportRepository
            ) as T
        }
    }
}
