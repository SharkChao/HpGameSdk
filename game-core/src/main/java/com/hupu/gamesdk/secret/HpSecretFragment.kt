package com.hupu.gamesdk.secret

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hupu.gamesdk.base.CommonUtil
import com.hupu.gamesdk.base.CommonUtil.Companion.dp2px
import com.hupu.gamesdk.databinding.HpGameCoreSecretDialogBinding

internal class HpSecretFragment: DialogFragment() {

    private var _binding: HpGameCoreSecretDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: HpGameSecret.HpSecretListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HpGameCoreSecretDialogBinding.inflate(inflater, container, false)
        return binding.root
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
        binding.tvContent.movementMethod = LinkMovementMethod.getInstance()
        binding.tvAgree.setOnClickListener {
            HpSecretManager.saveSecretAgree(true)
            listener?.agree()
            dismiss()
        }

        binding.tvReject.setOnClickListener {
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