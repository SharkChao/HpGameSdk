package com.hupu.gamesdk.config

import retrofit2.http.*

internal interface HpConfigService {
    @GET("/external/config/get")
    suspend fun getConfig(): HpConfigResult?
}