package com.hupu.gamesdk.base

import android.util.Log
import com.hupu.gamesdk.core.HpGame

class HpLogUtil {
    companion object {
        private const val DEFAULT_TAG = "HpGameSdk_debug"
        fun d(message: String?) {
            if (HpGame.debug) {
                Log.d(DEFAULT_TAG, message + "")
            }
        }

        fun e(e: String?) {
            if (HpGame.debug) {
                Log.e(DEFAULT_TAG, e + "")
            }
        }
    }
}