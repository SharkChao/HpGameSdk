package com.hupu.gamesdk.login

import android.app.Activity
import android.app.DialogFragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hupu.gamesdk.core.HpGame
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.base.CommonUtil
import com.hupu.gamesdk.base.CommonUtil.Companion.encrypt
import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.base.activitycallback.ActResultRequest
import com.hupu.gamesdk.core.HpGameLogin
import com.hupu.gamesdk.databinding.HpGameCoreLoginDialogBinding
import org.json.JSONObject
import java.net.URLEncoder

internal class HpLoginFragment: DialogFragment() {
    private var _binding: HpGameCoreLoginDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: HpGameLogin.HpLoginListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HpGameCoreLoginDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEvent()
    }

    private fun initView() {
        binding.tvDesc.text = if (HpLoginManager.hasLoginedBefore()) "切换账号请先至虎扑APP内操作" else "如遇问题可前往虎扑app-「我的」进行咨询反馈，我们将第一时间催促游戏方处理"
    }

    private fun initEvent() {
        binding.tvLogin.setOnClickListener {

            if (CommonUtil.isAppInstalled2(activity,HpGameConstant.HUPU_PACKAGE_NAME)) {
                try {
                   startHpLogin()
                }catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity,"请安装最新版本虎扑后再试！",Toast.LENGTH_SHORT).show()
                    listener?.fail(ErrorType.LoginHpNotSupportSchema.code,ErrorType.LoginHpNotSupportSchema.msg)
                    CommonUtil.goToMarket(activity,HpGameConstant.HUPU_PACKAGE_NAME)
                }
            }else {
                Toast.makeText(activity,"请安装最新版本虎扑后再试！",Toast.LENGTH_SHORT).show()
                listener?.fail(ErrorType.LoginNotInstallHp.code,ErrorType.LoginNotInstallHp.msg)
                CommonUtil.goToMarket(activity,HpGameConstant.HUPU_PACKAGE_NAME)
            }
        }
    }


    private fun startHpLogin() {
        val uri = "hupugame://com.hupu.games/sign?appId=${HpGameAppInfo.appId}&appKey=${URLEncoder.encode(HpGameAppInfo.appKey,"UTF-8")}&appIcon=${URLEncoder.encode(HpGameAppInfo.appIcon,"UTF-8")}&appName=${URLEncoder.encode(HpGameAppInfo.appName,"UTF-8")}&sdkVersion=${HpGame.sdkVersion}"
        val intent = Intent()
        intent.data = Uri.parse(uri)

        val actResultRequest = ActResultRequest(activity)
        actResultRequest.startForResult(intent
        ) { resultCode, data ->
            if (data != null && resultCode == Activity.RESULT_OK) {
                val result = data.getStringExtra("data") ?: ""

                val jsonObject = JSONObject(result)
                val code = jsonObject.optInt("code")//0：用户取消授权  1：授权成功 2：授权失败
                val jsonData = jsonObject.optJSONObject("data")
                if (code == 1 && jsonData != null && !TextUtils.isEmpty(jsonData.optString("access_token"))) {
                    //成功
                    val hpUserEntity = HpUserEntity()
                    hpUserEntity.accessToken = jsonData.optString("access_token")
                    hpUserEntity.puid = jsonData.optString("puid")
                    hpUserEntity.nickName = jsonData.optString("nickname")
                    hpUserEntity.head = jsonData.optString("head")
                    HpLoginManager.saveUserInfo(hpUserEntity)

                    onLoginSuccess()
                } else {
                    if (code == 0) {
                        Toast.makeText(
                            activity,
                            jsonObject.optString("msg") ?: "授权失败，请稍后重试!",
                            Toast.LENGTH_SHORT
                        ).show()
                        listener?.fail(ErrorType.LoginCancel.code, ErrorType.LoginCancel.msg)
                    } else {
                        Toast.makeText(
                            activity,
                            jsonObject.optString("msg") ?: "授权失败，请稍后重试!",
                            Toast.LENGTH_SHORT
                        ).show()
                        listener?.fail(
                            ErrorType.LoginNetError.code,
                            jsonObject.optString("msg") ?: "授权失败，请稍后重试!"
                        )
                    }
                }
            } else {
                Toast.makeText(activity, "授权失败，请稍后重试!", Toast.LENGTH_SHORT).show()
                listener?.fail(ErrorType.LoginResultError.code, ErrorType.LoginResultError.msg)
            }
        }
    }

    fun registerLoginListener(listener: HpGameLogin.HpLoginListener?) {
        this.listener = listener
    }


    private fun onLoginSuccess() {
        val jsonObject = JSONObject()
        val userInfo = HpLoginManager.getUserInfo()
        jsonObject.put("puid",userInfo?.puid)
        jsonObject.put("nickname",userInfo?.nickName)
        jsonObject.put("head",userInfo?.head)
        listener?.success(jsonObject)
        dismiss()
    }
}