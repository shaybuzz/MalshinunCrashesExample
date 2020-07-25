package com.malshinun_crashes

import android.content.Context
import android.os.Handler
import android.util.Log
import com.malshinun_crashes.model.MiscData
import com.malshinun_crashes.remote.ReportApi
import com.malshinun_crashes.remote.ReportResponse
import com.malshinun_crashes.repository.ReportRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class SenderManger(
    private val context: Context,
    private val reportApi: ReportApi,
    private val reportRepository: ReportRepository,
    private val interval: Long
) : Runnable {
    private val TAG = SenderManger::class.java.simpleName
    private val handler = Handler()
    private var isCanceled = false

    fun reporting(enabled: Boolean) {
        if (enabled) {
            isCanceled = false
            handler.post(this)
        } else {
            isCanceled = true
            handler.removeCallbacks(this)
        }
    }

    override fun run() {
        if (!isCanceled) {
            sendReport()
            handler.postDelayed(this@SenderManger, interval)
        }
    }

    private fun sendReport() {
        val report = reportRepository.getReport()
        report?.let { lastReport ->
            //init misc data only before sending to server - no need to save/load such data in the repository
            lastReport.miscData =
                MiscData(
                    Utils.getPackage(context), Utils.getVersionName(context)
                )
            try {
                Log.d(TAG, "####\n\nfound crash report to send $report\n\n####")
                reportApi.sendReport(lastReport).enqueue(object : Callback<ReportResponse> {
                    override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                        Log.e(TAG, "failed sending report ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<ReportResponse>,
                        response: Response<ReportResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                if (it.success) {
                                    //after sending the report to the server we can remove it from our local store
                                    reportRepository.delete(lastReport)
                                } else {
                                    Log.e(
                                        TAG,
                                        "Got response from server - Sending was not successful"
                                    )
                                }
                            } ?: run() {
                                Log.e(TAG, "Sending was not successful - Body is null")
                            }
                        } else {
                            Log.e(TAG, "Sending was not successful")
                            //testing delete on error
                            //reportRepository.delete(lastReport)
                        }
                    }
                })
            } catch (e: Throwable) {
                Log.e(TAG, "Some error while sending report to server ${e.message}")
            }
        }
    }
}