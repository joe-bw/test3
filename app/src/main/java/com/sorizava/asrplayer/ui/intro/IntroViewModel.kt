/*
 * Create by jhong on 2022. 7. 11.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.intro

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorizava.asrplayer.config.EarzoomLoginManager
import com.sorizava.asrplayer.data.IntroState
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.extension.getVersion
import com.sorizava.asrplayer.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.mozilla.focus.ext.application

private const val MAX_DELAY_TIME_WITH_INTRO = 3000L

class IntroViewModel(private val context: Application) : ViewModel() {

    private val introStatePrivate = MutableLiveData<IntroState>()
    val introState: LiveData<IntroState> = introStatePrivate

    private val appVersionPrivate = MutableLiveData<String>()
    val appVersion: LiveData<String> = appVersionPrivate

    init {

        appVersionPrivate.value = context.getVersion()

        viewModelScope.launch {
            introStatePrivate.value = IntroState.LOADING

            val time = System.currentTimeMillis()

            if (!isLatestVersion()) {
                introStatePrivate.value = IntroState.NEED_APP_UPDATE
            } else {
                val delayTime = (System.currentTimeMillis() - time)

                if (MAX_DELAY_TIME_WITH_INTRO > delayTime) {
                    delay(MAX_DELAY_TIME_WITH_INTRO - delayTime)
                }

                introStatePrivate.value = IntroState.CHECK_LOGIN
            }
        }
    }

    private fun isLatestVersion(): Boolean {
        return this.context.application.isLatestVersion()
    }

    fun checkLoginInfo() {
        viewModelScope.launch {

            val birth = EarzoomLoginManager.instance?.prefUserBirth
            val phone = EarzoomLoginManager.instance?.prefUserPhone

            if (EarzoomLoginManager.instance?.userSNSType == EarzoomLoginManager.SNS_TYPE_NONE) {
                introStatePrivate.value = IntroState.GOTO_LOGIN
            } else if (TextUtils.isEmpty(birth)) {
                introStatePrivate.value = IntroState.GOTO_LOGIN
            } else {
                if (birth != null && phone != null){
                    val request = LoginNewRequest(birth, phone)
                    val repository = LoginRepository(context.application, request)
                    repository.requestMemberInfo().collect {
                        when (it) {
                            is ResultState.Success -> {
                                introStatePrivate.value = IntroState.GOTO_MAIN
                            }
                            else -> {
                                introStatePrivate.value = IntroState.GOTO_LOGIN
                            }
                        }
                    }
                }
            }
        }
    }
}
