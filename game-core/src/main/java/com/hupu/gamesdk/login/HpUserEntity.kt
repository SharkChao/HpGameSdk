package com.hupu.gamesdk.login

import android.support.annotation.Keep


@Keep
internal class HpUserEntity {
    var accessToken: String? = null
    var puid: String? = null
    var head: String? = null
    var nickName: String? = null
}