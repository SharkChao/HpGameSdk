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
import com.hupu.gamesdk.databinding.HpGameCoreLoadingLayoutBinding

class HpLoadingFragment: DialogFragment() {

    private var _binding: HpGameCoreLoadingLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HpGameCoreLoadingLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoading()
    }

    override fun dismiss() {
        super.dismiss()
        binding.ivLoading.clearAnimation()
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

        binding.ivLoading.animation = animation
        animation.start()
    }
}