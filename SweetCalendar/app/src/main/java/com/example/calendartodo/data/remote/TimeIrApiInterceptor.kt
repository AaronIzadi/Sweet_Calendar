package com.example.calendartodo.data.remote

import com.example.calendartodo.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds [x-api-key] when [BuildConfig.TIME_IR_API_KEY] is set (same as cm-calendar-service).
 * When no key is configured, sends an empty header so time.ir accepts anonymous requests
 * without waiting through the service's 65s rate-limit retry window.
 */
class TimeIrApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val apiKey = BuildConfig.TIME_IR_API_KEY.trim()
        builder.addHeader("x-api-key", apiKey)
        return chain.proceed(builder.build())
    }
}
