package com.hupu.gamesdk.report

import android.arch.lifecycle.ProcessLifecycleOwner
import android.os.Build
import com.google.gson.Gson
import com.hupu.gamesdk.base.*
import com.hupu.gamesdk.core.HpGame
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.login.HpLoginManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

object HpReportManager {
    private val mainScope = MainScope()
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
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : ApplicationLifeObserver() {

            override fun onCreate() {
                appStartTime = System.currentTimeMillis()
            }

            override fun onResume() {
                appResumeTime = System.currentTimeMillis()

                if (appResumeTime - appStartTime > 3000) {
                    //热启动
                    postHeartBeat(1,null)
                }
            }

            override fun onPause() {

                val hashMap = HashMap<String, Any?>()
                hashMap["duration"] = System.currentTimeMillis() - appResumeTime
                hashMap["start_time"] = appResumeTime
                hashMap["end_time"] = System.currentTimeMillis()
                HpReportManager.report(HpGameConstant.REPORT_PLAY_DURATION,hashMap)

                postHeartBeat(0,null)
            }
        })
    }


    private fun createReportBean(type: String,hashMap: HashMap<String,Any?>): HpReportEntity {
        val hpReportEntity = HpReportEntity()

        try {
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
        }catch (e: Exception) {
            e.printStackTrace()
        }
        return hpReportEntity
    }

    fun report(type: String,hashMap: HashMap<String,Any?>) {
        val createReportBean = createReportBean(type, hashMap)

        val requestBody = HPAppInfo.convertJson(
            Gson().toJson(createReportBean)).toRequestBody("text/plain".toMediaTypeOrNull())
        mainScope.launch(Dispatchers.IO) {
            try {
                service.postReport(requestBody)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postHeartBeat(type: Int,callback: (()->Unit)?) {
        val userInfo = HpLoginManager.getUserInfo() ?: return
        mainScope.launch(Dispatchers.IO) {
            try {
                val hashMap = HashMap<String, Any?>()
                hashMap["puid"] = userInfo.puid
                hashMap["type"] = type
                service.postHeartBeat(hashMap)

                ExecutorManager.instance.mainExecutor.execute {
                    callback?.invoke()
                }
            }catch (e: Exception) {
                e.printStackTrace()
                ExecutorManager.instance.mainExecutor.execute {
                    callback?.invoke()
                }
            }
        }
    }

}