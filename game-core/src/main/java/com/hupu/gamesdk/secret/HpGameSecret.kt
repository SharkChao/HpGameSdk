package com.hupu.gamesdk.secret

import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity


class HpGameSecret {
    fun start(activity: FragmentActivity, listener: HpSecretListener) {
        if (HpSecretManager.getSecretAgree()) {
            listener.agree()
            return
        }
        val findFragmentByTag = activity.supportFragmentManager.findFragmentByTag("HpSecretFragment")
        if (findFragmentByTag?.isAdded == true && findFragmentByTag is DialogFragment) {
            findFragmentByTag.dismiss()
        }

        if (activity.isDestroyed) {
            return
        }

        val hpSecretFragment = HpSecretFragment()
        hpSecretFragment.isCancelable = false
        hpSecretFragment.registerSecretListener(listener)
        hpSecretFragment.show(activity.supportFragmentManager,"HpSecretFragment")
    }

    class Builder {
        fun build(): HpGameSecret {
            return HpGameSecret()
        }
    }

    interface HpSecretListener {
        fun agree()
        fun reject()
    }
}