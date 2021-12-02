package com.hupu.gamesdk.certification

import android.text.TextUtils
import android.util.Base64
import com.hupu.gamesdk.base.HpNetService
import kotlinx.coroutines.flow.flow
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

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
            hashMap["name_cipher"] = encrypt(name)
            hashMap["identification"] = encrypt(card)
            val result = service.postCertification(hashMap)
            emit(result)
        }catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }

    private val key = "163381885615040118961695"
    private val initVector = "1048908077297390"

    /**
     * 使用AES/CBC/PKCS5PADDING进行加密,使用时需要注意判空
     * @param value
     * @return
     */
    private fun encrypt(value: String?): String? {

        if (TextUtils.isEmpty(value)) {
            return null
        }

        try {
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted: ByteArray = cipher.doFinal(value?.toByteArray())
            return Base64.encodeToString(encrypted, Base64.URL_SAFE).trim()
        } catch (e: java.lang.Exception) {
        }
        return null
    }
}