/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.app.Activity
import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.sorizava.asrplayer.config.EarzoomLoginManager
import com.sorizava.asrplayer.data.IntroState
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.model.FacebookData
import com.sorizava.asrplayer.data.model.KakaoData
import com.sorizava.asrplayer.data.model.NaverData
import com.sorizava.asrplayer.data.model.SnsResultData
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.repository.LoginRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.mozilla.focus.extension.toActivity

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    var callbackManager: CallbackManager? = null

    private val loginStatePrivate = MutableLiveData<IntroState>()
    val loginState: LiveData<IntroState> = loginStatePrivate

    private val signCallbackPrivate = MutableLiveData<ResultState<Unit>>()
    val signCallback: LiveData<ResultState<Unit>> = signCallbackPrivate

    fun callKakaoLogin(
        context: Activity,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        // 기기에 카카오톡 설치되있는 경우
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance
                .loginWithKakaoTalk(context) { token: OAuthToken?, error: Throwable? ->
                    kakaoLoginCallback(
                        token,
                        error,
                        onSuccess,
                        onFailed
                    )
                }
        } else {
            UserApiClient.instance
                .loginWithKakaoAccount(context) { token: OAuthToken?, error: Throwable? ->
                    kakaoLoginCallback(
                        token,
                        error,
                        onSuccess,
                        onFailed
                    )
                }
        }
    }

    private fun kakaoLoginCallback(
        token: OAuthToken?,
        error: Throwable?,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        if (error != null) {

            val result = KakaoData(
                errorMessage = error.message
            )

            onFailed.invoke(result)

        } else if (token != null) {
            // 유저정보 획득
            UserApiClient.instance.me { user: User?, e: Throwable? ->
                if (e != null) {
                    e.printStackTrace()

                    val result = KakaoData(
                        errorMessage = e.message
                    )

                    onFailed.invoke(result)

                } else if (user != null) {
                    val account = user.kakaoAccount
                    var email: String? = null
                    if (account != null) {
                        email = account.email
                    }

                    val result = KakaoData(
                        token = token.accessToken,
                        id = user.id.toString(),
                        email = email
                    )

                    onSuccess.invoke(result)
                }
            }
        }
    }

    fun callNaverLogin(
        context: Context,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        NaverIdLoginSDK.behavior = NidOAuthBehavior.DEFAULT
        NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
            override fun onSuccess() {

                val accessToken = NaverIdLoginSDK.getAccessToken()

                val result = NaverData(
                    token = accessToken
                )

                onSuccess.invoke(result)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()

                val result = NaverData(
                    errorCode = errorCode,
                    errorDescription = errorDescription
                )

                onFailed.invoke(result)
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    fun callFacebookLogin(
        context: Activity,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        val callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val token = loginResult.accessToken.token
                    val credential = FacebookAuthProvider.getCredential(token)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(context.toActivity()
                        ) { task: Task<AuthResult?> ->
                            if (task.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                if (user != null) {
                                    val result = FacebookData(
                                        token = token,
                                        id = user.uid,
                                        email = user.email
                                    )
                                    onSuccess.invoke(result)
                                }
                            } else {
                                val result = FacebookData(
                                    errorMessage = "Authentication failed."
                                )
                                onFailed.invoke(result)
                            }
                        }
                }

                override fun onCancel() {
                    Log.i("Facebook", "cancel")
                    val result = FacebookData(
                        errorMessage = "cancel."
                    )
                    onFailed.invoke(result)
                }

                override fun onError(error: FacebookException) {
                    Log.i("Facebook", "error >> $error")
                    val result = FacebookData(
                        errorMessage = "error >> $error"
                    )
                    onFailed.invoke(result)
                }
            })
        LoginManager.getInstance()
            .logInWithReadPermissions(context.toActivity(), listOf("email", "public_profile"))
    }

    fun checkLoginInfo() {
        viewModelScope.launch {

            val birth = EarzoomLoginManager.instance?.prefUserBirth
            val phone = EarzoomLoginManager.instance?.prefUserPhone

            if (EarzoomLoginManager.instance?.userSNSType == EarzoomLoginManager.SNS_TYPE_NONE) {
                loginStatePrivate.value = IntroState.GOTO_LOGIN
            } else if (TextUtils.isEmpty(birth)) {
                loginStatePrivate.value = IntroState.GOTO_LOGIN
            } else {
                if (birth != null && phone != null) {
                    val request = LoginNewRequest(birth, phone)
                    val repository = LoginRepository(context, request)
                    repository.requestMemberInfo().collect {
                        when (it) {
                            is ResultState.Success -> {
                                loginStatePrivate.value = IntroState.GOTO_MAIN
                            }
                            is ResultState.Error -> {
                                if (it.errorCode == 404) {
                                    loginStatePrivate.value = IntroState.GOTO_SIGN_UP
                                } else {
                                    loginStatePrivate.value = IntroState.FAILED_LOGIN
                                }
                            }
                            is ResultState.Loading -> {}
                        }
                    }
                }
            }
        }
    }
}


