package com.example.calendartodo.data.remote

import com.example.calendartodo.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the optional time.ir API key header when [BuildConfig.TIME_IR_API_KEY] is set.
 */
class TimeIrApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            val apiKey = BuildConfig.TIME_IR_API_KEY.trim()
            if (apiKey.isNotEmpty()) {
                addHeader("x-api-key", apiKey)
            }
        }.build()
        return chain.proceed(request)
    }
}
