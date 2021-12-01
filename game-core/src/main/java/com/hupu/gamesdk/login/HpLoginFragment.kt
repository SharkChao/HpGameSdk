package com.hupu.gamesdk.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.hupu.gamesdk.core.HpGame
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.base.CommonUtil
import com.hupu.gamesdk.base.CommonUtil.Companion.encrypt
import com.hupu.gamesdk.base.ErrorType
import com.hupu.gamesdk.base.HpGameConstant
import com.hupu.gamesdk.core.HpGameLogin
import com.hupu.gamesdk.databinding.HpGameCoreLoginDialogBinding
import org.json.JSONObject

internal class HpLoginFragment: DialogFragment() {
    private val viewModel: HpLoginViewModel by viewModels()
    private var _binding: HpGameCoreLoginDialogBinding? = null
    private val binding get() = _binding!!
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var listener: HpGameLogin.HpLoginListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //此处是跳转的result回调方法
            if (it.data != null && it.resultCode == Activity.RESULT_OK) {
                val result = it.data?.getStringExtra("data") ?: ""

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
                }else {
                    if (code == 0) {
                        Toast.makeText(requireContext(),jsonObject.optString("msg")?:"授权失败，请稍后重试!",Toast.LENGTH_SHORT).show()
                        listener?.fail(ErrorType.LoginCancel.code,ErrorType.LoginCancel.msg)
                    }else {
                        Toast.makeText(requireContext(),jsonObject.optString("msg")?:"授权失败，请稍后重试!",Toast.LENGTH_SHORT).show()
                        listener?.fail(ErrorType.LoginNetError.code,jsonObject.optString("msg")?:"授权失败，请稍后重试!")
                    }
                }
            }else {
                Toast.makeText(requireContext(),"授权失败，请稍后重试!",Toast.LENGTH_SHORT).show()
                listener?.fail(ErrorType.LoginResultError.code,ErrorType.LoginResultError.msg)
            }
        }
    }
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
        binding.tvDesc.text = if (HpLoginManager.hasLoginedBefore()) "切换账号请先至虎扑APP内操作" else "如遇问题可前往虎扑app-「我的」进行咨询反馈，我们将第一时间为您跟进~"
    }

    private fun initEvent() {
        binding.tvLogin.setOnClickListener {

            if (CommonUtil.isAppInstalled2(requireContext(),HpGameConstant.HUPU_PACKAGE_NAME)) {
                try {
                    val uri = "hupugame://com.hupu.games/sign?appId=${HpGameAppInfo.appId}&appKey=${HpGameAppInfo.appKey?.encrypt()}&appIcon=${HpGameAppInfo.appIcon}&appName=${HpGameAppInfo.appName}&sdkVersion=${HpGame.sdkVersion}"
                    val intent = Intent()
                    intent.data = Uri.parse(uri)
                    launcher?.launch(intent)
                }catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(),"请安装最新版本虎扑后再试！",Toast.LENGTH_SHORT).show()
                    listener?.fail(ErrorType.LoginHpNotSupportSchema.code,ErrorType.LoginHpNotSupportSchema.msg)
                    CommonUtil.goToMarket(requireContext(),HpGameConstant.HUPU_PACKAGE_NAME)
                }
            }else {
                Toast.makeText(requireContext(),"请安装最新版本虎扑后再试！",Toast.LENGTH_SHORT).show()
                listener?.fail(ErrorType.LoginNotInstallHp.code,ErrorType.LoginNotInstallHp.msg)
                CommonUtil.goToMarket(requireContext(),HpGameConstant.HUPU_PACKAGE_NAME)
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