package com.malshinun_crashes.repository

import android.content.Context
import com.malshinun_crashes.model.Report

internal class ReportRepositoryImpl(context: Context) :
    ReportRepository {
    private val PREFERENCE_FILE_NAME = "com.tut.mycarshreporterplayground.sdk.crashReporter"

    //implementing repository by writing/reading local share preferences with private mode
    //where the time of the report (its a string format) is the key in the sharepref of the stacktrace
    private val sharedPref =
        context.applicationContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

    override fun saveReport(report: Report) {
        sharedPref.edit().putString(report.time, report.stackTrace).commit()
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