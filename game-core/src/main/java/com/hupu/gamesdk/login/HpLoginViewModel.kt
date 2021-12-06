package com.hupu.gamesdk.login

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class HpLoginViewModel: ViewModel() {
    private val repository = HpLoginRepository()
    private val mainScope = MainScope()

    fun checkAccessToken(): MutableLiveData<AccessTokenResult?> {
        val checkTokenData = MutableLiveData<AccessTokenResult?>()
        mainScope.launch {
            repository.checkAccessToken().collectLatest {
                checkTokenData.postValue(it)
            }
        }
        return checkTokenData
    }

    override fun onCleared() {
        super.onCleared()
        mainScope.cancel()
    }

}