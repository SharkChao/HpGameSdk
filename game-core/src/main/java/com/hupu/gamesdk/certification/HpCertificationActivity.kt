package com.hupu.gamesdk.certification

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.RelativeLayout
import android.widget.TextView
import com.gyf.immersionbar.ImmersionBar
import com.hupu.gamesdk.base.CommonUtil
import com.hupu.gamesdk.base.HpLoadingFragment
import com.hupu.gamesdk.base.ReflectUtil
import com.hupu.gamesdk.core.HpGame
import com.hupu.gamesdk.login.HpLoginManager
import java.util.*


/**
 * 实名认证
 */
class HpCertificationActivity: FragmentActivity() {

    companion object {
        const val CERTIFICATION_RESULT_KEY = "certification_result_key"
    }


    private var viewModel: HpCertificationViewModel? = null
    private var hpLoadingFragment: HpLoadingFragment? = null
    private lateinit var tvLogout: TextView
    private lateinit var tvName: TextInputEditText
    private lateinit var tvCard: TextInputEditText
    private lateinit var llName: TextInputLayout
    private lateinit var llCard: TextInputLayout
    private lateinit var rlBack: RelativeLayout
    private lateinit var tvPost: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColorInt(Color.parseColor("#FFFFFF"))
            .statusBarDarkFont(true)
            .init()

        setContentView(ReflectUtil.getLayoutId(this,"hp_game_core_certification_layout"))
        tvLogout = findViewById(ReflectUtil.getViewId(this,"tv_logout"))
        tvName = findViewById(ReflectUtil.getViewId(this,"tv_name"))
        llName = findViewById(ReflectUtil.getViewId(this,"ll_name"))
        llCard = findViewById(ReflectUtil.getViewId(this,"ll_card"))
        rlBack = findViewById(ReflectUtil.getViewId(this,"rl_back"))
        tvPost = findViewById(ReflectUtil.getViewId(this,"tv_post"))
        tvCard = findViewById(ReflectUtil.getViewId(this,"tv_card"))

        viewModel = ViewModelProviders.of(this).get(HpCertificationViewModel::class.java)
        initEvent()
    }


    private fun initEvent() {
        changeBtnState()


        tvLogout.setOnClickListener {
            HpGame.logout()
        }

        tvName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (tvName.text.toString().isEmpty()){
                    llName.error = "请输入用户名"
                }else {
                    llName.error = ""
                }
                changeBtnState()
            }
        })



        tvCard.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (tvCard.text.toString().isEmpty()){
                    llCard.error = "请输入身份证号"
                }else if (!CommonUtil.checkIsIDCard(tvCard.text.toString())) {
                    llCard.error = "请输入合法身份证号"
                }else {
                    llCard.error = ""
                }
                changeBtnState()
            }
        })

        rlBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }



        tvPost.setOnClickListener {
            if(!checkParamsVaild()) {
                return@setOnClickListener
            }

            hpLoadingFragment = HpLoadingFragment()
            hpLoadingFragment?.isCancelable = false
            hpLoadingFragment?.show(fragmentManager,"")
            val userInfo = HpLoginManager.getUserInfo()
            viewModel?.postCertification(userInfo?.puid,tvName.text.toString(),tvCard.text.toString())?.observe(this, {
                hpLoadingFragment?.dismiss()
                val intent = Intent()
                intent.putExtra(CERTIFICATION_RESULT_KEY,it)
                setResult(Activity.RESULT_OK,intent)
                finish()
            })
        }
    }

    private fun checkParamsVaild(): Boolean {
        if (TextUtils.isEmpty(tvName.text.toString()) || TextUtils.isEmpty(tvCard.text.toString()) || !CommonUtil.checkIsIDCard(tvCard.text.toString())) {
            return false
        }

        return true
    }

    private fun changeBtnState() {
        if (!checkParamsVaild()) {
            tvPost.setBackgroundResource(ReflectUtil.getDrawableId(this,"hp_game_core_dialog_btn_unselect_bg"))
            tvPost.setTextColor(Color.parseColor("#89909F"))
        }else {
            tvPost.setBackgroundResource(ReflectUtil.getDrawableId(this,"hp_game_core_dialog_btn_bg"))
            tvPost.setTextColor(Color.parseColor("#FFFFFF"))
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