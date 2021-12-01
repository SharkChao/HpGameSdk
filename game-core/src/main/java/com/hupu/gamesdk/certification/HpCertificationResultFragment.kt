package com.hupu.gamesdk.certification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hupu.gamesdk.R
import com.hupu.gamesdk.databinding.HpGameCoreCertificationResultLayoutBinding

class HpCertificationResultFragment: DialogFragment() {

    companion object {
        const val KEY_RESULT_TYPE = "key_result_type"
    }


    private var _binding: HpGameCoreCertificationResultLayoutBinding? = null
    private val binding get() = _binding!!
    private var listener:  (()->Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HpGameCoreCertificationResultLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val result = arguments?.getInt(KEY_RESULT_TYPE)
        if (result == CertificationType.SUCCESS.code) {
            binding.ivStatus.setImageResource(R.mipmap.hp_game_core_certification_result_success)
            binding.tvTitle.text = "认证成功"
            binding.tvDesc.text = "恭喜您完成了认证"
            binding.tvSure.text = "确 认"
            binding.tvSure.visibility = View.VISIBLE
        }else if (result == CertificationType.FAIL.code) {
            binding.ivStatus.setImageResource(R.mipmap.hp_game_core_certification_result_fail)
            binding.tvTitle.text = "认证失败"
            binding.tvDesc.text = "请再次提交正确的信息验证"
            binding.tvSure.text = "重 试"
            binding.tvSure.visibility = View.VISIBLE
        }else {
            binding.ivStatus.setImageResource(R.mipmap.hp_game_core_certification_result_processing)
            binding.tvTitle.text = "认证中"
            binding.tvDesc.text = "您的实名正在校验中,\n 预计最迟48小时结束，请耐心等待"
            binding.tvSure.visibility = View.GONE
        }



        binding.tvSure.setOnClickListener {
            listener?.invoke()
            dismiss()
        }
    }

    fun registerResultClickListener(listener: (()->Unit)?) {
        this.listener = listener
    }
}