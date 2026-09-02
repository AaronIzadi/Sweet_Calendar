package com.example.calendartodo.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.GregorianDate
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.reminder.TaskReminderScheduler
import com.example.calendartodo.repository.EventRepository
import com.example.calendartodo.repository.TaskRepository
import com.example.calendartodo.ui.addtask.TaskFormData
import com.example.calendartodo.ui.components.TaskCategory
import com.example.calendartodo.widget.SweetWidgets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
    val isLoadingEvents: Boolean = false,
    val eventsLoadFailed: Boolean = false,
    val showHolidays: Boolean = true,
    val weekStartsOn: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    application: Application,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository
) : AndroidViewModel(application) {

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
        val eventDatesInfo = if (meta.showHolidays) {
            monthEvents
                .filter { it.description.isNotEmpty() }
                .groupBy { normalizeEventDateKey(it.jalaliDate) }
        } else {
            emptyMap()
        }
        val tasksByDate = allTasks.groupBy { JalaliDate.parseIso(it.jalaliDate).formatIso() }

        val cells = when (meta.calendarSystem) {
            CalendarSystem.PERSIAN -> buildPersianMonthCells(pMonth, meta.weekStartsOn, eventDatesInfo, tasksByDate)
            CalendarSystem.GREGORIAN -> buildGregorianMonthCells(gMonth, meta.weekStartsOn, eventDatesInfo, tasksByDate)
        }

        val selectedIso = selected.formatIso()
        val selectedEvents = eventDatesInfo[normalizeEventDateKey(selectedIso)].orEmpty().map {
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
        combine(base, selectedDate.flatMapLatest { taskRepository.observeForDate(it.formatIso()) }) { state, tasks ->
            state.copy(selectedDayTasks = tasks)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun rescheduleAllActive() {
        viewModelScope.launch {
            taskRepository.getActiveReminders().forEach { reminderScheduler.schedule(it) }
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
                repeatWeekly = form.repeatWeekly
            )
            val task = TaskEntity(
                id = id,
                title = form.title,
                notes = form.notes,
                jalaliDate = jalaliDate,
                reminderTime = form.reminderTime,
                category = form.category,
                priority = form.priority,
                repeatWeekly = form.repeatWeekly
            )
            reminderScheduler.schedule(task)
            refreshWidgets()
        }
    }

    fun updateTask(task: TaskEntity, form: TaskFormData) {
        if (form.title.isBlank()) return
        viewModelScope.launch {
            val updated = task.copy(
                title = form.title,
                notes = form.notes,
                jalaliDate = form.jalaliDate.formatIso(),
                reminderTime = form.reminderTime,
                category = form.category,
                priority = form.priority,
                repeatWeekly = form.repeatWeekly
            )
            taskRepository.updateTask(updated)
            reminderScheduler.cancel(task.id)
            if (!updated.isDone) {
                reminderScheduler.schedule(updated)
            }
            refreshWidgets()
        }
    }

    fun toggleTaskDone(task: TaskEntity) {
        viewModelScope.launch {
            val done = !task.isDone
            taskRepository.setDone(task.id, done)
            if (done) {
                reminderScheduler.cancel(task.id)
                if (task.repeatWeekly) {
                    val nextDate = JalaliDate.parseIso(task.jalaliDate).plusDays(7)
                    val newId = taskRepository.addTask(
                        title = task.title,
                        notes = task.notes,
                        jalaliDate = nextDate.formatIso(),
                        reminderTime = task.reminderTime,
                        category = task.category,
                        priority = task.priority,
                        repeatWeekly = true
                    )
                    reminderScheduler.schedule(
                        TaskEntity(
                            id = newId,
                            title = task.title,
                            notes = task.notes,
                            jalaliDate = nextDate.formatIso(),
                            reminderTime = task.reminderTime,
                            category = task.category,
                            priority = task.priority,
                            repeatWeekly = true
                        )
                    )
                }
            } else {
                reminderScheduler.schedule(task.copy(isDone = false))
            }
            refreshWidgets()
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            reminderScheduler.cancel(task.id)
            taskRepository.deleteTask(task)
            refreshWidgets()
        }
    }

    fun restoreTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.restoreTask(task)
            if (!task.isDone && task.reminderTime != null) {
                reminderScheduler.schedule(task)
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
                    }
                    CalendarSystem.GREGORIAN -> {
                        val month = visibleGregorianMonth.value
                        eventRepository.ensureGregorianMonthCached(month.year, month.month, forceRefresh)
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
        eventDatesInfo: Map<String, List<com.example.calendartodo.data.local.EventCacheEntity>>,
        tasksByDate: Map<String, List<TaskEntity>>
    ): List<DayCellInfo?> {
        val daysInMonth = month.daysInMonth()
        val leadingBlanks = (month.firstOfMonth().weekdayIndex() - weekStartsOn + 7) % 7
        val cells = mutableListOf<DayCellInfo?>()
        repeat(leadingBlanks) { cells.add(null) }
        for (d in 1..daysInMonth) {
            val date = JalaliDate(month.year, month.month, d)
            cells.add(buildDayCell(date, d, eventDatesInfo, tasksByDate))
        }
        return cells
    }

    private fun buildGregorianMonthCells(
        month: GregorianDate,
        weekStartsOn: Int,
        eventDatesInfo: Map<String, List<com.example.calendartodo.data.local.EventCacheEntity>>,
        tasksByDate: Map<String, List<TaskEntity>>
    ): List<DayCellInfo?> {
        val daysInMonth = month.daysInMonth()
        val leadingBlanks = (month.firstOfMonth().weekdayIndex() - weekStartsOn + 7) % 7
        val cells = mutableListOf<DayCellInfo?>()
        repeat(leadingBlanks) { cells.add(null) }
        for (d in 1..daysInMonth) {
            val gregorian = GregorianDate(month.year, month.month, d)
            val jalali = gregorian.toJalali()
            cells.add(buildDayCell(jalali, d, eventDatesInfo, tasksByDate))
        }
        return cells
    }

    private fun buildDayCell(
        date: JalaliDate,
        displayDay: Int,
        eventDatesInfo: Map<String, List<com.example.calendartodo.data.local.EventCacheEntity>>,
        tasksByDate: Map<String, List<TaskEntity>>
    ): DayCellInfo {
        val iso = date.formatIso()
        val dayEvents = eventDatesInfo[normalizeEventDateKey(iso)].orEmpty()
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

    private fun normalizeEventDateKey(raw: String): String {
        val parsed = JalaliDate.parseIso(raw)
        return "%04d-%02d-%02d".format(parsed.year, parsed.month, parsed.day)
    }

    class Factory(
        private val application: Application,
        private val taskRepository: TaskRepository,
        private val eventRepository: EventRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(application, taskRepository, eventRepository) as T
        }
    }
}
