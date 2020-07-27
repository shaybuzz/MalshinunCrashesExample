package com.malshinun_crashes

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.malshinun_crashes.model.MiscData
import com.malshinun_crashes.remote.ReportApi
import com.malshinun_crashes.repository.ReportRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class SenderManger(
    private val reportApi: ReportApi,
    private val reportRepository: ReportRepository,
    private val miscData: MiscData,
    private val interval: Long
) : Runnable {
    private val TAG = SenderManger::class.java.simpleName
    private val executorService: ExecutorService = Executors.newFixedThreadPool(1)
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
            executorService.execute {
                sendReportTask()
            }
            handler.postDelayed(this@SenderManger, interval)
        }
    }

    fun sendReportTask() {
        val report = reportRepository.getReport()
        report?.let { lastReport ->
            try {
                //add the miscData before sending to server
                //no need to save/load such data in the repository
                lastReport.miscData = miscData
                val response = reportApi.sendReport(Gson().toJson(lastReport)).execute()
                if (response.isSuccessful) {
                    //after sending the report to the server we can remove it from our local repository
                    reportRepository.delete(lastReport)
                } else {
                    //dont remove the report and try to send it next time
                    Log.e(TAG, "failed sending report")
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Some error while sending report to server ${e.message}")
            }
        }
    }
}