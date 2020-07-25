package com.sw.malshinuncrashesexample

import android.app.Application
import com.malshinun_crashes.MalshinunCrashes

class ExampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MalshinunCrashes(this)
    }
}