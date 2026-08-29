package com.example.calendartodo.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

data class DayEventDto(
    val description: String,
    val additional_description: String? = null,
    val is_religious: Boolean = false,
    val is_holiday: Boolean = false
)

data class DayEventsResponseDto(
    val is_holiday: Boolean = false,
    val events: List<DayEventDto> = emptyList()
)

/**
 * Talks to holidayapi.ir, a JSON API that mirrors the official Iranian
 * calendar occasions published on time.ir. One call = one Jalali day.
 */
interface HolidayApiService {

    @GET("jalali/{year}/{month}/{day}")
    suspend fun getJalaliDayEvents(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ): DayEventsResponseDto

    companion object {
        const val BASE_URL = "https://holidayapi.ir/"
    }
}
