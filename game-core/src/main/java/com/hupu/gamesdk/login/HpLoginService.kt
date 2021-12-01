package com.hupu.gamesdk.login

import retrofit2.http.Headers
import retrofit2.http.POST

internal interface HpLoginService {
    @Headers("Content-type:application/json;charset=UTF-8")
    @POST("/external/auth/checkAccessToken")
    suspend fun checkAccessToken(): AccessTokenResult?
}