package com.sweetcalendar.data.remote

class NagerHolidaysClient(
    private val api: NagerApiService,
) {
    suspend fun fetchYear(year: Int, countryCode: String): List<NagerHolidayDto> {
        val code = countryCode.uppercase().take(2)
        require(code.length == 2) { "Invalid country code: $countryCode" }
        return api.getPublicHolidays(year, code)
    }
}
