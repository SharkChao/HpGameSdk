package com.hupu.gamesdk.config

import com.google.gson.Gson
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.base.HpNetService
import com.hupu.gamesdk.base.isSuccess
import com.hupu.gamesdk.base.HpGameSp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal object HpConfigManager {
    private val mainScope = MainScope()
    private var hpConfigResponse: HpConfigResult.HpConfigResponse? = null
    private val service = HpNetService.getRetrofit().create(HpConfigService::class.java)
    private val gson = Gson()


    internal fun getConfig(): HpConfigResult.HpConfigResponse? {
        if (hpConfigResponse == null) {
            val json = HpGameSp.sp?.getString(HpGameConstant.SP_NAME_CONFIG, null)
            hpConfigResponse = Gson().fromJson(json, HpConfigResult.HpConfigResponse::class.java)?: createDefaultConfig()
        }
        return hpConfigResponse
    }

    private fun createDefaultConfig(): HpConfigResult.HpConfigResponse {
        val hpConfigResponse = HpConfigResult.HpConfigResponse()
        val payList = ArrayList<HpPayItem>()

        val hpPayItemAlipay = HpPayItem()
        hpPayItemAlipay.code = 1
        hpPayItemAlipay.name = "支付宝"
        hpPayItemAlipay.img = "https://i4.hoopchina.com.cn/bc/af/d0/bcafd0220febb13aa44583f2efc40ffb002.png"
        payList.add(hpPayItemAlipay)


//        val hpPayItemWechat = HpPayItem()
//        hpPayItemWechat.code = 2
//        hpPayItemWechat.name = "微信"
//        hpPayItemWechat.img = "https://i3.hoopchina.com.cn/fa/c2/6a/fac26aa4ab7aa3aa802d42d01ea7b2ac002.png"
//        payList.add(hpPayItemWechat)


        hpConfigResponse.payConfig = payList
        return hpConfigResponse
    }

    internal fun initConfig() {
        mainScope.launch(Dispatchers.IO) {
            try {
                val config = service.getConfig()
                if (config.isSuccess()) {
                    hpConfigResponse = config?.data
                    HpGameSp.sp?.edit()?.putString(HpGameConstant.SP_NAME_CONFIG,gson.toJson(
                        hpConfigResponse))?.apply()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}