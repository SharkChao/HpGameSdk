package com.hupu.gamesdk.certification

import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hupu.gamesdk.base.HPDeviceInfo
import com.hupu.gamesdk.base.ReflectUtil

class HpCertificationResultFragment: DialogFragment() {

    companion object {
        const val KEY_RESULT_TYPE = "key_result_type"
    }
    private var listener:  (()->Unit)? = null
    private lateinit var tvTitle: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvSure: TextView
    private lateinit var ivStatus: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(
            ReflectUtil.getLayoutId(activity, "hp_game_core_certification_result_layout"),
            container,
            false
        )

        tvTitle = v.findViewById(ReflectUtil.getViewId(activity,"tv_title"))
        tvDesc = v.findViewById(ReflectUtil.getViewId(activity,"tv_desc"))
        tvSure = v.findViewById(ReflectUtil.getViewId(activity,"tv_sure"))
        ivStatus = v.findViewById(ReflectUtil.getViewId(activity,"iv_status"))
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val result = arguments?.getInt(KEY_RESULT_TYPE)
        if (result == CertificationType.SUCCESS.code) {
            ivStatus.setImageResource(ReflectUtil.getMipmapId(activity,"hp_game_core_certification_result_success"))
            tvTitle.text = "认证成功"
            tvDesc.text = "恭喜您完成了认证"
            tvSure.text = "确 认"
            tvSure.visibility = View.VISIBLE
        }else if (result == CertificationType.FAIL.code) {
            ivStatus.setImageResource(ReflectUtil.getMipmapId(activity,"hp_game_core_certification_result_fail"))
            tvTitle.text = "认证失败"
            tvDesc.text = "请再次提交正确的信息验证"
            tvSure.text = "重 试"
            tvSure.visibility = View.VISIBLE
        }else {
            ivStatus.setImageResource(ReflectUtil.getMipmapId(activity,"hp_game_core_certification_result_processing"))
            tvTitle.text = "认证中"
            tvDesc.text = "您的实名正在校验中,\n 预计最迟48小时结束，请耐心等待"
            tvSure.visibility = View.GONE
            tvDesc.setPadding(0,0,0,HPDeviceInfo.DipToPixels(activity,25))

            val layoutParams = tvDesc.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin =HPDeviceInfo.DipToPixels(activity,30)
            tvDesc.layoutParams = layoutParams
        }



        tvSure.setOnClickListener {
            listener?.invoke()
            dismiss()
        }
    }

    fun registerResultClickListener(listener: (()->Unit)?) {
        this.listener = listener
    }
}