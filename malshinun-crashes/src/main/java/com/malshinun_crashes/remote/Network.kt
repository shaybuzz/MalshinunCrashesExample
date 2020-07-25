package com.malshinun_crashes.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class Network {
    private val BASE_URL = "https://jsonplaceholder.typicode.com/"
    val reportApi by lazy { retrofit.create(ReportApi::class.java) }

    private val okHttpClient by lazy { OkHttpClient.Builder().build() }

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}

