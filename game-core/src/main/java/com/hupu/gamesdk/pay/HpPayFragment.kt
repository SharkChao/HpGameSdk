package com.hupu.gamesdk.pay

import android.annotation.SuppressLint
import android.app.DialogFragment
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.hupu.gamesdk.base.*
import com.hupu.gamesdk.base.CommonUtil.Companion.dp2px
import com.hupu.gamesdk.config.HpConfigManager
import com.hupu.gamesdk.config.HpPayItem
import com.hupu.gamesdk.core.HpGamePay
import com.hupu.gamesdk.init.HpGameAppInfo
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
    private var listener: HpGamePay.HpPayListener? = null
    private val mainScope = MainScope()
    private val service = HpNetService.getRetrofit().create(HpPayService::class.java)
    private lateinit var tvGameName: TextView
    private lateinit var tvProductName: TextView
    private lateinit var tvMoney: TextView
    private lateinit var rvPayWay: RecyclerView
    private lateinit var ivClose: ImageView
    private lateinit var tvSure: TextView
    private lateinit var tvPay: TextView
    private lateinit var clContent: View
    private lateinit var clResult: View
    private lateinit var clLoading: View
    private lateinit var tvStatus: TextView
    private lateinit var ivStatus: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(
            ReflectUtil.getLayoutId(activity, "hp_game_core_pay_dialog"),
            container,
            false
        )
        //添加这一行
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        tvGameName = v.findViewById(ReflectUtil.getViewId(activity,"tv_game_name"))
        tvProductName = v.findViewById(ReflectUtil.getViewId(activity,"tv_product_name"))
        tvMoney = v.findViewById(ReflectUtil.getViewId(activity,"tv_money"))
        rvPayWay = v.findViewById(ReflectUtil.getViewId(activity,"rv_pay_way"))
        ivClose = v.findViewById(ReflectUtil.getViewId(activity,"iv_close"))
        tvSure = v.findViewById(ReflectUtil.getViewId(activity,"tv_sure"))
        tvPay = v.findViewById(ReflectUtil.getViewId(activity,"tv_pay"))
        clContent = v.findViewById(ReflectUtil.getViewId(activity,"cl_content"))
        clResult = v.findViewById(ReflectUtil.getViewId(activity,"cl_result"))
        clLoading = v.findViewById(ReflectUtil.getViewId(activity,"cl_loading"))
        tvStatus = v.findViewById(ReflectUtil.getViewId(activity,"tv_status"))
        ivStatus = v.findViewById(ReflectUtil.getViewId(activity,"iv_status"))
        return v
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
        tvGameName.text = HpGameAppInfo.appName
        tvProductName.text = payEntity?.productName
        tvMoney.text = "${CommonUtil.fenToYuan(payEntity?.totalFee?:0)}元"

        dispatchAdapter = CommonDispatchAdapter.Builder()
            .registerDispatcher(dispatch)
            .build()
        rvPayWay.adapter = dispatchAdapter
        rvPayWay.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
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
        ivClose.setOnClickListener {
            dismiss()
        }

        dispatch.registerOnItemSelectListener(object : PayWayItemDispatch.OnItemSelectListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelect(data: HpPayItem) {
                setSelectPayWay(data)
                dispatchAdapter.notifyDataSetChanged()
            }
        })

        tvSure.setOnClickListener {
            if (payResult) {
                listener?.success()
            }else {
                listener?.fail(ErrorType.PayFail.code,ErrorType.PayFail.msg)
            }
            dismiss()
        }

        tvPay.setOnClickListener {
            setViewLoading()
            val selectPayCode = getSelectPayWay()?.code

            val hashMap = HashMap<String, Any?>()
            hashMap["appid"] = payEntity?.appid
            hashMap["title"] = payEntity?.productName
            hashMap["desc"] = payEntity?.productDesc
            hashMap["game_trade_no"] = payEntity?.gameTradeNo
            hashMap["pay_fee"] = payEntity?.totalFee
            hashMap["pay_way"] = selectPayCode
            hashMap["order_sign"] = payEntity?.sign
            hashMap["role_id"] = payEntity?.roleId
            hashMap["server_id"] = payEntity?.serverId
            hashMap["puid"] = payEntity?.puid
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
        clContent.visibility = View.VISIBLE
        clLoading.visibility = View.GONE
        clResult.visibility = View.GONE
    }
    private fun setViewLoading() {
        clLoading.visibility = View.VISIBLE
        clContent.visibility = View.GONE
        clResult.visibility = View.GONE
    }

    private fun setViewResult(success: Boolean) {
        payResult = success
        clResult.visibility = View.VISIBLE
        clContent.visibility = View.GONE
        clLoading.visibility = View.GONE
        if (success) {
            tvStatus.text = "支付成功"
            ivStatus.setImageResource(ReflectUtil.getDrawableId(activity,"hp_game_core_pay_dialog_success_icon"))
        }else {
            tvStatus.text = "支付失败"
            ivStatus.setImageResource(ReflectUtil.getDrawableId(activity,"hp_game_core_pay_dialog_fail_icon"))
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