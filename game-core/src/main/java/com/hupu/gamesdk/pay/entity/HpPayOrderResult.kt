package com.hupu.gamesdk.pay.entity

import androidx.annotation.Keep
import com.hupu.gamesdk.base.BaseEntity

@Keep
internal class HpPayOrderResult: BaseEntity<HpPayOrderResult.PayResponse>() {

    @Keep
    internal class PayResponse {
        var payUrl: String? = null
    }
}