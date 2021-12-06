package com.hupu.gamesdk.pay

import com.hupu.gamesdk.base.HpNetService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class HpPayRepository {
    private val service = HpNetService.getRetrofit().create(HpPayService::class.java)

    fun startPay(hashMap: HashMap<String,Any?>) = flow {
        try {
            val result = service.startPay(hashMap)
            emit(result)
        }catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }.flowOn(Dispatchers.IO)
}