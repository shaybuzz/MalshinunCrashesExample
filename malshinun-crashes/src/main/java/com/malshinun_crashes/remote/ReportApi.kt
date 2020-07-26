package com.malshinun_crashes.remote

import com.malshinun_crashes.model.Report
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

internal interface ReportApi {
    @POST("sendreport")
    fun sendReport(@Body report: Report): Call<Void>
}