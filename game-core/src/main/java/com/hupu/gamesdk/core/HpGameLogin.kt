package com.hupu.gamesdk.core

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpLoadingFragment
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.login.HpLoginFragment
import com.hupu.gamesdk.login.HpLoginManager
import com.hupu.gamesdk.login.HpLoginViewModel
import org.json.JSONObject

class HpGameLogin {
    internal fun start(activity: AppCompatActivity, listener: HpLoginListener) {
        //app不合法
        if (!HpGameAppInfo.legal) {
            listener.fail(ErrorType.AppNotLegal.code, ErrorType.AppNotLegal.msg)
            return
        }
        val findFragmentByTag = activity.supportFragmentManager.findFragmentByTag("HpLoginFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }

        val hpLoadingFragment = HpLoadingFragment()
        hpLoadingFragment.isCancelable = false
        hpLoadingFragment.show(activity.supportFragmentManager,"")
        //检测accessToken是否有效
        val hpLoginViewModel = ViewModelProviders.of(activity)[HpLoginViewModel::class.java]
        hpLoginViewModel.checkAccessToken().observe(activity, Observer {
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
                hpLoginFragment.show(activity.supportFragmentManager,"HpLoginFragment")
            }
        })
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