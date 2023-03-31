/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import kr.co.sorizava.asrplayer.ZerothDefine
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AppApiClient {
    private val gson = GsonBuilder().setLenient().create()

    @JvmStatic
    val apiService: AppApiService
        get() = instance.create(AppApiService::class.java)

    @JvmStatic
    val boardApiService: BoardApiService
        get() = instance.create(BoardApiService::class.java)

    private val instance: Retrofit = Retrofit.Builder()
            .baseUrl(ZerothDefine.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getProvideOkHttpClient(httpInterceptor))
            .build()

    private val httpInterceptor: Interceptor
        get() = Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val builder = original.newBuilder()
            val request = builder.build()
            chain.proceed(request)
        }

    private fun getProvideOkHttpClient(interceptor: Interceptor?): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        if (interceptor != null) {
            httpClientBuilder.addNetworkInterceptor(interceptor)
        }
        httpClientBuilder.connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build()
        return httpClientBuilder.build()
    }
}