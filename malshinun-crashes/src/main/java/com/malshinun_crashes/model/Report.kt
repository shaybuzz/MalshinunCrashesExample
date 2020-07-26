package com.malshinun_crashes.model

import android.os.Build
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

internal data class Report(val time: String, val stackTrace: String) {
    var miscData: MiscData? = null

    companion object {
        fun toReport(throwable: Throwable): Report {
            val time = Date().time.toString()
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            return Report(
                time,
                sw.toString()
            )
        }
    }
}

//General data about the device and the hosting app
internal data class MiscData(
    val packageName: String,
    val appVersion: String,
    val manufacturer: String = Build.MANUFACTURER,
    val model: String = Build.MODEL,
    val version: Int = Build.VERSION.SDK_INT,
    val versionRelease: String = Build.VERSION.RELEASE
)
