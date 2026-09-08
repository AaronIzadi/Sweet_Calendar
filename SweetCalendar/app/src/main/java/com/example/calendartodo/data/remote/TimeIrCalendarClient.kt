package com.example.calendartodo.data.remote

import com.example.calendartodo.BuildConfig
import kotlinx.coroutines.delay
import retrofit2.HttpException

/**
 * Fetches Persian-month occasions from time.ir using the same request contract and retry
 * policy as [cm-calendar-service TimeIrCalendarSource].
 */
class TimeIrCalendarClient(
    private val api: TimeIrApiService
) {
    suspend fun fetchMonth(persianYear: Int, persianMonth: Int): TimeIrCalendarMonthDto {
        val hasApiKey = BuildConfig.TIME_IR_API_KEY.trim().isNotEmpty()

        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                val envelope = api.getMonthEvents(persianYear, persianMonth)
                return envelope.data
                    ?: error("time.ir returned no calendar data for $persianYear/${persianMonth.toString().padStart(2, '0')}")
            } catch (http: HttpException) {
                val anonymousRateLimited = !hasApiKey &&
                    (http.code() == 401 || http.code() == 429)
                if (!anonymousRateLimited || attempt == MAX_ATTEMPTS - 1) {
                    throw http
                }
                delay(retryDelayMs(http))
            }
        }

        error("time.ir request retry loop exited unexpectedly")
    }

    private fun retryDelayMs(http: HttpException): Long {
        val retryAfterSeconds = http.response()
            ?.headers()
            ?.get("Retry-After")
            ?.toLongOrNull()
        return when {
            retryAfterSeconds != null && retryAfterSeconds > 0 -> retryAfterSeconds * 1000
            else -> ANONYMOUS_RATE_LIMIT_DELAY_MS
        }
    }

    private companion object {
        const val MAX_ATTEMPTS = 3
        const val ANONYMOUS_RATE_LIMIT_DELAY_MS = 65_000L
    }
}
