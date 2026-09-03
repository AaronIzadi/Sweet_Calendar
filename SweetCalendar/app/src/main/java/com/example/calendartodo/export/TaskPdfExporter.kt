package com.example.calendartodo.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.example.calendartodo.R
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.GregorianDate
import com.example.calendartodo.jalali.JalaliDate
import java.io.OutputStream

/**
 * Renders task data into a multi-page PDF using the platform [PdfDocument] API.
 * Call [export] with a prepared task list; filtering is handled upstream.
 */
class TaskPdfExporter(context: Context) {

    private val bodyTypeface: Typeface = loadBodyTypeface(context)

    private fun loadBodyTypeface(context: Context): Typeface {
        return try {
            ResourcesCompat.getFont(context, R.font.quicksand_variable) ?: Typeface.SANS_SERIF
        } catch (_: Exception) {
            // Variable fonts are not supported on API 25; PDF export falls back to system sans.
            Typeface.SANS_SERIF
        }
    }

    fun export(
        request: PdfExportRequest,
        tasks: List<TaskEntity>,
        output: OutputStream
    ): PdfExportResult {
        val document = PdfDocument()
        val renderer = PageRenderer(document, request)

        try {
            renderer.drawReport(tasks)
            document.writeTo(output)
        } finally {
            document.close()
        }

        return PdfExportResult(
            taskCount = tasks.size,
            pageCount = renderer.pageCount,
            scope = request.scope
        )
    }

