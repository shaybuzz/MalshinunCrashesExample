package com.malshinun_crashes.repository

import android.content.Context
import com.malshinun_crashes.Utils.SDK_PACKAGE_NAME
import com.malshinun_crashes.model.Report

internal class ReportRepositoryImpl(context: Context) :
    ReportRepository {
    private val PREFERENCE_FILE_NAME = "$SDK_PACKAGE_NAME-crash-reports"

    //implementing repository by writing/reading local share preferences with private mode
    //where the time when crash accord (its a string format) is part of the report info
    //and acts as the key in the sharepref for that crash
    private val sharedPref =
        context.applicationContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

    override fun saveReport(report: Report) {
        sharedPref.edit().putString(report.time, report.stackTrace).apply()
    }

    override fun getReport(): Report? {
        sharedPref.all.keys.firstOrNull()?.let { time ->
            sharedPref.getString(time, null)?.let { stackTrace ->
                return Report(
                    time,
                    stackTrace
                )
            }
        }
        return null
    }

    override fun delete(report: Report) {
        sharedPref.edit().remove(report.time).apply()
    }
}