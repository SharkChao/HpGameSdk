package com.hupu.gamesdk.report

import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import com.google.gson.Gson
import com.hupu.gamesdk.core.HpGame
import com.hupu.gamesdk.base.HPAppInfo
import com.hupu.gamesdk.base.HPDeviceInfo
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.base.HpNetService
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.login.HpLoginManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object HpReportManager {
    private val deviceId: String by lazy {
        HPDeviceInfo.getDeviceID(HpGame.context)
    }

    private val timeZone: String by lazy {
        HPDeviceInfo.getTimeZone()
    }

    private val versionName: String by lazy {
        HPAppInfo.getVersionName(HpGame.context)
    }

    private var appStartTime: Long = 0
    private var appResumeTime: Long = 0

    private val service = HpNetService.getRetrofit().create(HpReportService::class.java)
    fun init() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                appStartTime = System.currentTimeMillis()
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                appResumeTime = System.currentTimeMillis()

                if (appResumeTime - appStartTime > 3000) {
                    //热启动
                    postHeartBeat(1)
                }
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)

                val hashMap = HashMap<String, Any?>()
                hashMap["duration"] = System.currentTimeMillis() - appResumeTime
                hashMap["start_time"] = appResumeTime
                hashMap["end_time"] = System.currentTimeMillis()
                HpReportManager.report(HpGameConstant.REPORT_PLAY_DURATION,hashMap)

                postHeartBeat(0)
            }
        })
    }


    private fun createReportBean(type: String,hashMap: HashMap<String,Any?>): HpReportEntity {
        val hpReportEntity = HpReportEntity()
        hpReportEntity.os = "Android"
        hpReportEntity.osv = Build.VERSION.RELEASE
        hpReportEntity.sv = HpGame.sdkVersion.toString()
        hpReportEntity.av = versionName
        hpReportEntity.aid = HpGameAppInfo.appId
        hpReportEntity.puid = HpLoginManager.getUserInfo()?.puid
        hpReportEntity.clt = deviceId
        hpReportEntity.mfrs = Build.BRAND
        hpReportEntity.model = Build.MODEL
        hpReportEntity.tz = timeZone
        hpReportEntity.et = System.currentTimeMillis()

        hpReportEntity.type = type
        hpReportEntity.pdata = hashMap
        return hpReportEntity
    }

    fun report(type: String,hashMap: HashMap<String,Any?>) {
        val createReportBean = createReportBean(type, hashMap)
        val requestBody = HPAppInfo.convertJson(
            Gson().toJson(createReportBean))

        ProcessLifecycleOwner.get().lifecycle.coroutineScope.launch(Dispatchers.IO) {
            try {
//                service.postReport(requestBody)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postHeartBeat(type: Int) {
        val userInfo = HpLoginManager.getUserInfo() ?: return
        ProcessLifecycleOwner.get().lifecycle.coroutineScope.launch(Dispatchers.IO) {
            try {
                val hashMap = HashMap<String, Any?>()
                hashMap["puid"] = userInfo.puid
                hashMap["type"] = type
                service.postHeartBeat(hashMap)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}