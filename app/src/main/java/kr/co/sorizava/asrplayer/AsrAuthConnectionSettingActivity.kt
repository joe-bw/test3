package kr.co.sorizava.asrplayer

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.sorizava.asrplayer.extension.appConfig
import org.mozilla.focus.R

class AsrAuthConnectionSettingActivity : BaseActivity() {

    val TAG = "AsrAuthConnectionSettingActivity"

    var editTextAuthTokenUrl: EditText? = null
    private  var editTextAsrServerUrl:EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asr_auth_connection_setting)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = resources.getString(R.string.setting_asr_server_auth_connection)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        editTextAuthTokenUrl = findViewById<View>(R.id.asr_auth_url) as EditText?
        editTextAsrServerUrl = findViewById<View>(R.id.asr_ws_url) as EditText
        editTextAuthTokenUrl!!.setText(appConfig.getPrefAuthTokenUrl())
        editTextAsrServerUrl!!.setText(appConfig.getPrefAuthTokenUrl())
    }

    private fun getAuthTokenUrl(): String {
        var text = editTextAuthTokenUrl!!.text.toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    private fun getAsrServerUrl(): String {
        var text: String = editTextAsrServerUrl!!.getText().toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    override fun onDestroy() {
        // close any value
        appConfig.setPrefAuthTokenUrl( getAuthTokenUrl())
        appConfig.setPrefAsrAuthUrl( getAsrServerUrl())
        super.onDestroy()
    }
}