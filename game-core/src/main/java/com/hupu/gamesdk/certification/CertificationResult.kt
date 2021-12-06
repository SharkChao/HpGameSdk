package com.hupu.gamesdk.certification

import android.support.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.hupu.gamesdk.base.BaseEntity
import java.io.Serializable

@Keep
internal class CertificationResult: BaseEntity<CertificationResult.CertficationResponse>() {

    @Keep
    class CertficationResponse: Serializable {
        @SerializedName("anti_status")
        var status: Int = -1  // 0成功 1认证中 2认证失败
        @SerializedName("is_adult")
        var adult: Boolean = false//是否成年
    }
}