package com.hupu.gamesdk.pay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.hupu.gamesdk.config.HpConfigManager
import com.hupu.gamesdk.config.HpPayItem
import com.hupu.gamesdk.pay.entity.HpPayEntity
import kotlinx.coroutines.flow.collectLatest

internal class HpPayViewModel: ViewModel() {
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

    fun startPay(hashMap: HashMap<String,Any?>) = liveData{
        mRepository.startPay(hashMap).collectLatest {
            emit(it)
        }
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
}