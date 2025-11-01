package org.crackajoke.agent.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.crackajoke.agent.network.NetworkConstant.CONNECTION_TIMEOUT
import org.crackajoke.agent.network.NetworkConstant.READ_TIMEOUT
import org.crackajoke.agent.network.NetworkConstant.WRITE_TIMEOUT
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Created by Rajendhiran Easu on 27/08/25.
 * Description: Singleton API client using Retrofit and OkHttp
 */

object APIClient {

    private val connectionPool by lazy {
        ConnectionPool()
    }

    private val apiClient by lazy {
        OkHttpClient.Builder()
            .connectionPool(connectionPool)
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(Interceptors.connectionAliveInterceptor(connectionPool))
            .addNetworkInterceptor(Interceptors.logInterceptor())
            .build()
    }

    private val contentType = "application/json".toMediaType()

    private val format = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .addConverterFactory(format.asConverterFactory(contentType))
    }

    fun <T> createService(
        baseUrl: String,
        headers: Map<String, String>? = null,
        tClass: Class<T>
    ): T {

        val client = apiClient.newBuilder()
            .addInterceptor(Interceptors.headerInterceptors(headers))
            .build()

        retrofitBuilder.baseUrl(baseUrl)
        retrofitBuilder.client(client)

        return retrofitBuilder.build().create(tClass)
    }

    fun cancelAll() {
        try {
            apiClient.dispatcher.cancelAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
