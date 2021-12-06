package com.hupu.gamesdk.pay

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hupu.gamesdk.R
import com.hupu.gamesdk.base.*
import com.hupu.gamesdk.base.CommonUtil.Companion.dp2px
import com.hupu.gamesdk.config.HpPayItem
import com.hupu.gamesdk.core.HpGamePay
import com.hupu.gamesdk.databinding.HpGameCorePayDialogBinding
import com.hupu.gamesdk.init.HpGameAppInfo
import com.hupu.gamesdk.login.HpLoginManager
import com.hupu.gamesdk.pay.entity.HpPayEntity
import com.hupu.gamesdk.pay.way.PayWayItemDispatch
import com.hupu.gamesdk.report.HpReportManager


internal class HpPayFragment: DialogFragment() {

    companion object {
        const val HP_PAY_INFO_KEY = "hupu_pay_info_key"
    }

    private var viewModel: HpPayViewModel? = null
    private var payEntity: HpPayEntity? = null
    private val dispatch = PayWayItemDispatch()
    private lateinit var dispatchAdapter: CommonDispatchAdapter
    private var _binding: HpGameCorePayDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: HpGamePay.HpPayListener? = null

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

        viewModel = ViewModelProviders.of(this).get(HpPayViewModel::class.java)

        setViewState(viewModel!!.getViewState())
        payEntity = arguments?.getSerializable(HP_PAY_INFO_KEY) as HpPayEntity?
        binding.clContent.tvGameName.text = HpGameAppInfo.appName
        binding.clContent.tvProductName.text = payEntity?.productName
        binding.clContent.tvMoney.text = "${CommonUtil.fenToYuan(payEntity?.totalFee?:0)}元"

        dispatchAdapter = CommonDispatchAdapter.Builder()
            .registerDispatcher(dispatch)
            .build()
        binding.clContent.rvPayWay.adapter = dispatchAdapter
        binding.clContent.rvPayWay.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        dispatchAdapter.getDataList().clear()
        dispatchAdapter.getDataList().addAll(viewModel!!.getPayList()?: emptyList())
        dispatchAdapter.notifyDataSetChanged()
    }

    private fun initEvent() {
        binding.clContent.ivClose.setOnClickListener {
            dismiss()
        }

        dispatch.registerOnItemSelectListener(object : PayWayItemDispatch.OnItemSelectListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelect(data: HpPayItem) {
                viewModel?.setSelectPayItem(data)
                dispatchAdapter.notifyDataSetChanged()
            }
        })

        binding.clResult.tvSure.setOnClickListener {
            if (viewModel?.getPayResult() == true) {
                listener?.success()
            }else {
                listener?.fail(ErrorType.PayFail.code,ErrorType.PayFail.msg)
            }
            dismiss()
        }

        binding.clContent.tvPay.setOnClickListener {
            if (viewModel?.checkParams() == false){
                Toast.makeText(requireContext(),"支付失败，请稍后重试",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            setViewState(ViewState.Loading)
            val hashMap = HashMap<String, Any?>()
            hashMap["appid"] = HpGameAppInfo.appId
            hashMap["title"] = payEntity?.productName
            hashMap["desc"] = payEntity?.productDesc
            hashMap["game_trade_no"] = payEntity?.gameTradeNo
            hashMap["pay_fee"] = payEntity?.totalFee
            hashMap["pay_way"] = viewModel?.getSelectPayItem()?.code
            hashMap["order_sign"] = payEntity?.sign
            hashMap["role_id"] = payEntity?.roleId
            hashMap["server_id"] = payEntity?.serverId
            hashMap["puid"] = HpLoginManager.getUserInfo()?.puid
            HpLogUtil.e("支付信息：${hashMap.toString()}")

            viewModel?.startPay(hashMap)?.observe(this, Observer {
                val reportMap = HashMap<String, Any?>()
                reportMap["way"] = viewModel?.getSelectPayItem()?.code
                reportMap["money"] = payEntity?.totalFee
                if (it.isSuccess()) {
                    if (viewModel?.getSelectPayItem()?.code == HpPayType.ALIPAY.value) {
                        CommonUtil.alipay(requireActivity(),it?.data?.payUrl){ result->
                            setViewState(ViewState.Result(result))
                            viewModel?.setPayResult(result)

                            reportMap["result"] = if (result) 1 else 0
                            reportMap["error_msg"] = if (result) "" else "支付宝接口异常"
                            HpReportManager.report(HpGameConstant.REPORT_PAY_RESULT,reportMap)
                        }
                    }
                }else {
                    setViewState(ViewState.Result(false))
                    Toast.makeText(requireContext(),it?.message?:"支付失败,请稍后重试",Toast.LENGTH_SHORT).show()


                    reportMap["result"] = 0
                    reportMap["error_msg"] = it?.message?:"虎扑支付接口异常"
                    HpReportManager.report(HpGameConstant.REPORT_PAY_RESULT,reportMap)
                }
            })
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
                width = requireContext().dp2px(400f).toInt()
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialog?.window?.attributes = this
            }
        }
    }

    private fun setViewState(viewState: ViewState) {
        viewModel?.setViewState(viewState)
        when(viewState) {
             is ViewState.Init -> {
                binding.clContent.root.visibility = View.VISIBLE
                 binding.clLoading.root.visibility = View.GONE
                 binding.clResult.root.visibility = View.GONE
            }
            is ViewState.Loading -> {
                binding.clLoading.root.visibility = View.VISIBLE
                binding.clContent.root.visibility = View.GONE
                binding.clResult.root.visibility = View.GONE
            }
            is ViewState.Result -> {
                binding.clResult.root.visibility = View.VISIBLE
                binding.clContent.root.visibility = View.GONE
                binding.clLoading.root.visibility = View.GONE
                if (viewState.success) {
                    binding.clResult.tvStatus.text = "支付成功"
                    binding.clResult.ivStatus.setImageResource(R.mipmap.hp_game_core_pay_dialog_success_icon)
                }else {
                    binding.clResult.tvStatus.text = "支付失败"
                    binding.clResult.ivStatus.setImageResource(R.mipmap.hp_game_core_pay_dialog_fail_icon)
                }
            }
        }
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