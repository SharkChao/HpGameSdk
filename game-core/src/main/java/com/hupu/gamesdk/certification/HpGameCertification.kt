package com.hupu.gamesdk.certification

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.base.activitycallback.ActResultRequest
import com.hupu.gamesdk.base.isSuccess
import com.hupu.gamesdk.login.HpLoginManager
import com.hupu.gamesdk.report.HpReportManager

internal class HpGameCertification {
    fun start(activity: FragmentActivity, listener: HpCertificationListener) {
        val findFragmentByTag = activity.supportFragmentManager.findFragmentByTag("HpCertificationResultFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }

        val viewModel = ViewModelProviders.of(activity)[HpCertificationViewModel::class.java]
        val puid = HpLoginManager.getUserInfo()?.puid
        viewModel.checkCertification(puid).observe(activity, {
            processResult(activity,it,listener)
        })
    }

    private fun processResult(activity: FragmentActivity,result: CertificationResult?,listener: HpCertificationListener) {
        if (result.isSuccess()) {
            if (result?.data?.status?:-1 == 0){
                //认证成功
                listener.success(result?.data!!)
                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 1
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION,hashMap)
            }else if (result?.data?.status?:-1 == 1) {
                //认证中
                showResultFragment(activity,CertificationType.PROCESSING,null)
                listener.fail(ErrorType.Certificationing.code, ErrorType.Certificationing.msg)

                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 2
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION,hashMap)
            }else {
                //认证失败或未认证
                startCertification(activity,listener)
                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 0
                hashMap["error_msg"] = "检测实名验证不通过或未实名"
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION,hashMap)
            }
        }else {
            //认证失败或未认证
            startCertification(activity,listener)
            val hashMap = HashMap<String, Any?>()
            hashMap["result"] = 0
            hashMap["error_msg"] = "检测实名接口返回异常"
            HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION,hashMap)
        }
    }

    private fun processPostResult(activity: FragmentActivity,result: CertificationResult?,listener: HpCertificationListener) {
        if (result.isSuccess()) {
            if (result?.data?.status?:-1 == 0){
                //认证成功
                showResultFragment(activity,CertificationType.SUCCESS){
                    listener.success(result?.data!!)
                }
                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 1
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION_POST,hashMap)
            }else if (result?.data?.status?:-1 == 1) {
                //认证中
                showResultFragment(activity,CertificationType.PROCESSING,null)
                listener.fail(ErrorType.Certificationing.code, ErrorType.Certificationing.msg)

                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 2
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION_POST,hashMap)
            }else {
                //认证失败或未认证
                showResultFragment(activity,CertificationType.FAIL) {
                    Log.e("sharkchao","showResultFragment-click")
                    startCertification(activity,listener)
                }
                val hashMap = HashMap<String, Any?>()
                hashMap["result"] = 0
                hashMap["error_msg"] = "实名验证不通过或未实名"
                HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION_POST,hashMap)
            }
        }else {
            //认证失败或未认证
            showResultFragment(activity,CertificationType.FAIL) {
                startCertification(activity,listener)
            }
            val hashMap = HashMap<String, Any?>()
            hashMap["result"] = 0
            hashMap["error_msg"] = "实名接口返回异常"
            HpReportManager.report(HpGameConstant.REPORT_CERTIFICATION_POST,hashMap)
        }
    }


    private fun startCertification(activity: FragmentActivity,listener: HpCertificationListener) {
        val intent = Intent(activity, HpCertificationActivity::class.java)
        ActResultRequest(activity).startForResult(intent) { resultCode, data ->
            val result =
                data?.getSerializableExtra(HpCertificationActivity.CERTIFICATION_RESULT_KEY)
            processPostResult(activity, result as CertificationResult?,listener)
        }
    }


    private fun showResultFragment(activity: FragmentActivity,result: CertificationType,listener:  (()->Unit)?) {
       if (activity.isDestroyed) {
           return
       }

        val findFragmentByTag = activity.supportFragmentManager.findFragmentByTag("HpCertificationResultFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }

        val bundle = Bundle()
        bundle.putInt(HpCertificationResultFragment.KEY_RESULT_TYPE,result.code)
        val fragment = HpCertificationResultFragment()
        fragment.registerResultClickListener(listener)
        fragment.arguments = bundle
        fragment.isCancelable = false
        fragment.show(activity.supportFragmentManager,"HpCertificationResultFragment")
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