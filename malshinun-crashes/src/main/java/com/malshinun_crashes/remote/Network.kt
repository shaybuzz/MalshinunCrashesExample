package com.malshinun_crashes.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object Network {
    private val BASE_URL = "https://jsonplaceholder.typicode.com/"
    val reportApi by lazy { retrofit.create(ReportApi::class.java) }

    private val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
}

