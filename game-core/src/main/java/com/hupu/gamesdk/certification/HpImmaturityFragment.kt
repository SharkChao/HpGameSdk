package com.hupu.gamesdk.certification

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hupu.gamesdk.base.CommonUtil.Companion.dp2px
import com.hupu.gamesdk.databinding.HpGameCoreImmaturityLayoutBinding

/**
 * 未成年防沉迷
 */
internal class HpImmaturityFragment: DialogFragment() {

    private var _binding: HpGameCoreImmaturityLayoutBinding? = null
    private val binding get() = _binding!!
    private var listener: (()->Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HpGameCoreImmaturityLayoutBinding.inflate(inflater, container, false)
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
        binding.tvSure.setOnClickListener {
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
                width = requireContext().dp2px(430f).toInt()
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialog?.window?.attributes = this
            }
        }
    }


    fun registerListener(listener: (()->Unit)?) {
        this.listener = listener
    }
}