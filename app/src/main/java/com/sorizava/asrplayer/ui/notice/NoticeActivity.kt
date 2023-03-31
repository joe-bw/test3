/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.ui.notice

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.webkit.WebSettings
import android.webkit.WebView
import org.mozilla.focus.R
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.view.WindowManager
import android.webkit.WebView.WebViewTransport
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sorizava.asrplayer.data.BoardType
import com.sorizava.asrplayer.extension.appConfig


const val ARGUMENT_IDX = "ARGUMENT_IDX"
const val ARGUMENT_URL = "ARGUMENT_URL"
/**
 * 공지사항 activity
 * 알림을 통해 화면에 진입할 때의 예외사항이 있다.
 * 로그인 정보가 없을 시 문제가 있을 수 있다.
 */
class NoticeActivity : AppCompatActivity() {
    private var context: Context? = null
    private var mWebSettings: WebSettings? = null
    private var webView: WebView? = null
    private val TAG_JAVA_INTERFACE = "SORIJAVA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)

        val argumentIdx = intent.getIntExtra(ARGUMENT_IDX, -1)
        val argumentUrl = intent.getStringExtra(ARGUMENT_URL)

        webView = findViewById(R.id.webViewNotice)

        when(argumentIdx) {
            BoardType.NOTICE.type -> {
                webView!!.loadUrl(appConfig.getPrefWebNoticeViewUrl()!!)
            }
            BoardType.FAQ.type -> {
                webView!!.loadUrl(appConfig.getPrefWebFaqViewUrl()!!)
            }
            BoardType.EVENT.type -> {
                webView!!.loadUrl(appConfig.getPrefWebEventViewUrl()!!)
            }
        }

        argumentUrl?.let {
            webView!!.loadUrl(argumentUrl)
        }

        context = this
        webView!!.webViewClient = WebViewClient() // 클릭시 새창 안뜨게
        mWebSettings = webView!!.getSettings() //세부 세팅 등록
        mWebSettings!!.javaScriptEnabled = true // 웹페이지 자바스클비트 허용 여부
        mWebSettings!!.setSupportMultipleWindows(false) // 새창 띄우기 허용 여부
        mWebSettings!!.javaScriptCanOpenWindowsAutomatically = false // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings!!.loadWithOverviewMode = true // 메타태그 허용 여부
        mWebSettings!!.useWideViewPort = true // 화면 사이즈 맞추기 허용 여부
        mWebSettings!!.setSupportZoom(false) // 화면 줌 허용 여부
        mWebSettings!!.builtInZoomControls = false // 화면 확대 축소 허용 여부
//        mWebSettings!!.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN // 컨텐츠 사이즈 맞추기
        mWebSettings!!.cacheMode = WebSettings.LOAD_NO_CACHE // 브라우저 캐시 허용 여부
        mWebSettings!!.domStorageEnabled = true // 로컬저장소 허용 여부
        webView!!.addJavascriptInterface(WebBridge(), TAG_JAVA_INTERFACE)
        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                bDialog: Boolean,
                userGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@NoticeActivity)
                val webSettings = newWebView.settings
                webSettings.javaScriptEnabled = true
                val dialog = Dialog(this@NoticeActivity)
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
    }

    internal inner class WebBridge {
        @JavascriptInterface
        fun callbackAndroid(result: Int) {
            runOnUiThread {
                if (result == 1) {
                    finish()
                } else {
                    Toast.makeText(
                        this@NoticeActivity,
                        getString(R.string.web_callback_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}