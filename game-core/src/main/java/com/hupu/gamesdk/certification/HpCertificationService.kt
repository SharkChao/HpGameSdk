package com.hupu.gamesdk.certification

import retrofit2.http.*

internal interface HpCertificationService {
    @GET("/external/antiAddiction/query")
    suspend fun checkCertification(@Query("puid") puid: String?): CertificationResult?

    @Headers("Content-type:application/json;charset=UTF-8")
    @POST("/external/antiAddiction/certify")
    suspend fun postCertification(@Body params: @JvmSuppressWildcards Map<String, Any?>): CertificationResult?
}