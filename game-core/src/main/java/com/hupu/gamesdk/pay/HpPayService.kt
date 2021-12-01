package com.hupu.gamesdk.pay

import com.hupu.gamesdk.pay.entity.HpPayOrderResult
import retrofit2.http.*

internal interface HpPayService {
    @Headers("Content-type:application/json;charset=UTF-8")
    @POST("/external/pay/apply")
    suspend fun startPay(@Body params: @JvmSuppressWildcards Map<String, Any?>): HpPayOrderResult?
}