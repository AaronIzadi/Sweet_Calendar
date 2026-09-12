package com.example.calendartodo.data.remote

import com.example.calendartodo.BuildConfig
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Fetches Persian-month occasions from time.ir using a conservative retry policy.
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
                    (http.code() == 401 || http.code() == 403 || http.code() == 429)
                if (!anonymousRateLimited || attempt == MAX_ATTEMPTS - 1) {
                    throw http
                }
                delay(retryDelayMs(http))
            } catch (io: IOException) {
                if (attempt == MAX_ATTEMPTS - 1) throw io
                delay(retryDelayMs(io))
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

    private fun retryDelayMs(io: IOException): Long = when (io) {
        is UnknownHostException,
        is SocketTimeoutException -> TRANSIENT_NETWORK_DELAY_MS
        else -> TRANSIENT_NETWORK_DELAY_MS
    }

    private companion object {
        const val MAX_ATTEMPTS = 3
        const val ANONYMOUS_RATE_LIMIT_DELAY_MS = 65_000L
        const val TRANSIENT_NETWORK_DELAY_MS = 10_000L
    }
}
