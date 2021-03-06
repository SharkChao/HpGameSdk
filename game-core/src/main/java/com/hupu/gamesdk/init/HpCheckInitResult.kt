package com.hupu.gamesdk.init

import androidx.annotation.Keep
import com.hupu.gamesdk.base.BaseEntity

@Keep
internal class HpCheckInitResult: BaseEntity<HpCheckInitResult.HpCheckInitResponse>() {

    @Keep
    internal class HpCheckInitResponse {
        var icon: String? = null
        var name: String? = null
    }
}