    private inner class PageRenderer(
        private val document: PdfDocument,
        private val request: PdfExportRequest
    ) {
        private var pageNumber = 0
        private var currentPage: PdfDocument.Page? = null
        private var canvas: Canvas? = null
        private var cursorY = 0f

        val pageCount: Int
            get() = pageNumber

        private val margin = 48f
        private val contentWidth = PAGE_WIDTH - margin * 2

        private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_INK
            textSize = 22f
            typeface = Typeface.create(bodyTypeface, Typeface.BOLD)
        }
        private val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_MUTED
            textSize = 11f
            typeface = bodyTypeface
        }
        private val sectionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_CHOC
            textSize = 13f
            typeface = Typeface.create(bodyTypeface, Typeface.BOLD)
        }
        private val taskTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_INK
            textSize = 12f
            typeface = Typeface.create(bodyTypeface, Typeface.BOLD)
        }
        private val taskMetaPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_MUTED
            textSize = 10f
            typeface = bodyTypeface
        }
        private val taskNotesPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_INK
            textSize = 10f
            typeface = bodyTypeface
        }
        private val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_MUTED
            textSize = 9f
            typeface = bodyTypeface
        }
        private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_LINE
            strokeWidth = 1f
        }
        private val pageFillPaint = Paint().apply {
            color = COLOR_PAPER
        }

        fun drawReport(tasks: List<TaskEntity>) {
            startPage()
            drawHeader(tasks)
            if (tasks.isEmpty()) {
                drawParagraph("No tasks match this export.", taskMetaPaint)
            } else {
                drawTaskSections(tasks)
            }
            finishPage()
        }

        private fun drawHeader(tasks: List<TaskEntity>) {
            drawText("Sweet Calendar", titlePaint)
            advance(6f)
            drawText("Export for ${request.userName}", subtitlePaint)
            advance(4f)
            drawText("Generated ${ExportFormatting.formatGeneratedAt(request.generatedAtMillis)}", subtitlePaint)
            advance(4f)
            drawText("Scope: ${ExportFormatting.scopeLabel(request.scope)}", subtitlePaint)
            if (tasks.isNotEmpty()) {
                advance(4f)
                val done = tasks.count { it.isDone }
                val pending = tasks.size - done
                drawText("${tasks.size} tasks · $done completed · $pending pending", subtitlePaint)
            }
            advance(14f)
            drawDivider()
            advance(16f)
        }

        private fun drawTaskSections(tasks: List<TaskEntity>) {
            val grouped = groupTasksByDay(tasks, request.calendarSystem)
            grouped.forEach { (dayLabel, dayTasks) ->
                ensureSpace(28f)
                drawText(dayLabel, sectionPaint)
                advance(10f)
                dayTasks.forEach { task ->
                    drawTask(task)
                    advance(8f)
                }
                advance(6f)
            }
        }

        private fun drawTask(task: TaskEntity) {
            val statusPrefix = "[${ExportFormatting.taskStatus(task)}] "
            drawParagraph(statusPrefix + task.title, taskTitlePaint)
            advance(2f)

            val metaParts = buildList {
                add(ExportFormatting.formatTaskDate(task.jalaliDate, request.calendarSystem))
                if (task.category.isNotBlank()) add(task.category)
                if (task.priority.isNotBlank() && task.priority != "Medium") add(task.priority)
                task.reminderTime?.let { add("Reminder $it") }
                if (task.repeatWeekly) add("Repeats weekly")
            }
            if (metaParts.isNotEmpty()) {
                drawParagraph(metaParts.joinToString(" · "), taskMetaPaint)
                advance(2f)
            }

            if (task.notes.isNotBlank()) {
                drawParagraph(task.notes, taskNotesPaint)
                advance(2f)
            }

            drawDivider()
        }

        private fun drawText(text: String, paint: TextPaint) {
            ensureSpace(paint.textSize + 4f)
            canvas?.drawText(text, margin, cursorY + paint.textSize, paint)
            advance(paint.textSize + 4f)
        }

        private fun drawParagraph(text: String, paint: TextPaint) {
            val layout = StaticLayout.Builder
                .obtain(text, 0, text.length, paint, contentWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.15f)
                .setIncludePad(false)
                .build()
            val blockHeight = layout.height.toFloat() + 4f
            ensureSpace(blockHeight)
            canvas?.let { target ->
                target.save()
                target.translate(margin, cursorY)
                layout.draw(target)
                target.restore()
            }
            advance(blockHeight)
        }

        private fun drawDivider() {
            ensureSpace(8f)
            canvas?.drawLine(margin, cursorY, PAGE_WIDTH - margin, cursorY, dividerPaint)
            advance(8f)
        }

        private fun ensureSpace(required: Float) {
            if (canvas == null || cursorY + required > PAGE_HEIGHT - margin - FOOTER_HEIGHT) {
                finishPage()
                startPage()
            }
        }

        private fun advance(delta: Float) {
            cursorY += delta
        }

        private fun startPage() {
            pageNumber++
            val pageInfo = PdfDocument.PageInfo.Builder(
                PAGE_WIDTH.toInt(),
                PAGE_HEIGHT.toInt(),
                pageNumber
            ).create()
            val page = document.startPage(pageInfo)
            currentPage = page
            canvas = page.canvas
            canvas?.drawRect(0f, 0f, PAGE_WIDTH, PAGE_HEIGHT, pageFillPaint)
            cursorY = margin
        }

        private fun finishPage() {
            val page = currentPage ?: return
            val footer = "Page $pageNumber"
            val footerWidth = footerPaint.measureText(footer)
            page.canvas.drawText(
                footer,
                PAGE_WIDTH - margin - footerWidth,
                PAGE_HEIGHT - margin / 2,
                footerPaint
            )
            document.finishPage(page)
            currentPage = null
            canvas = null
        }
    }

    private fun groupTasksByDay(
        tasks: List<TaskEntity>,
        calendarSystem: CalendarSystem
    ): List<Pair<String, List<TaskEntity>>> {
        val todayIso = JalaliDate.today().formatIso()
        val yesterdayIso = JalaliDate.today().minusDays(1).formatIso()
        return tasks
            .groupBy { it.jalaliDate }
            .toList()
            .sortedByDescending { it.first }
            .map { (iso, dayTasks) ->
                val label = formatDayHeading(iso, todayIso, yesterdayIso, calendarSystem)
                label to dayTasks.sortedWith(compareBy({ it.isDone }, { it.createdAt }))
            }
    }

    private fun formatDayHeading(
        iso: String,
        todayIso: String,
        yesterdayIso: String,
        calendarSystem: CalendarSystem
    ): String {
        val jalali = JalaliDate.parseIso(iso)
        val monthName = JalaliDate.MONTH_NAMES_EN[jalali.month - 1]
        val weekday = JalaliDate.WEEKDAY_NAMES_EN_SHORT[jalali.weekdayIndex()]
        val primary = when (iso) {
            todayIso -> "TODAY"
            yesterdayIso -> "YESTERDAY"
            else -> weekday.uppercase()
        }
        val datePart = when (calendarSystem) {
            CalendarSystem.PERSIAN -> "$monthName ${jalali.day}, ${jalali.year}"
            CalendarSystem.GREGORIAN -> {
                val gregorian = GregorianDate.fromJalali(jalali)
                val gMonth = GregorianDate.MONTH_NAMES_EN[gregorian.month - 1]
                "$gMonth ${gregorian.day}, ${gregorian.year}"
            }
        }
        return "$primary · $datePart"
    }

    companion object {
        private const val PAGE_WIDTH = 595f
        private const val PAGE_HEIGHT = 842f
        private const val FOOTER_HEIGHT = 24f

        private const val COLOR_PAPER = 0xFFFFFDF9.toInt()
        private const val COLOR_INK = 0xFF3A2317.toInt()
        private const val COLOR_CHOC = 0xFF6B4226.toInt()
        private const val COLOR_MUTED = 0xFF9A8878.toInt()
        private const val COLOR_LINE = 0xFFEAD9C4.toInt()
    }
}
