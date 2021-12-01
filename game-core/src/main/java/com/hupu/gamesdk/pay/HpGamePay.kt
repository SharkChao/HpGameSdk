package com.hupu.gamesdk.pay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.pay.entity.HpPayEntity
import com.hupu.gamesdk.report.HpReportManager

class HpGamePay private constructor(private val hpPayEntity: HpPayEntity){

    fun start(activity: AppCompatActivity,listener: HpPayListener) {

        val hashMap = HashMap<String, Any?>()
        HpReportManager.report(HpGameConstant.REPORT_PAY_CLICK,hashMap)

        if (!HpGameAppInfo.legal) {
            listener.fail(ErrorType.AppNotLegal.code,ErrorType.AppNotLegal.msg)
            return
        }


        val findFragmentByTag = activity.supportFragmentManager.findFragmentByTag("HpPayFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }

        if (activity.isDestroyed) {
            return
        }

        val hpPayFragment = HpPayFragment()
        hpPayFragment.registerPayListener(listener)
        hpPayFragment.isCancelable = false
        val bundle = Bundle()
        bundle.putSerializable(HpPayFragment.HP_PAY_INFO_KEY,hpPayEntity)
        hpPayFragment.arguments = bundle
        hpPayFragment.show(activity.supportFragmentManager,"HpPayFragment")
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

        fun setProductName(name: String) = apply {
            this.mProductName = name
        }

        fun setProductDesc(desc: String) = apply {
            this.mProductDesc = desc
        }

        fun setGameTradeNo(gameTradeNo: String) = apply {
            this.mGameTradeNo = gameTradeNo
        }

        fun setTotalFee(totalFee: Int) = apply {
            this.mTotalFee = totalFee
        }

        fun setSign(sign: String) = apply {
            this.mSign = sign
        }

        fun setRoleId(roleId: String?) = apply {
            this.mRoleId = roleId
        }

        fun setServerId(serverId: String) = apply {
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
            return HpGamePay(hpPayEntity)
        }
    }

    interface HpPayListener {
        fun success()
        fun fail(code: Int,msg: String?)
    }
}