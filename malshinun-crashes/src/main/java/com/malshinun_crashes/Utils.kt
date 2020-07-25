package com.malshinun_crashes

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

internal object Utils {
    const val ONE_MINUTE = 60000L
    const val SDK_PACKAGE_NAME = "com.tut.mycarshreporterplayground"
    const val DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os"

    fun getPackage(context: Context):String{
        return context.applicationContext.packageName
    }

    fun getVersionName(context: Context): String {
        try {
            return getPackageInfo(context.applicationContext)?.versionName ?: "not found"
        } catch (e: PackageManager.NameNotFoundException) {
            return "not found"
        }
    }

    fun getPackageInfo(context: Context): PackageInfo? {
        return context.applicationContext.packageManager.getPackageInfo(getPackage(context), 0)
    }

}