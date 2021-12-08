package com.hupu.gamesdk.secret

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hupu.gamesdk.base.CommonUtil
import com.hupu.gamesdk.base.CommonUtil.Companion.dp2px
import com.hupu.gamesdk.base.ReflectUtil
import com.hupu.gamesdk.databinding.HpGameCoreSecretDialogBinding

internal class HpSecretFragment: DialogFragment() {
    private var listener: HpGameSecret.HpSecretListener? = null
    private lateinit var tvContent: TextView
    private lateinit var tvAgree: TextView
    private lateinit var tvReject: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(
            ReflectUtil.getLayoutId(requireContext(), "hp_game_core_secret_dialog"),
            container,
            false
        )
        tvContent = v.findViewById(ReflectUtil.getViewId(requireContext(),"tv_content"))
        tvAgree = v.findViewById(ReflectUtil.getViewId(requireContext(),"tv_agree"))
        tvReject = v.findViewById(ReflectUtil.getViewId(requireContext(),"tv_reject"))

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
        tvContent.movementMethod = LinkMovementMethod.getInstance()
        tvAgree.setOnClickListener {
            HpSecretManager.saveSecretAgree(true)
            listener?.agree()
            dismiss()
        }

        tvReject.setOnClickListener {
            HpSecretManager.saveSecretAgree(false)
            listener?.reject()
        }
    }

    fun registerSecretListener(listener: HpGameSecret.HpSecretListener?) {
        this.listener = listener
    }



    private fun configOrientation() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable())
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            dialog?.window?.attributes?.apply {
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = requireContext().dp2px(400f).toInt()
                dialog?.window?.attributes = this
            }
        }else {
            dialog?.window?.attributes?.apply {
                width = requireContext().dp2px(430f).toInt()
                height = CommonUtil.getScreenHeight(requireContext()) - requireContext().dp2px(50f).toInt()
                dialog?.window?.attributes = this
            }
        }
    }
}