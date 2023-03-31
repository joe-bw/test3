/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.sorizava.asrplayer.extension.appConfig
import com.sorizava.asrplayer.extension.config
import com.sorizava.asrplayer.ui.intro.IntroActivity
import org.mozilla.focus.activity.MainActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 시작 통계 삭제
        appConfig.clearPrefstatistics()

        startActivity(Intent(this, IntroActivity::class.java))
        //startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}