package com.malshinun_crashes

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import java.lang.ref.WeakReference

internal class AppLifeCycleHandler(private val appOnForeground: (Boolean) -> Unit) :
    Application.ActivityLifecycleCallbacks {
    private val delay = 500L
    private val handler = Handler()
    private var isResumed = false
    private var wasOnBackground = true
    lateinit var lastActivity: WeakReference<Activity>
        private set

    //wait each pause - wait for next activity resume - to avoid call back when pausing from activity to another activity
    override fun onActivityPaused(activity: Activity) {
        isResumed = false
        handler.postDelayed({
            //may resume from passing to another activity
            if (!isResumed) {
                appOnForeground.invoke(false)
                wasOnBackground = true
            }
        }, delay)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        lastActivity = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        isResumed = true
        handler.postDelayed({
            if (isResumed && wasOnBackground) {
                wasOnBackground = false
                appOnForeground.invoke(true)
            }
        }, delay)
    }
}