package com.hupu.gamesdk.base

import android.content.Context
import com.hupu.gamesdk.HpGame
import com.hupu.gamesdk.base.HpGameConstant

internal object HpGameSp {
    val sp = HpGame.context?.getSharedPreferences(HpGameConstant.SP_NAME, Context.MODE_PRIVATE)

}