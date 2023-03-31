/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.ui.signup

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sorizava.asrplayer.config.EarzoomLoginManager
import com.sorizava.asrplayer.config.LOGIN_TYPE_RELOGIN
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.network.AppApiClient.apiService
import com.sorizava.asrplayer.network.AppApiResponse
import com.sorizava.asrplayer.ui.login.LoginActivity
import kr.co.sorizava.asrplayer.AppConfig
import org.mozilla.focus.R
import org.mozilla.focus.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 소리자바 서버로의 고객 정보 신규 등록 화면
 * 신규등록 API로 ID, snsFlag, deviceToken 정보를 전달한다.
 * snsFlag 값 정의
 * 1 : naver, 2: kakao, 3: faceBook, 4: google
 */
class Signup2Activity : AppCompatActivity() {
    private var webView: WebView? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)

        val birth = EarzoomLoginManager.instance!!.prefUserBirth
        val phone = EarzoomLoginManager.instance!!.prefUserPhone
        val adFlag = "a"

        webView = findViewById(R.id.webView)
        webView!!.webViewClient = WebViewClient() // 클릭시 새창 안뜨게
        //웹뷰세팅
        val mWebSettings = webView!!.getSettings() //세부 세팅 등록
        mWebSettings.javaScriptEnabled = true // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false) // 새창 띄우기 허용 여부
        mWebSettings.javaScriptCanOpenWindowsAutomatically = false // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.loadWithOverviewMode = true // 메타태그 허용 여부
        mWebSettings.useWideViewPort = true // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false) // 화면 줌 허용 여부
        mWebSettings.builtInZoomControls = false // 화면 확대 축소 허용 여부
        mWebSettings.cacheMode = WebSettings.LOAD_NO_CACHE // 브라우저 캐시 허용 여부
        mWebSettings.domStorageEnabled = true // 로컬저장소 허용 여부
        val TAG_JAVA_INTERFACE = "SORIJAVA"
        webView!!.addJavascriptInterface(WebBridge(), TAG_JAVA_INTERFACE)
        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                bDialog: Boolean,
                userGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@Signup2Activity)
                val webSettings = newWebView.settings
                webSettings.javaScriptEnabled = true
                val dialog = Dialog(this@Signup2Activity)
                dialog.setContentView(newWebView)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window!!.attributes)
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                lp.height = WindowManager.LayoutParams.MATCH_PARENT
                dialog.show()
                val window = dialog.window
                window!!.attributes = lp
                newWebView.webChromeClient = object : WebChromeClient() {
                    override fun onCloseWindow(window: WebView) {
                        dialog.dismiss()
                    }
                }
                (resultMsg.obj as WebViewTransport).webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
        AppConfig.getInstance(applicationContext).getPrefWebAddInfoUrl()
            ?.let { webView!!.loadUrl(it) }

        val delayTime = 3000L
        Handler(Looper.getMainLooper()).postDelayed({
            webView!!.loadUrl(
                "javascript:addInfo_script.parameter" +
                        "('" + birth + "', '" + phone + "', '" + adFlag + "')"
            )
        }, delayTime)
    }

    internal inner class WebBridge {
        @JavascriptInterface
        fun callbackAndroid(result: Int) {
            runOnUiThread {
                when (result) {
                    1 -> {
                        callMemberInfo()
                    }
                    2 -> {
                        reLogin()
                    }
                    else -> {
                        Toast.makeText(
                            this@Signup2Activity,
                            getString(R.string.web_callback_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.txt_notice))
            .setMessage(getString(R.string.txt_backpress_contents))
            .setPositiveButton(getString(R.string.txt_exit)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton(getString(R.string.txt_continue)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun reLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .putExtra(LOGIN_TYPE_RELOGIN, true)
        )
        finish()
    }

    private val TAG = "TEST"

    /** 2021.10.31 회원 가입 여부 확인, 가입이 되어 있다면 그대로 앱 사용, 가입이 되어 있지 않다면 회원 가입 웹뷰 요청  */
    private fun callMemberInfo() {
        val birth = EarzoomLoginManager.instance!!.prefUserBirth
        val phone = EarzoomLoginManager.instance!!.prefUserPhone
        val request = LoginNewRequest(birth!!, phone!!)
        val call = apiService.requestMemberInfo(request)
        call.enqueue(object : Callback<AppApiResponse<LoginDataVO>> {
            override fun onResponse(
                call: Call<AppApiResponse<LoginDataVO>>,
                response: Response<AppApiResponse<LoginDataVO>>
            ) {
                Log.d(TAG, "response.code(): " + response.code())
                if (response.isSuccessful) {
                    Log.d(TAG, "isSuccessful")
                    val result: AppApiResponse<*> = response.body()!!
                    Log.d(TAG, "onResponse - result: $result")
                    if (result.status == 200) {
                        val data = result.data as LoginDataVO?
                        val member = data!!.member
                        EarzoomLoginManager.instance!!.prefUserId = member!!.id
                        gotoMainActivity()
                    } else {
                        reLogin()
                    }
                } else {
                    Log.d(TAG, "fail")
                    reLogin()
                }
            }

            override fun onFailure(call: Call<AppApiResponse<LoginDataVO>>, t: Throwable) {
                Log.d(TAG, "onFailure - result: " + t.message)
            }
        })
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this@Signup2Activity, MainActivity::class.java))
        finish()
    }
}