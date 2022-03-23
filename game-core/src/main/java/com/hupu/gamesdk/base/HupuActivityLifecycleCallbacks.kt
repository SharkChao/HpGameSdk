package com.hupu.gamesdk.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.util.*

object HupuActivityLifecycleCallbacks: Application.ActivityLifecycleCallbacks {

    private val scopeMap = WeakHashMap<Activity, CoroutineScope>()
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
       if (activity != null) {
           scopeMap[activity] = MainScope()
       }
    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (activity != null) {
            scopeMap[activity]?.cancel()
            scopeMap.remove(activity)
        }
    }

    fun getScope(activity: Activity): CoroutineScope? {
        return scopeMap[activity] ?: MainScope()
    }
}