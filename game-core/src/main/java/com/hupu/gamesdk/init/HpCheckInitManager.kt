package com.hupu.gamesdk.init

import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpNetService
import com.hupu.gamesdk.base.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 用来校验appkey的合法性
 */
internal object HpCheckInitManager {
    private val mainScope = MainScope()
    private val service = HpNetService.getRetrofit().create(HpCheckInitService::class.java)

    internal fun checkAppLegal(listener: HpCheckInitListener) {
        mainScope.launch(Dispatchers.IO) {
            try {
                val result = service.checkApp()
                if (result.isSuccess()) {
                    listener.success(result?.data!!)
                }else {
                    listener.fail(result?.code?:0,result?.message)
                }
            }catch (e: Exception){
                e.printStackTrace()
                listener.fail(ErrorType.AppAuthError.code,ErrorType.AppAuthError.msg + "," +e.message)
            }
        }
    }


    interface HpCheckInitListener {
        fun success(hpCheckInitResponse: HpCheckInitResult.HpCheckInitResponse)
        fun fail(code: Int,msg: String?)
    }
}