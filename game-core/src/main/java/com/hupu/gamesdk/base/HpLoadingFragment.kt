package com.hupu.gamesdk.base

import android.app.DialogFragment
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView

class HpLoadingFragment: DialogFragment() {

    private lateinit var ivLoading: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(
            ReflectUtil.getLayoutId(activity, "hp_game_core_loading_layout"),
            container,
            false
        )

        ivLoading = v.findViewById(ReflectUtil.getViewId(activity,"iv_loading"))
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoading()
    }

    override fun dismiss() {
        super.dismiss()
        ivLoading.clearAnimation()
    }

    private fun startLoading() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable())
        val animation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 600
            repeatMode = Animation.RESTART
            interpolator = LinearInterpolator()
            repeatCount = -1
        }

        ivLoading.animation = animation
        animation.start()
    }
}