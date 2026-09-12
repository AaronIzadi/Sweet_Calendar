package com.example.calendartodo.data.remote

import com.example.calendartodo.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds [x-api-key] only when the app is configured with a real key. Some time.ir
 * anonymous requests get rejected or rate-limited when an empty header is sent.
 */
class TimeIrApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val apiKey = BuildConfig.TIME_IR_API_KEY.trim()
        return if (apiKey.isEmpty()) {
            chain.proceed(request)
        } else {
            chain.proceed(request.newBuilder().addHeader("x-api-key", apiKey).build())
        }
    }
}
