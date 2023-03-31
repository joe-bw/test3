package kr.co.sorizava.asrplayer.network

import kr.co.sorizava.asrplayer.ZerothDefine
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



object ApiManager {


    private var builder: Retrofit? = null

    fun createServer(): ApiService? {
        builder = Retrofit.Builder()
            .baseUrl(ZerothDefine.API_AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
        return builder?.create(ApiService::class.java)
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getHttpLoginInterceptor())
            .connectTimeout((20 * 1000).toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout((20 * 1000).toLong(), TimeUnit.MILLISECONDS)
            .readTimeout((20 * 1000).toLong(), TimeUnit.MILLISECONDS)
            .build()
    }

    private fun getHttpLoginInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

}