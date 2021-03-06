package com.hupu.gamesdk.core

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.base.HpLogUtil
import com.hupu.gamesdk.certification.CertificationResult
import com.hupu.gamesdk.certification.HpGameCertification
import com.hupu.gamesdk.certification.HpImmaturityFragment
import com.hupu.gamesdk.config.HpConfigManager
import com.hupu.gamesdk.init.HpCheckInitManager
import com.hupu.gamesdk.init.HpCheckInitResult
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.login.HpLoginManager
import com.hupu.gamesdk.report.HpReportManager
import org.json.JSONObject
import java.util.concurrent.CountDownLatch

class HpGame private constructor(private val builder: Builder){

    companion object {
        internal var debug: Boolean = false
        @SuppressLint("StaticFieldLeak")
        internal var context: Context? = null
        internal val sdkVersion = 1
        private val logoutListeners = ArrayList<HpGameLogin.HpLogoutListener>()


        fun startLogin(activity: AppCompatActivity,listener: HpGameLogin.HpLoginListener) {
            startLoginFragment(activity,listener)
        }

        fun logout() {
            HpReportManager.postHeartBeat(0) {
                HpLoginManager.clearUserInfo()
                logoutListeners.forEach {
                    it.success()
                }
            }
        }

        fun registerGlobalLogoutListener(listener: HpGameLogin.HpLogoutListener?) {
            if (listener != null) {
                logoutListeners.add(listener)
            }
        }

        fun report(type: String,hashMap: HashMap<String,Any?>) {
            HpReportManager.report(type, hashMap)
        }

        private fun startLoginFragment(
            activity: AppCompatActivity,
            listener: HpGameLogin.HpLoginListener
        ) {
            HpGameLogin.Builder()
                .build()
                .start(activity,object : HpGameLogin.HpLoginListener{
                    override fun success(jsonObject: JSONObject) {
                        val hashMap = HashMap<String, Any?>()
                        hashMap["result"] = 1
                        HpReportManager.report(HpGameConstant.REPORT_HUPU_LOGIN,hashMap)
                        HpReportManager.postHeartBeat(1,null)

                        HpLogUtil.e("????????????!,${jsonObject}")
                        startCertification(activity,object :HpGameCertification.HpCertificationListener{
                           override fun success(response: CertificationResult.CertficationResponse) {

                               HpLogUtil.e("??????????????????!")
                               if (response.adult){
                                   HpLogUtil.e("?????????????????????")
                                   listener.success(jsonObject)
                                   val imHashMap = HashMap<String, Any?>()
                                   imHashMap["result"] = 1
                                   HpReportManager.report(HpGameConstant.REPORT_IMMATURITY,imHashMap)
                               }else {
                                   showImmaturityFragment(activity){
                                       listener.fail(ErrorType.Immaturity.code,ErrorType.Immaturity.msg)
                                   }
                                   val imHashMap = HashMap<String, Any?>()
                                   imHashMap["result"] = 0
                                   HpReportManager.report(HpGameConstant.REPORT_IMMATURITY,imHashMap)
                                   HpLogUtil.e("???????????????????????????")
                               }
                           }

                           override fun fail(code: Int, msg: String?) {
                               Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show()
                               listener.fail(code, msg)

                               HpLogUtil.e("??????????????????!,???????????????${msg},????????????${code}")
                           }
                       })
                    }

                    override fun fail(code: Int, msg: String?) {
                        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show()
                        listener.fail(code, msg)

                        val hashMap = HashMap<String, Any?>()
                        hashMap["result"] = 0
                        hashMap["error_msg"] = msg
                        HpReportManager.report(HpGameConstant.REPORT_HUPU_LOGIN,hashMap)
                        HpLogUtil.e("????????????!,???????????????${msg},????????????${code}")
                    }
                })
        }


        private fun startCertification(
            activity: AppCompatActivity,
            listener:HpGameCertification.HpCertificationListener
        ) {
            HpGameCertification.Builder()
                .build()
                .start(activity,listener)
        }

        private fun showImmaturityFragment(activity: AppCompatActivity,listener:  (()->Unit)?) {
            if (activity.isDestroyed) {
                return
            }
            val findFragmentByTag = activity.supportFragmentManager.findFragmentByTag("HpImmaturityFragment")
            if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
                findFragmentByTag.dismiss()
            }

            val fragment = HpImmaturityFragment()
            fragment.registerListener(listener)
            fragment.isCancelable = false
            fragment.show(activity.supportFragmentManager,"HpImmaturityFragment")
        }
    }

    @MainThread
    fun init(context: Context) {
        val countDownLatch = CountDownLatch(1)
        HpGameAppInfo.appId = builder.mAppId
        HpGameAppInfo.appKey = builder.mAppKey
        debug = builder.mDebug
        Companion.context = context

        //???????????????
        HpConfigManager.initConfig()
        //?????????????????????
        HpReportManager.init()
        //???????????????
        HpCheckInitManager.checkAppLegal(object : HpCheckInitManager.HpCheckInitListener{
            override fun success(hpCheckInitResponse: HpCheckInitResult.HpCheckInitResponse) {
                HpGameAppInfo.appName = hpCheckInitResponse.name
                HpGameAppInfo.appIcon = hpCheckInitResponse.icon
                HpGameAppInfo.legal = true
                countDownLatch.countDown()


                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 1
                HpReportManager.report(HpGameConstant.REPORT_INIT_RESULT,hashMap)
                HpLogUtil.e("??????sdk???????????????!,???????????????${HpGameAppInfo.appName},??????id???${HpGameAppInfo.appId}")
            }

            override fun fail(code: Int, msg: String?) {
                countDownLatch.countDown()

                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 0
                hashMap["error_msg"] = msg
                HpReportManager.report(HpGameConstant.REPORT_INIT_RESULT,hashMap)
                HpLogUtil.e("??????sdk???????????????!,??????id???${HpGameAppInfo.appId},???????????????${msg},????????????${code}")
            }
        })
        countDownLatch.await()
    }

    /**
     * ?????????
     */
    @MainThread
    fun initAsync(context: Context,listener: HpGameInitListener) {

        HpGameAppInfo.appId = builder.mAppId
        HpGameAppInfo.appKey = builder.mAppKey
        debug = builder.mDebug
        Companion.context = context

        //???????????????
        HpConfigManager.initConfig()
        //???????????????
        HpCheckInitManager.checkAppLegal(object : HpCheckInitManager.HpCheckInitListener{
            override fun success(hpCheckInitResponse: HpCheckInitResult.HpCheckInitResponse) {
                HpGameAppInfo.appName = hpCheckInitResponse.name
                HpGameAppInfo.appIcon = hpCheckInitResponse.icon
                HpGameAppInfo.legal = true
                listener.success()


                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 1
                HpReportManager.report(HpGameConstant.REPORT_INIT_RESULT,hashMap)
                HpLogUtil.e("??????sdk???????????????!,???????????????${HpGameAppInfo.appName},??????id???${HpGameAppInfo.appId}")
            }

            override fun fail(code: Int, msg: String?) {
                listener.fail(code, msg)

                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 0
                hashMap["error_msg"] = msg
                HpReportManager.report(HpGameConstant.REPORT_INIT_RESULT,hashMap)

                HpLogUtil.e("??????sdk???????????????!,??????id???${HpGameAppInfo.appId},???????????????${msg},????????????${code}")
            }
        })
    }


    class Builder {
        internal var mDebug: Boolean = false
        internal var mAppId: String? = null
        internal var mAppKey: String? = null
        fun setDebug(debug: Boolean) = apply {
            mDebug = debug
        }

        fun setAppId(appId: String) = apply {
            this.mAppId = appId
        }

        fun setAppKey(appKey: String) = apply {
            this.mAppKey = appKey
        }

        fun build(): HpGame {
            return HpGame(this)
        }
    }


    interface HpGameInitListener {
        fun success()
        fun fail(code: Int,msg: String?)
    }
}