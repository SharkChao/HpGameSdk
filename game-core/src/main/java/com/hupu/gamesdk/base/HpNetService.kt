package com.hupu.gamesdk.base

import com.hupu.gamesdk.core.HpGame
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.login.HpLoginManager
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


internal object HpNetService {
    private var client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request: Request = chain.request()
                .newBuilder()
                .addHeader("access-token", HpLoginManager.getUserInfo()?.accessToken?:"")
                .addHeader("appid", HpGameAppInfo.appId?:"")
                .addHeader("sdk-version", HpGame.sdkVersion.toString())
                .build()
            chain.proceed(request)
        }.addInterceptor(SignInterceptor()).build()
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://mobilegame.hupu.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}