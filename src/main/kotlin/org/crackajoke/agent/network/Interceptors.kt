package org.crackajoke.agent.network

import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.net.UnknownHostException

/**
 * Created by Rajendhiran Easu on 27/08/25.
 * Description: Interceptors for OkHttpClient
 */

object Interceptors {

    fun headerInterceptors(headers: Map<String, String>?) = Interceptor {
        val requestBuilder = it.request().newBuilder()
        headers?.forEach { data ->
            requestBuilder.addHeader(data.key, data.value)
        }
        it.proceed(requestBuilder.build())
    }

    fun connectionAliveInterceptor(connectionPool: ConnectionPool) = Interceptor { chain ->
        val request = chain.request()
        try {
            chain.proceed(request)
        } catch (_: UnknownHostException) {
            connectionPool.evictAll()
            chain.proceed(request)
        }
    }

    fun logInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
