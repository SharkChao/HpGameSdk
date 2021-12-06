package com.hupu.gamesdk.certification

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.KeyEvent
import com.gyf.immersionbar.ImmersionBar
import com.hupu.gamesdk.R
import com.hupu.gamesdk.base.CommonUtil
import com.hupu.gamesdk.base.HpLoadingFragment
import com.hupu.gamesdk.core.HpGame
import com.hupu.gamesdk.databinding.HpGameCoreCertificationLayoutBinding
import com.hupu.gamesdk.login.HpLoginManager
import java.util.*
import android.arch.lifecycle.ViewModelProviders
import android.text.Editable
import android.text.TextWatcher


/**
 * 实名认证
 */
class HpCertificationActivity: AppCompatActivity() {

    companion object {
        const val CERTIFICATION_RESULT_KEY = "certification_result_key"
    }


    private var viewModel: HpCertificationViewModel? = null
    private var _binding: HpGameCoreCertificationLayoutBinding? = null
    private val binding get() = _binding!!
    private var hpLoadingFragment: HpLoadingFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColorInt(Color.parseColor("#FFFFFF"))
            .statusBarDarkFont(true)
            .init()


        _binding = HpGameCoreCertificationLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProviders.of(this).get(HpCertificationViewModel::class.java)
        initEvent()
    }


    private fun initEvent() {
        changeBtnState()


        binding.tvLogout.setOnClickListener {
            HpGame.logout()
        }

        binding.tvName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.tvName.text.toString().isEmpty()){
                    binding.llName.error = "请输入用户名"
                }else {
                    binding.llName.error = ""
                }
                changeBtnState()
            }
        })



        binding.tvCard.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.tvCard.text.toString().isEmpty()){
                    binding.llCard.error = "请输入身份证号"
                }else if (!CommonUtil.checkIsIDCard(binding.tvCard.text.toString())) {
                    binding.llCard.error = "请输入合法身份证号"
                }else {
                    binding.llCard.error = ""
                }
                changeBtnState()
            }
        })

        binding.rlBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }



        binding.tvPost.setOnClickListener {
            if(!checkParamsVaild()) {
                return@setOnClickListener
            }

            hpLoadingFragment = HpLoadingFragment()
            hpLoadingFragment?.isCancelable = false
            hpLoadingFragment?.show(supportFragmentManager,"")
            val userInfo = HpLoginManager.getUserInfo()
            viewModel?.postCertification(userInfo?.puid,binding.tvName.text.toString(),binding.tvCard.text.toString())?.observe(this, {
                hpLoadingFragment?.dismiss()
                val intent = Intent()
                intent.putExtra(CERTIFICATION_RESULT_KEY,it)
                setResult(Activity.RESULT_OK,intent)
                finish()
            })
        }
    }

    private fun checkParamsVaild(): Boolean {
        if (TextUtils.isEmpty(binding.tvName.text.toString()) || TextUtils.isEmpty(binding.tvCard.text.toString()) || !CommonUtil.checkIsIDCard(binding.tvCard.text.toString())) {
            return false
        }

        return true
    }

    private fun changeBtnState() {
        if (!checkParamsVaild()) {
            binding.tvPost.setBackgroundResource(R.drawable.hp_game_core_dialog_btn_unselect_bg)
            binding.tvPost.setTextColor(Color.parseColor("#89909F"))
        }else {
            binding.tvPost.setBackgroundResource(R.drawable.hp_game_core_dialog_btn_bg)
            binding.tvPost.setTextColor(Color.parseColor("#FFFFFF"))
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