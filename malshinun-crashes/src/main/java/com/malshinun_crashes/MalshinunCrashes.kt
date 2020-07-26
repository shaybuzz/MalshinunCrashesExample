package com.malshinun_crashes

import android.app.Application
import android.content.Context
import android.util.Log
import com.malshinun_crashes.Utils.DEFAULT_HANDLER_PACKAGE_NAME
import com.malshinun_crashes.Utils.ONE_MINUTE
import com.malshinun_crashes.Utils.SDK_PACKAGE_NAME
import com.malshinun_crashes.model.Report
import com.malshinun_crashes.remote.Network
import com.malshinun_crashes.repository.ReportRepository
import com.malshinun_crashes.repository.ReportRepositoryImpl

class MalshinunCrashes(private val context: Context) {

    private val TAG = MalshinunCrashes::class.java.simpleName

    private var oldHandler: Thread.UncaughtExceptionHandler? = null
    private val reportApi = Network.reportApi
    private val reportRepository: ReportRepository = ReportRepositoryImpl(context)
    private val senderManger = SenderManger(context, reportApi, reportRepository, ONE_MINUTE)
    private val appLifeCycleHandler: AppLifeCycleHandler = AppLifeCycleHandler { isOnForeground ->
        senderManger.reporting(isOnForeground)
    }

    init {
        try {
            oldHandler = Thread.getDefaultUncaughtExceptionHandler()

            if (oldHandler != null && oldHandler!!.javaClass.name.startsWith(SDK_PACKAGE_NAME)) {
                //our sdk handler
                Log.d(TAG, "sdk handler")

            } else if (oldHandler != null && oldHandler!!.javaClass.name.startsWith(
                    DEFAULT_HANDLER_PACKAGE_NAME
                )
            ) {
                //android os default handler
                Log.d(TAG, "os handler")
            }

            registerToAppLifeCycleCallBack()
            handleUncaughtException()
        } catch (throwable: Throwable) {
            Log.e(TAG, "failed to report crash ${throwable.message}")
        }
    }

    private fun registerToAppLifeCycleCallBack() {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(
            appLifeCycleHandler
        )
    }

    private fun handleUncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler({ thread, throwable ->
            val report = Report.toReport(throwable)
            reportRepository.saveReport(report)

            oldHandler?.uncaughtException(thread, throwable)

            finishLastActivity()
            exit()
        })
    }

    private fun finishLastActivity() {
        appLifeCycleHandler.lastActivity.get()?.let { lastActivity ->
            lastActivity.finish()
            appLifeCycleHandler.lastActivity.clear()
        }
    }

    private fun exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(2)
    }
}
