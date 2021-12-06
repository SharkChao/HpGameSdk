package com.hupu.gamesdk.login

import com.hupu.gamesdk.base.HpNetService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class HpLoginRepository {
    private val service = HpNetService.getRetrofit().create(HpLoginService::class.java)

    fun checkAccessToken() = flow {
        try {
            val result = service.checkAccessToken()
            emit(result)
        }catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }.flowOn(Dispatchers.IO)
}