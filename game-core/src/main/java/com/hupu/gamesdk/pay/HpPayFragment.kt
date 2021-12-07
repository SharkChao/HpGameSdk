package com.hupu.gamesdk.pay

import android.annotation.SuppressLint
import android.app.DialogFragment
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hupu.gamesdk.R
import com.hupu.gamesdk.base.*
import com.hupu.gamesdk.base.CommonUtil.Companion.dp2px
import com.hupu.gamesdk.config.HpConfigManager
import com.hupu.gamesdk.config.HpPayItem
import com.hupu.gamesdk.core.HpGamePay
import com.hupu.gamesdk.databinding.HpGameCorePayDialogBinding
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.login.HpLoginManager
import com.hupu.gamesdk.pay.entity.HpPayEntity
import com.hupu.gamesdk.pay.way.PayWayItemDispatch
import com.hupu.gamesdk.report.HpReportManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


internal class HpPayFragment: DialogFragment() {

    companion object {
        const val HP_PAY_INFO_KEY = "hupu_pay_info_key"
    }

    private var payList = HpConfigManager.getConfig()?.payConfig
    private var payEntity: HpPayEntity? = null
    private var payResult: Boolean = false

    private val dispatch = PayWayItemDispatch()
    private lateinit var dispatchAdapter: CommonDispatchAdapter
    private var _binding: HpGameCorePayDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: HpGamePay.HpPayListener? = null
    private val mainScope = MainScope()
    private val service = HpNetService.getRetrofit().create(HpPayService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HpGameCorePayDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        configOrientation()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configOrientation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initEvent()
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun initData() {
        setViewInit()
        initPay()
        binding.clContent.tvGameName.text = HpGameAppInfo.appName
        binding.clContent.tvProductName.text = payEntity?.productName
        binding.clContent.tvMoney.text = "${CommonUtil.fenToYuan(payEntity?.totalFee?:0)}元"

        dispatchAdapter = CommonDispatchAdapter.Builder()
            .registerDispatcher(dispatch)
            .build()
        binding.clContent.rvPayWay.adapter = dispatchAdapter
        binding.clContent.rvPayWay.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        dispatchAdapter.getDataList().clear()
        dispatchAdapter.getDataList().addAll(payList?: emptyList())
        dispatchAdapter.notifyDataSetChanged()
    }

    private fun initPay() {
        if (!payList.isNullOrEmpty()) {
            payList?.forEach {
                it.select = false
            }
            payList!![0].select = true
        }
        payEntity = arguments?.getSerializable(HP_PAY_INFO_KEY) as HpPayEntity?
    }


    private fun setSelectPayWay(payItem: HpPayItem) {
        payList?.forEach {
            it.select = false
        }
        payItem.select = true
    }

    private fun getSelectPayWay(): HpPayItem? {
        payList?.forEach {
            if (it.select) {
                return it
            }
        }

        return null
    }

    private fun initEvent() {
        binding.clContent.ivClose.setOnClickListener {
            dismiss()
        }

        dispatch.registerOnItemSelectListener(object : PayWayItemDispatch.OnItemSelectListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelect(data: HpPayItem) {
                setSelectPayWay(data)
                dispatchAdapter.notifyDataSetChanged()
            }
        })

        binding.clResult.tvSure.setOnClickListener {
            if (payResult) {
                listener?.success()
            }else {
                listener?.fail(ErrorType.PayFail.code,ErrorType.PayFail.msg)
            }
            dismiss()
        }

        binding.clContent.tvPay.setOnClickListener {
            setViewLoading()
            val selectPayCode = getSelectPayWay()?.code

            val hashMap = HashMap<String, Any?>()
            hashMap["appid"] = HpGameAppInfo.appId
            hashMap["title"] = payEntity?.productName
            hashMap["desc"] = payEntity?.productDesc
            hashMap["game_trade_no"] = payEntity?.gameTradeNo
            hashMap["pay_fee"] = payEntity?.totalFee
            hashMap["pay_way"] = selectPayCode
            hashMap["order_sign"] = payEntity?.sign
            hashMap["role_id"] = payEntity?.roleId
            hashMap["server_id"] = payEntity?.serverId
            hashMap["puid"] = HpLoginManager.getUserInfo()?.puid
            HpLogUtil.e("支付信息：${hashMap.toString()}")



            mainScope.launch {
                try {
                    flow {
                        try {
                            val result = service.startPay(hashMap)
                            emit(result)
                        }catch (e: Exception) {
                            e.printStackTrace()
                            emit(null)
                        }
                    }.flowOn(Dispatchers.IO).collectLatest {
                        val reportMap = HashMap<String, Any?>()
                        reportMap["way"] = getSelectPayWay()?.code
                        reportMap["money"] = payEntity?.totalFee
                        if (it.isSuccess()) {
                            if (selectPayCode == HpPayType.ALIPAY.value) {
                                CommonUtil.alipay(activity,it?.data?.payUrl){ result->
                                    setViewResult(result)

                                    reportMap["result"] = if (result) 1 else 0
                                    reportMap["error_msg"] = if (result) "" else "支付宝接口异常"
                                    HpReportManager.report(HpGameConstant.REPORT_PAY_RESULT,reportMap)
                                }
                            }
                        }else {
                            setViewResult(false)
                            Toast.makeText(activity,it?.message?:"支付失败,请稍后重试",Toast.LENGTH_SHORT).show()


                            reportMap["result"] = 0
                            reportMap["error_msg"] = it?.message?:"虎扑支付接口异常"
                            HpReportManager.report(HpGameConstant.REPORT_PAY_RESULT,reportMap)
                        }
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun configOrientation() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable())
            dialog?.window?.attributes?.apply {
                gravity = Gravity.BOTTOM
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialog?.window?.attributes = this
            }
        }else {
            dialog?.window?.setBackgroundDrawable(ColorDrawable())
            dialog?.window?.attributes?.apply {
                gravity = Gravity.CENTER
                width = activity.dp2px(400f).toInt()
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialog?.window?.attributes = this
            }
        }
    }

    private fun setViewInit() {
        binding.clContent.root.visibility = View.VISIBLE
        binding.clLoading.root.visibility = View.GONE
        binding.clResult.root.visibility = View.GONE
    }
    private fun setViewLoading() {
        binding.clLoading.root.visibility = View.VISIBLE
        binding.clContent.root.visibility = View.GONE
        binding.clResult.root.visibility = View.GONE
    }

    private fun setViewResult(success: Boolean) {
        binding.clResult.root.visibility = View.VISIBLE
        binding.clContent.root.visibility = View.GONE
        binding.clLoading.root.visibility = View.GONE
        if (success) {
            binding.clResult.tvStatus.text = "支付成功"
            binding.clResult.ivStatus.setImageResource(R.mipmap.hp_game_core_pay_dialog_success_icon)
        }else {
            binding.clResult.tvStatus.text = "支付失败"
            binding.clResult.ivStatus.setImageResource(R.mipmap.hp_game_core_pay_dialog_fail_icon)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    fun registerPayListener(listener: HpGamePay.HpPayListener) {
        this.listener = listener
    }

     sealed class ViewState {
         object Init: ViewState()
         object Loading: ViewState()
         data class Result(val success: Boolean): ViewState()
    }
}