package com.hupu.gamesdk.core

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.base.HpLogUtil
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.pay.HpPayFragment
import com.hupu.gamesdk.pay.entity.HpPayEntity
import com.hupu.gamesdk.report.HpReportManager

class HpGamePay private constructor(private val hpPayEntity: HpPayEntity){

    fun start(activity: Activity, tempListener: HpPayListener) {

        val listener = object : HpPayListener{
            override fun success() {
                tempListener.success()
                HpLogUtil.e("HpGamePay:支付成功！")
            }
            override fun fail(code: Int, msg: String?) {
                tempListener.fail(code, msg)
                HpLogUtil.e("HpGamePay:支付失败！code:${code},msg:${msg}")
            }

            override fun cancel() {
                tempListener.cancel()
                HpLogUtil.e("HpGamePay:支付取消！")
            }
        }


        HpLogUtil.e("HpGamePay:开始支付")
        val hashMap = HashMap<String, Any?>()
        HpReportManager.report(HpGameConstant.REPORT_PAY_CLICK,hashMap)

        if (!HpGameAppInfo.legal) {
            HpLogUtil.e("HpGamePay:app不合法")
            listener.fail(ErrorType.AppNotLegal.code,ErrorType.AppNotLegal.msg)
            return
        }

        if (activity.isDestroyed) {
            return
        }
        val findFragmentByTag = activity.fragmentManager.findFragmentByTag("HpPayFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }

        val hpPayFragment = HpPayFragment()
        hpPayFragment.registerPayListener(listener)
        hpPayFragment.isCancelable = false
        val bundle = Bundle()
        bundle.putSerializable(HpPayFragment.HP_PAY_INFO_KEY,hpPayEntity)
        hpPayFragment.arguments = bundle
        hpPayFragment.show(activity.fragmentManager,"HpPayFragment")
    }


    class Builder {
        //商品名称
        private var mProductName: String? = null
        //订单描述信息
        private var mProductDesc: String? = null
        //游戏方订单号
        private var mGameTradeNo: String? = null
        //本次交易金额，单位：分（注意，total_fee的值必须为整数，并且在1~100000之间)
        private var mTotalFee: Int = 0
        //签名，服务端需要校验
        private var mSign: String? = null
        //游戏角色id
        private var mRoleId: String? = null
        //区服id
        private var mServerId: String? = null
        //puid
        private var mPuid: String? = null
        //appid
        private var mAppId: String? = null

        fun setProductName(name: String?) = apply {
            this.mProductName = name
        }

        fun setProductDesc(desc: String?) = apply {
            this.mProductDesc = desc
        }

        fun setGameTradeNo(gameTradeNo: String?) = apply {
            this.mGameTradeNo = gameTradeNo
        }

        fun setTotalFee(totalFee: Int) = apply {
            this.mTotalFee = totalFee
        }

        fun setSign(sign: String?) = apply {
            this.mSign = sign
        }

        fun setAppId(appid: String?) = apply {
            this.mAppId = appid
        }

        fun setPuid(puid: String?) = apply {
            this.mPuid = puid
        }

        fun setRoleId(roleId: String?) = apply {
            this.mRoleId = roleId
        }

        fun setServerId(serverId: String?) = apply {
            this.mServerId = serverId
        }



        fun build(): HpGamePay {
            val hpPayEntity = HpPayEntity()
            hpPayEntity.productName = mProductName
            hpPayEntity.productDesc = mProductDesc
            hpPayEntity.gameTradeNo = mGameTradeNo
            hpPayEntity.totalFee = mTotalFee
            hpPayEntity.sign = mSign
            hpPayEntity.roleId = mRoleId
            hpPayEntity.serverId = mServerId
            hpPayEntity.appid = mAppId
            hpPayEntity.puid = mPuid
            return HpGamePay(hpPayEntity)
        }
    }

    interface HpPayListener {
        fun success()
        fun fail(code: Int,msg: String?)
        fun cancel()
    }
}