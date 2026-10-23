package com.sweetcalendar.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * [Nager.Date](https://date.nager.at) public holiday API (used for Gregorian calendar mode).
 */
interface NagerApiService {

    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String,
    ): List<NagerHolidayDto>

    companion object {
        const val BASE_URL = "https://date.nager.at/"
    }
}

data class NagerHolidayDto(
    val date: String,
    @SerializedName("localName") val localName: String,
    val name: String,
    @SerializedName("countryCode") val countryCode: String,
    val types: List<String> = emptyList(),
)
