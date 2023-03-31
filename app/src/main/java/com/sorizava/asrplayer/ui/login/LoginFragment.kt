/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sorizava.asrplayer.config.EarzoomLoginManager
import com.sorizava.asrplayer.config.EarzoomLoginManager.Companion.SNS_TYPE_FACEBOOK
import com.sorizava.asrplayer.config.EarzoomLoginManager.Companion.SNS_TYPE_GOOGLE
import com.sorizava.asrplayer.config.EarzoomLoginManager.Companion.SNS_TYPE_KAKAO
import com.sorizava.asrplayer.config.EarzoomLoginManager.Companion.SNS_TYPE_NAVER
import com.sorizava.asrplayer.config.LOGIN_TYPE_RELOGIN
import com.sorizava.asrplayer.data.IntroState
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.model.*
import com.sorizava.asrplayer.extension.beGone
import com.sorizava.asrplayer.extension.beVisible
import com.sorizava.asrplayer.extension.observe
import com.sorizava.asrplayer.extension.toast
import com.sorizava.asrplayer.ui.base.BaseFragment
import com.sorizava.asrplayer.ui.privacy.PrivacyPolicyActivity
import com.sorizava.asrplayer.ui.signup.Signup2Activity
import org.mozilla.focus.R
import org.mozilla.focus.activity.MainActivity
import org.mozilla.focus.databinding.FragmentLoginBinding
import org.mozilla.focus.ext.application


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate),
    AddInfoDialog.AddInfoDialogListener {

    companion object {
        fun newInstance() = LoginFragment()
        private const val RC_SIGN_IN = 1000
    }

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var viewModel: LoginViewModel

    override fun initView() {
        val context = activity?.application

        viewModel = ViewModelProvider(this, LoginViewModelFactory(context as Application))
            .get(LoginViewModel::class.java)

        setUI()

        if (arguments != null) {
            val isReLogin = requireArguments().getBoolean(LOGIN_TYPE_RELOGIN, false)
            if (isReLogin) {
                setReLoginUI()
            }
        }

        setGoogleSignInit()
    }

    private fun setGoogleSignInit() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    /** 추가 정보 등록 UI 처리  */
    private fun setReLoginUI() {
        binding.apply {

            btnLoginNaver.isEnabled = false
            btnLoginKakao.isEnabled = false
            btnLoginFacebook.isEnabled = false
            btnLoginGoogle.isEnabled = false

            layoutAddInfo.beVisible()

            btnAddInfo.setOnClickListener {
                checkMemberOrNot()
            }
        }
    }

    private fun setUI() {
        binding.apply {

            btnLoginNaver.setOnClickListener {
                viewModel.callNaverLogin(requireContext(), ::onSuccessLogin, ::onFailedLogin)
            }

            btnLoginKakao.setOnClickListener {
                activity?.let { viewModel.callKakaoLogin(it, ::onSuccessLogin, ::onFailedLogin) }
            }

            btnLoginFacebook.setOnClickListener {
                viewModel.callFacebookLogin(requireActivity(), ::onSuccessLogin, ::onFailedLogin)
            }

            btnLoginGoogle.setOnClickListener {
                signInGoogle()
            }
        }
    }

    /** 2021.10.31 개인정보 확인  */
    private fun checkMemberOrNot() {
        if (TextUtils.isEmpty(EarzoomLoginManager.instance?.prefUserBirth)) {
            showNoticeDialog()
        } else {
            /** 회원 가입 여부 확인  */
            viewModel.checkLoginInfo()
        }
    }

    private fun showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        val dialog: DialogFragment = AddInfoDialog(this)
        dialog.show(activity?.supportFragmentManager!!, "AddInfoDialog")
    }

    override fun initViewModelObserver() {
        observe(viewModel.signCallback, ::snsCallback)
        observe(viewModel.loginState, ::loginStateHandle)
    }

    private fun loginStateHandle(introState: IntroState) {

        when(introState) {

            IntroState.GOTO_LOGIN -> {}
            IntroState.GOTO_MAIN -> {
                gotoMainActivity()
            }
            IntroState.GOTO_SIGN_UP -> {
                showSignupPopup()
            }
            IntroState.FAILED_LOGIN -> {
                showErrorPopup()
            }
            else -> {}
        }
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            val result = GoogleData(
                token = account.idToken,
                id = account.id,
                email = account.email
            )
            onSuccessLogin(result)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("TEST", "signInResult:failed code=" + e.statusCode)
            val result = GoogleData(
                errorCode = e.statusCode
            )
            onFailedLogin(result)
        }
    }

    private fun showSignupPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.txt_notice))
            .setCancelable(false)
            .setMessage(getString(R.string.txt_signup_error))
            .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                gotoSignupActivity()
            }
            .setNegativeButton(getString(R.string.action_cancel)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                setReLoginUI()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showErrorPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.txt_fail_login))
            .setMessage(getString(R.string.txt_login_error))
            .setPositiveButton(getString(R.string.ok)) {
                    dialog: DialogInterface, _: Int -> dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun onSuccessLogin(result: SnsResultData) {

        hideLoadingBar()

        // todo 각 SNS 에 따른 정보 저장
        when (result.type) {
            SnsProvider.EMAIL -> {
            }
            SnsProvider.KAKAO -> {
                val kakaoData = result as KakaoData
                setLoginInfo(SNS_TYPE_KAKAO, "" + kakaoData.id)
                checkMemberOrNot()
            }
            SnsProvider.NAVER -> {
                val naverData = result as NaverData
                setLoginInfo(SNS_TYPE_NAVER, "" + naverData.accessToken)
                checkMemberOrNot()
            }
            SnsProvider.FACEBOOK -> {
                val facebookData = result as FacebookData
                setLoginInfo(SNS_TYPE_FACEBOOK, "" + facebookData.token)
                checkMemberOrNot()
            }
            SnsProvider.GOOGLE -> {
                val googleData = result as GoogleData
                setLoginInfo(SNS_TYPE_GOOGLE, "" + googleData.id)
                checkMemberOrNot()
            }
            else -> {}
        }
    }

    private fun setLoginInfo(snsType: Int, id: String) {
        EarzoomLoginManager.instance?.putUserSNSType(snsType)
        EarzoomLoginManager.instance?.putUserId(id)
        context?.application?.setFCMSubscribe()
    }

    private fun onFailedLogin(error: SnsResultData) {
        hideLoadingBar()

        when (error.type) {
            SnsProvider.EMAIL -> {

            }
            SnsProvider.KAKAO -> {
                activity?.toast("로그인이 정상적으로 이루어지지 않았습니다. 다시 확인바랍니다.")
            }
            SnsProvider.NAVER -> {
                activity?.toast("로그인이 정상적으로 이루어지지 않았습니다. 다시 확인바랍니다.")
            }
            SnsProvider.FACEBOOK -> {
                activity?.toast("로그인이 정상적으로 이루어지지 않았습니다. 다시 확인바랍니다.")
            }
            SnsProvider.GOOGLE -> {
                activity?.toast("로그인이 정상적으로 이루어지지 않았습니다. 다시 확인바랍니다.")
            }
            else -> {}
        }
    }

    private fun snsCallback(result: ResultState<Unit>) {
        when(result) {
            is ResultState.Loading -> {
                showLoadingBar()
            }
            else -> {
                hideLoadingBar()
            }
        }
    }


    private fun showLoadingBar() {
        binding.progressLoading.beVisible()
    }

    private fun hideLoadingBar() {
        binding.progressLoading.beGone()
    }

    private fun gotoMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        activity?.finish()
    }

    private fun gotoSignupActivity() {
        startActivity(Intent(activity, Signup2Activity::class.java))
        activity?.finish()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, birth: String, phone: String) {

        EarzoomLoginManager.instance?.prefUserBirth = birth
        EarzoomLoginManager.instance?.prefUserPhone = phone

        dialog.dismiss()

        /** 추가정보 확인후 회원 가입여부 확인 */
        viewModel.checkLoginInfo()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onDialogPrivacyClick(dialog: DialogFragment) {
        startActivity(Intent(activity, PrivacyPolicyActivity::class.java))
    }


    private var backKeyPressedTime: Long = 0
    private lateinit var toast: Toast

    override fun setBackPressed() {
        super.setBackPressed()

        // 백버튼 확인 후 종료
        toast = Toast.makeText(activity, getString(R.string.txt_press_back_button), Toast.LENGTH_SHORT)

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            toast.show()
            return
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel()
            requireActivity().finish()
            return
        }
    }
}

class LoginViewModelFactory(private val context: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            LoginViewModel(context) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}