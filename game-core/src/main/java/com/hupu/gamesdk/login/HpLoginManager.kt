package com.hupu.gamesdk.login

import com.google.gson.Gson
import com.hupu.gamesdk.base.HpGameSp

internal object HpLoginManager {
    private const val USER_INFO_KEY = "user_info_key"
    private const val USER_HAS_LOGINED_BEFORE_KEY = "user_has_logined_before_key"
    private val gson = Gson()
    private var userEntity: HpUserEntity? = null


    fun saveUserInfo(hpUserEntity: HpUserEntity) {
        userEntity = hpUserEntity
        HpGameSp.sp?.edit()?.putString(USER_INFO_KEY,gson.toJson(hpUserEntity))?.apply()
    }

    fun getUserInfo(): HpUserEntity? {
        if (userEntity == null) {
            val json = HpGameSp.sp?.getString(USER_INFO_KEY, "")
            userEntity = Gson().fromJson(json, HpUserEntity::class.java)
        }
        return userEntity
    }

    /**
     * 之前有没有调用过登陆
     */
    fun hasLoginedBefore(): Boolean {
        return HpGameSp.sp?.getBoolean(USER_HAS_LOGINED_BEFORE_KEY,false)?:false
    }

    fun clearUserInfo() {
        userEntity = null
        HpGameSp.sp?.edit()?.putString(USER_INFO_KEY,"")?.apply()
        HpGameSp.sp?.edit()?.putBoolean(USER_HAS_LOGINED_BEFORE_KEY,true)?.apply()
    }
}