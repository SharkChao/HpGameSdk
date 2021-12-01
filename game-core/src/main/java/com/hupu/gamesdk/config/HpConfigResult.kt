package com.hupu.gamesdk.config

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.hupu.gamesdk.base.BaseEntity

@Keep
internal class HpConfigResult: BaseEntity<HpConfigResult.HpConfigResponse>() {
    @Keep
    class HpConfigResponse {
        @SerializedName("pay")
        var payConfig: List<HpPayItem>? = null
    }
}

@Keep
internal class HpPayItem {
    var code: Int = 0
    var desc: String? = null
    var img: String? = null
    var name: String? = null

    //-----本地属性-----------//
    var select: Boolean = false
    //-----本地属性end--------//
}