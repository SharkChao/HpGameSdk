package com.hupu.gamesdk.init

import retrofit2.http.*

internal interface HpCheckInitService {
    @Headers("Content-type:application/json;charset=UTF-8")
    @POST("/external/auth/checkApp")
    suspend fun checkApp(): HpCheckInitResult?
}