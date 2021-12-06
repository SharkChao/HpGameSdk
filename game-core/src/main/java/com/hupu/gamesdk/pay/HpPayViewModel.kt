package com.hupu.gamesdk.pay

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hupu.gamesdk.config.HpConfigManager
import com.hupu.gamesdk.config.HpPayItem
import com.hupu.gamesdk.pay.entity.HpPayOrderResult
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class HpPayViewModel: ViewModel() {
    private val mainScope = MainScope()
    private val mRepository = HpPayRepository()
    private var payResult: Boolean = false
    private var payList = HpConfigManager.getConfig()?.payConfig
    private var viewState: HpPayFragment.ViewState = HpPayFragment.ViewState.Init
    init {
        if (!payList.isNullOrEmpty()) {
            payList?.forEach {
                it.select = false
            }
            payList!![0].select = true
        }
    }

    fun startPay(hashMap: HashMap<String,Any?>): MutableLiveData<HpPayOrderResult?> {
        val payResultData = MutableLiveData<HpPayOrderResult?>()
        mainScope.launch {
            mRepository.startPay(hashMap).collectLatest {
                payResultData.postValue(it)
            }
        }

        return payResultData
    }

    fun getPayList(): List<HpPayItem>? {
        return payList
    }

    fun setSelectPayItem(payItem: HpPayItem) {
        payList?.forEach {
            it.select = false
        }
        payItem.select = true
    }


    fun getSelectPayItem(): HpPayItem? {
        payList?.forEach {
            if (it.select) {
                return it
            }
        }
        return null
    }

    fun checkParams(): Boolean {
        if (getSelectPayItem() == null) {
            return false
        }

        return true
    }

    fun setViewState(viewState: HpPayFragment.ViewState) {
        this.viewState = viewState
    }

    fun getViewState(): HpPayFragment.ViewState {
        return viewState
    }

    fun setPayResult(payResult: Boolean) {
        this.payResult = payResult
    }

    fun getPayResult(): Boolean {
        return payResult
    }

    override fun onCleared() {
        super.onCleared()
        mainScope.cancel()
    }
}