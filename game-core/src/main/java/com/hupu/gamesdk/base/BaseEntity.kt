package com.hupu.gamesdk.base

import androidx.annotation.Keep
import com.hupu.gamesdk.core.HpGame
import java.io.Serializable

@Keep
internal open class BaseEntity<T>: Serializable {
    var code: Int = -1
    var message: String? = null
    var data: T? = null
}

internal fun <T> BaseEntity<T>?.isSuccess(): Boolean {
    if (this == null || this.code != 0 || this.data == null) {
        if (this?.code == 1003) {
            //accesstoken过期
            HpGame.logout()
        }
        return false
    }
    return true
}