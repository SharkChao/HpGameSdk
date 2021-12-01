package com.hupu.gamesdk.certification

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.gyf.immersionbar.ImmersionBar
import com.hupu.gamesdk.base.CommonUtil
import com.hupu.gamesdk.base.HpLoadingFragment
import com.hupu.gamesdk.databinding.HpGameCoreCertificationLayoutBinding
import com.hupu.gamesdk.login.HpLoginManager

/**
 * 实名认证
 */
class HpCertificationActivity: AppCompatActivity() {

    companion object {
        const val CERTIFICATION_RESULT_KEY = "certification_result_key"
    }


    private val viewModel: HpCertificationViewModel by viewModels()
    private var _binding: HpGameCoreCertificationLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColorInt(Color.parseColor("#FFFFFF"))
            .statusBarDarkFont(true)
            .init()


        _binding = HpGameCoreCertificationLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEvent()
    }


    private fun initEvent() {

        binding.tvPost.setOnClickListener {
            if (binding.tvName.text.toString().isEmpty()){
                binding.llName.error = "请输入用户名"
                return@setOnClickListener
            }else {
                binding.llName.error = ""
            }

            if (binding.tvCard.text.toString().isEmpty()){
                binding.llCard.error = "请输入身份证号"
                return@setOnClickListener
            }

            if (!CommonUtil.checkIsIDCard(binding.tvCard.text.toString())) {
                binding.llCard.error = "请输入合法身份证号"
                return@setOnClickListener
            }
            binding.llCard.error = ""


            val hpLoadingFragment = HpLoadingFragment()
            hpLoadingFragment.isCancelable = false
            hpLoadingFragment.show(supportFragmentManager,"")
            val userInfo = HpLoginManager.getUserInfo()
            viewModel.postCertification(userInfo?.puid,binding.tvName.text.toString(),binding.tvCard.text.toString()).observe(this,
                Observer {
                    hpLoadingFragment.dismiss()
                    val intent = Intent()
                    intent.putExtra(CERTIFICATION_RESULT_KEY,it)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                })
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}