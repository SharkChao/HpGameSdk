package com.hupu.gamesdk.secret

import com.hupu.gamesdk.base.HpGameSp

object HpSecretManager {
    private const val USER_SECRET_AGREE_KEY = "user_secret_agree_key"
    private var agree: Boolean = false
    fun saveSecretAgree(agree: Boolean) {
        this.agree = agree
        HpGameSp.sp?.edit()?.putBoolean(USER_SECRET_AGREE_KEY, agree)?.apply()
    }

    fun getSecretAgree(): Boolean {
        if (!agree) {
            agree = HpGameSp.sp?.getBoolean(USER_SECRET_AGREE_KEY,false)?:false
        }
        return agree
    }
}