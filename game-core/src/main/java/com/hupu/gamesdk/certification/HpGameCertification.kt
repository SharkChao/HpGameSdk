package com.hupu.gamesdk.certification

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.hupu.gamesdk.base.*
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.base.HpNetService
import com.hupu.gamesdk.base.activitycallback.ActResultRequest
import com.hupu.gamesdk.base.isSuccess
import com.hupu.gamesdk.login.HpLoginManager
import com.hupu.gamesdk.report.HpReportManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.lang.Exception

internal class HpGameCertification {
    private val service = HpNetService.getRetrofit().create(HpCertificationService::class.java)
    fun start(activity: Activity, listener: HpCertificationListener) {
        HpLogUtil.e("HpGameCertification:开始实名认证")
        if (activity.isDestroyed) {
            return
        }

        val findFragmentByTag = activity.fragmentManager.findFragmentByTag("HpCertificationResultFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }

        HupuActivityLifecycleCallbacks.getScope(activity)?.launch {
            try {
                flow {
                    try {
                        val puid = HpLoginManager.getUserInfo()?.puid
                        val result = service.checkCertification(puid)
                        emit(result)
                    }catch (e: Exception) {
                        e.printStackTrace()
                        emit(null)
                    }
                }.flowOn(Dispatchers.IO).collectLatest {
                    processResult(activity,it,listener)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun processResult(activity: Activity,result: CertificationResult?,listener: HpCertificationListener) {
        if (result.isSuccess()) {
            if (result?.data?.status?:-1 == 0){
                //认证成功
                HpLogUtil.e("HpGameCertification:实名认证成功")
                listener.success(result?.data!!)
                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 1
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION,hashMap)
            }else if (result?.data?.status?:-1 == 1) {
                //认证中
                HpLogUtil.e("HpGameCertification:实名认证处理中")
                showResultFragment(activity,CertificationType.PROCESSING,null)
                listener.fail(ErrorType.Certificationing.code, ErrorType.Certificationing.msg)

                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 2
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION,hashMap)
            }else {
                //认证失败或未认证
                HpLogUtil.e("HpGameCertification:实名认证失败")
                startCertification(activity,listener)
                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 0
                hashMap["error_msg"] = "检测实名验证不通过或未实名"
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION,hashMap)
            }
        }else {
            //认证失败或未认证
            HpLogUtil.e("HpGameCertification:实名认证失败")
            startCertification(activity,listener)
            val hashMap = HashMap<String, Any?>()
            hashMap["result"] = 0
            hashMap["error_msg"] = "检测实名接口返回异常"
            HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION,hashMap)
        }
    }

    private fun processPostResult(activity: Activity,result: CertificationResult?,listener: HpCertificationListener) {
        if (result.isSuccess()) {
            if (result?.data?.status?:-1 == 0){
                //认证成功
                HpLogUtil.e("HpGameCertification:跳转回调，认证成功")
                showResultFragment(activity,CertificationType.SUCCESS){
                    listener.success(result?.data!!)
                }
                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 1
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION_POST,hashMap)
            }else if (result?.data?.status?:-1 == 1) {
                //认证中
                HpLogUtil.e("HpGameCertification:跳转回调，认证中")
                showResultFragment(activity,CertificationType.PROCESSING,null)
                listener.fail(ErrorType.Certificationing.code, ErrorType.Certificationing.msg)

                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 2
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION_POST,hashMap)
            }else {
                //认证失败或未认证
                HpLogUtil.e("HpGameCertification:跳转回调，认证失败")
                showResultFragment(activity,CertificationType.FAIL) {
                    startCertification(activity,listener)
                }
                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 0
                hashMap["error_msg"] = "实名验证不通过或未实名"
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION_POST,hashMap)
            }
        }else {
            //认证失败或未认证
            HpLogUtil.e("HpGameCertification:跳转回调，认证失败")
            showResultFragment(activity,CertificationType.FAIL) {
                startCertification(activity,listener)
            }
            val hashMap = HashMap<String, Any?>()
            hashMap["result"] = 0
            hashMap["error_msg"] = "实名接口返回异常"
            HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION_POST,hashMap)
        }
    }


    private fun startCertification(activity: Activity,listener: HpCertificationListener) {
        HpLogUtil.e("HpGameCertification:跳转实名认证页面")
        val intent = Intent(activity, HpCertificationActivity::class.java)
        ActResultRequest(activity).startForResult(intent) { resultCode, data ->
            if (resultCode == HpCertificationActivity.LOGOUT_OUT) {
                return@startForResult
            }
            val result =
                data?.getSerializableExtra(HpCertificationActivity.CERTIFICATION_RESULT_KEY)
            processPostResult(activity, result as CertificationResult?,listener)
        }
    }


    private fun showResultFragment(activity: Activity,result: CertificationType,listener:  (()->Unit)?) {
       if (activity.isDestroyed) {
           return
       }

        val findFragmentByTag = activity.fragmentManager.findFragmentByTag("HpCertificationResultFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }
        if (activity.isDestroyed) {
            return
        }

        val bundle = Bundle()
        bundle.putInt(HpCertificationResultFragment.KEY_RESULT_TYPE,result.code)
        val fragment = HpCertificationResultFragment()
        fragment.registerResultClickListener(listener)
        fragment.arguments = bundle
        fragment.isCancelable = false
        fragment.show(activity.fragmentManager,"HpCertificationResultFragment")
    }

    class Builder {
        fun build(): HpGameCertification {
            return HpGameCertification()
        }
    }

    interface HpCertificationListener {
        fun success(response: CertificationResult.CertficationResponse)
        fun fail(code: Int,msg: String?)
    }
}