package com.example.calendartodo.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.reminder.TaskReminderScheduler
import com.example.calendartodo.repository.EventRepository
import com.example.calendartodo.repository.TaskRepository
import com.example.calendartodo.ui.addtask.TaskFormData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayEvent(val description: String, val isHoliday: Boolean)

data class DayCellInfo(
    val date: JalaliDate,
    val hasTask: Boolean,
    val hasEvent: Boolean,
    val isHoliday: Boolean
)

data class CalendarUiState(
    val visibleMonth: JalaliDate = JalaliDate.today().firstOfMonth(),
    val selectedDate: JalaliDate = JalaliDate.today(),
    val monthCells: List<DayCellInfo?> = emptyList(),
    val selectedDayTasks: List<TaskEntity> = emptyList(),
    val selectedDayEvents: List<DayEvent> = emptyList(),
    val isLoadingEvents: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    application: Application,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository
) : AndroidViewModel(application) {

    private val reminderScheduler = TaskReminderScheduler(application)
    private val visibleMonth = MutableStateFlow(JalaliDate.today().firstOfMonth())
    private val selectedDate = MutableStateFlow(JalaliDate.today())
    private val isLoadingEvents = MutableStateFlow(false)

    val allTasks: StateFlow<List<TaskEntity>> = taskRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<CalendarUiState> = combine(
        visibleMonth,
        selectedDate,
        isLoadingEvents,
        taskRepository.observeDatesWithTasks(),
        visibleMonth.flatMapLatest { month ->
            eventRepository.observeForMonth(month.year, month.month)
        }
    ) { month, selected, loading, datesWithTasks, monthEvents ->
        val eventDatesInfo = monthEvents
            .filter { it.description.isNotEmpty() }
            .groupBy { it.jalaliDate }

        val daysInMonth = month.daysInMonth()
        val leadingBlanks = month.firstOfMonth().weekdayIndex()
        val cells = mutableListOf<DayCellInfo?>()
        repeat(leadingBlanks) { cells.add(null) }
        for (d in 1..daysInMonth) {
            val date = JalaliDate(month.year, month.month, d)
            val iso = date.formatIso()
            val dayEvents = eventDatesInfo[iso].orEmpty()
            cells.add(
                DayCellInfo(
                    date = date,
                    hasTask = iso in datesWithTasks,
                    hasEvent = dayEvents.isNotEmpty(),
                    isHoliday = dayEvents.any { it.isHoliday }
                )
            )
        }

        val selectedIso = selected.formatIso()
        val selectedEvents = eventDatesInfo[selectedIso].orEmpty()
            .map { DayEvent(it.description, it.isHoliday) }

        CalendarUiState(
            visibleMonth = month,
            selectedDate = selected,
            monthCells = cells,
            selectedDayEvents = selectedEvents,
            isLoadingEvents = loading
        )
    }.let { base ->
        combine(base, selectedDate.flatMapLatest { taskRepository.observeForDate(it.formatIso()) }) { state, tasks ->
            state.copy(selectedDayTasks = tasks)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    init {
        refreshEventsForVisibleMonth()
    }

    fun goToPreviousMonth() {
        visibleMonth.value = visibleMonth.value.plusMonths(-1)
        refreshEventsForVisibleMonth()
    }

    fun goToNextMonth() {
        visibleMonth.value = visibleMonth.value.plusMonths(1)
        refreshEventsForVisibleMonth()
    }

    fun goToToday() {
        val today = JalaliDate.today()
        visibleMonth.value = today.firstOfMonth()
        selectedDate.value = today
        refreshEventsForVisibleMonth()
    }

    fun selectDate(date: JalaliDate) {
        selectedDate.value = date
    }

    fun addTask(form: TaskFormData, date: JalaliDate = selectedDate.value) {
        if (form.title.isBlank()) return
        viewModelScope.launch {
            val jalaliDate = date.formatIso()
            val id = taskRepository.addTask(
                title = form.title,
                notes = form.notes,
                jalaliDate = jalaliDate,
                reminderTime = form.reminderTime,
                category = form.category
            )
            val task = TaskEntity(
                id = id,
                title = form.title,
                notes = form.notes,
                jalaliDate = jalaliDate,
                reminderTime = form.reminderTime,
                category = form.category
            )
            reminderScheduler.schedule(task)
        }
    }

    fun updateTask(task: TaskEntity, form: TaskFormData) {
        if (form.title.isBlank()) return
        viewModelScope.launch {
            val updated = task.copy(
                title = form.title,
                notes = form.notes,
                reminderTime = form.reminderTime,
                category = form.category
            )
            taskRepository.updateTask(updated)
            reminderScheduler.cancel(task.id)
            if (!updated.isDone) {
                reminderScheduler.schedule(updated)
            }
        }
    }

    fun toggleTaskDone(task: TaskEntity) {
        viewModelScope.launch {
            val done = !task.isDone
            taskRepository.setDone(task.id, done)
            if (done) {
                reminderScheduler.cancel(task.id)
            } else {
                reminderScheduler.schedule(task.copy(isDone = false))
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            reminderScheduler.cancel(task.id)
            taskRepository.deleteTask(task)
        }
    }

    private fun refreshEventsForVisibleMonth() {
        val month = visibleMonth.value
        viewModelScope.launch {
            isLoadingEvents.value = true
            runCatching { eventRepository.ensureMonthCached(month.year, month.month) }
            isLoadingEvents.value = false
        }
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
