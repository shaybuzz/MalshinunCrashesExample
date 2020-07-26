package com.malshinun_crashes

import android.content.Context
import android.os.Handler
import android.util.Log
import com.malshinun_crashes.model.MiscData
import com.malshinun_crashes.remote.ReportApi
import com.malshinun_crashes.repository.ReportRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class SenderManger(
    private val context: Context,
    private val reportApi: ReportApi,
    private val reportRepository: ReportRepository,
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

    private fun sendReportTask() {
        val report = reportRepository.getReport()
        report?.let { lastReport ->
            try {
                //init misc data and add it to report - this is done only before sending report to server
                //- no need to save/load such data in the repository
                lastReport.miscData =
                    MiscData(Utils.getPackage(context), Utils.getVersionName(context))
                val response = reportApi.sendReport(lastReport).execute()
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