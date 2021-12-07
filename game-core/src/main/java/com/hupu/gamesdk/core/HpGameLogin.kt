package com.hupu.gamesdk.core

import android.app.Activity
import android.support.v4.app.DialogFragment
import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpLoadingFragment
import com.hupu.gamesdk.base.HpNetService
import com.hupu.gamesdk.base.HupuActivityLifecycleCallbacks
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.login.HpLoginFragment
import com.hupu.gamesdk.login.HpLoginManager
import com.hupu.gamesdk.login.HpLoginService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.json.JSONObject


class HpGameLogin {
    private val service = HpNetService.getRetrofit().create(HpLoginService::class.java)
    internal fun start(activity: Activity, listener: HpLoginListener) {
        //app不合法
        if (!HpGameAppInfo.legal) {
            listener.fail(ErrorType.AppNotLegal.code, ErrorType.AppNotLegal.msg)
            return
        }
        val findFragmentByTag = activity.fragmentManager.findFragmentByTag("HpLoginFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }

        val hpLoadingFragment = HpLoadingFragment()
        hpLoadingFragment.isCancelable = false
        hpLoadingFragment.show(activity.fragmentManager,"")
        //检测accessToken是否有效


        HupuActivityLifecycleCallbacks.getScope(activity)?.launch {
            try {
                flow {
                    try {
                        val it = service.checkAccessToken()
                        emit(it)
                    }catch (e: Exception) {
                        e.printStackTrace()
                        emit(null)
                    }
                }.flowOn(Dispatchers.IO).collectLatest {
                    hpLoadingFragment.dismiss()
                    if (it?.code?:-1 == 0) {
                        val jsonObject = JSONObject()
                        val userInfo = HpLoginManager.getUserInfo()
                        jsonObject.put("puid",userInfo?.puid)
                        jsonObject.put("nickname",userInfo?.nickName)
                        jsonObject.put("head",userInfo?.head)
                        jsonObject.put("access_token",userInfo?.accessToken)
                        listener.success(jsonObject)
                    }else {
                        val hpLoginFragment = HpLoginFragment()
                        hpLoginFragment.registerLoginListener(listener)
                        hpLoginFragment.isCancelable = false
                        hpLoginFragment.show(activity.fragmentManager,"HpLoginFragment")
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    class Builder {
        fun build(): HpGameLogin {
            return HpGameLogin()
        }
    }


    interface HpLoginListener {
        fun success(jsonObject: JSONObject)
        fun fail(code: Int,msg: String?)
    }

    interface HpLogoutListener {
        fun success()
        fun fail()
    }
}