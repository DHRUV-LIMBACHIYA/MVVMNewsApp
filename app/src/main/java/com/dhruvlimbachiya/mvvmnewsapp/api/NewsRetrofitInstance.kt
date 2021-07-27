package com.dhruvlimbachiya.mvvmnewsapp.api

import android.util.Log
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants.BASE_URL
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants.NETWORK_CALL_TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Dhruv Limbachiya on 27-07-2021.
 */
object NewsRetrofitInstance {

    private const val TAG = "NewsLogger"

    // Log the HTTP request & response body.
    private val logger = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger{
        override fun log(message: String) {
            Log.d(TAG, message) // Added a custom tag for logging
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Creates an OkHttpClient with interceptor & time out properties.
    private val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(logger)
        readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
        writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
    }.build()

    // Set up retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    // Expose Retrofit Instance
    val api by lazy {
        retrofit.create(NewsApi::class.java)
    }
    
    
}