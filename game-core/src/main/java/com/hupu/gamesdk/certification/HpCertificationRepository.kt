package com.hupu.gamesdk.certification

import com.hupu.gamesdk.base.HPMd5
import com.hupu.gamesdk.base.HpNetService
import kotlinx.coroutines.flow.flow

internal class HpCertificationRepository {
    private val service = HpNetService.getRetrofit().create(HpCertificationService::class.java)

    fun checkCertification(puid: String?) = flow {
        try {
            val result = service.checkCertification(puid)
            emit(result)
        }catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }


    fun postCertification(puid: String?,name: String?,card: String?) = flow {
        try {
            val hashMap = HashMap<String, Any?>()
            hashMap["puid"] = puid
            hashMap["name_cipher"] = HPMd5().md5(name)
            hashMap["identification"] = HPMd5().md5(card)
            val result = service.postCertification(hashMap)
            emit(result)
        }catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }
}