package com.hupu.gamesdk.report

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

internal interface HpReportService {
    @Headers("Content-type:application/json;charset=UTF-8")
    @POST("/external/antiAddiction/report")
    suspend fun postHeartBeat(@Body params: @JvmSuppressWildcards Map<String, Any?>): Response<Unit>?

    @POST("/external/antiAddiction/heartbeat")
    suspend fun postReport(@Body json: RequestBody): Response<Unit>?
}