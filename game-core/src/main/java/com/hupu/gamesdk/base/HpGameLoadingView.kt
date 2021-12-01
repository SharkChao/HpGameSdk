package com.hupu.gamesdk.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import com.hupu.gamesdk.R

internal class HpGameLoadingView:FrameLayout {
    private var imageView: ImageView? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init(context)
    }

    private fun init(context: Context) {
        imageView = ImageView(context)
        imageView?.setImageResource(R.mipmap.hp_game_core_login_loading)
        addView(imageView)
    }

    fun startLoading() {
        visibility = View.VISIBLE
        val animation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 600
            repeatMode = Animation.RESTART
            interpolator = LinearInterpolator()
            repeatCount = -1
        }

        imageView?.animation = animation
        animation.start()
    }

    fun stopLoading() {
        imageView?.clearAnimation()
        visibility = View.GONE
    }
}