package com.hupu.gamesdk.certification

import android.app.DialogFragment
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hupu.gamesdk.base.CommonUtil.Companion.dp2px
import com.hupu.gamesdk.base.ReflectUtil

/**
 * 未成年防沉迷
 */
internal class HpImmaturityFragment: DialogFragment() {

    private var listener: (()->Unit)? = null
    private lateinit var tvSure: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(
            ReflectUtil.getLayoutId(activity, "hp_game_core_immaturity_layout"),
            container,
            false
        )

        tvSure = v.findViewById(ReflectUtil.getViewId(activity,"tv_sure"))
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    override fun onStart() {
        super.onStart()
        configOrientation()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configOrientation()
    }

    private fun initView() {
        tvSure.setOnClickListener {
            listener?.invoke()
        }
    }


    private fun configOrientation() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable())
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            dialog?.window?.attributes?.apply {
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialog?.window?.attributes = this
            }
        }else {
            dialog?.window?.attributes?.apply {
                width = activity.dp2px(430f).toInt()
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialog?.window?.attributes = this
            }
        }
    }


    fun registerListener(listener: (()->Unit)?) {
        this.listener = listener
    }
}