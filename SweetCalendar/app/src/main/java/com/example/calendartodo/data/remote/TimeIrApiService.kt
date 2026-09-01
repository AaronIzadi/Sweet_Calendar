package com.example.calendartodo.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class TimeIrEnvelopeDto<T>(
    val data: T? = null
)

data class TimeIrCalendarMonthDto(
    @SerializedName("created_date")
    val createdDate: String? = null,
    @SerializedName("event_list")
    val eventList: List<TimeIrEventDto> = emptyList()
)

data class TimeIrEventDto(
    val id: Int = 0,
    val title: String = "",
    val body: String? = null,
    val base: Int = 0,
    @SerializedName("gregorian_year")
    val gregorianYear: Int = 0,
    @SerializedName("gregorian_month")
    val gregorianMonth: Int = 0,
    @SerializedName("gregorian_day")
    val gregorianDay: Int = 0,
    @SerializedName("jalali_year")
    val jalaliYear: Int = 0,
    @SerializedName("jalali_month")
    val jalaliMonth: Int = 0,
    @SerializedName("jalali_day")
    val jalaliDay: Int = 0,
    @SerializedName("hijri_year")
    val hijriYear: Int? = null,
    @SerializedName("hijri_month")
    val hijriMonth: Int? = null,
    @SerializedName("hijri_day")
    val hijriDay: Int? = null,
    @SerializedName("is_holiday")
    val isHoliday: Boolean = false
)

/**
 * Official Iranian calendar occasions from time.ir (same API used by cm-calendar-service).
 * One request returns every event in a Persian month.
 */
interface TimeIrApiService {

    @GET("v1/event/fa/events/calendar")
    suspend fun getMonthEvents(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int = 0,
        @Query("base1") base1: Int = 0,
        @Query("base2") base2: Int = 1,
        @Query("base3") base3: Int = 2
    ): TimeIrEnvelopeDto<TimeIrCalendarMonthDto>

    companion object {
        const val BASE_URL = "https://api.time.ir/"
    }
}
