/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.privacy;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.focus.R;

import kr.co.sorizava.asrplayer.AppConfig;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private Context context;
    private WebSettings mWebSettings; //웹뷰세팅
    private WebView webView;

    private String TAG_JAVA_INTERFACE =  "SORIJAVA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        webView = findViewById(R.id.webViewPrivacyPolicy);
        context = this;

        webView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        mWebSettings = webView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        webView.addJavascriptInterface(new WebBridge(),TAG_JAVA_INTERFACE);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(final WebView view, boolean bDialog, boolean userGesture, Message resultMsg)
            {

                WebView newWebView = new WebView(PrivacyPolicyActivity.this);
                WebSettings webSettings = newWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                final Dialog dialog = new Dialog(PrivacyPolicyActivity.this);
                dialog.setContentView(newWebView);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;

                dialog.show();
                Window window = dialog.getWindow();
                window.setAttributes(lp);

                newWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onCloseWindow(WebView window) {
                        dialog.dismiss();
                    }
                });
                ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
                resultMsg.sendToTarget();

                return true;
            }
        });

        webView.loadUrl(AppConfig.Companion.getInstance(context).getPrefWebViewPrivacyPolicyUrl());
    }

    class WebBridge{
        @JavascriptInterface
        public void callbackAndroid(final int result){
            runOnUiThread(() -> {

                if (result == 1) {
                    finish();
                }
            });
        }
    }
}