package kr.co.sorizava.asrplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import com.sorizava.asrplayer.extension.appConfig

import org.mozilla.focus.R

class AsrConfigActivity : BaseActivity() , View.OnClickListener {

    val TAG = "AsrConfigActivity"

    var editTextModel: EditText? = null
    var editTextAppKey: EditText? = null
    private  var editTextAppSecret:EditText? = null


    var mAuthSettingGroup: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asr_config)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = resources.getString(R.string.preference_category_asr_server)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        editTextModel = findViewById<View>(R.id.asr_model) as EditText?
        editTextModel!!.setText(appConfig.getPrefAsrModel())
        editTextAppKey = findViewById<View>(R.id.asr_app_key) as EditText?
        editTextAppSecret = findViewById<View>(R.id.asr_app_secret) as EditText
        editTextAppKey!!.setText(appConfig.getPrefAppKey())
        editTextAppSecret!!.setText(appConfig.getPrefAppSecret())

        //재시청시 재생 시점
        mAuthSettingGroup =
            findViewById<View>(R.id.radio_group_asr_server_connection) as RadioGroup?
        mAuthSettingGroup!!.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.setting_asr_auth_off -> appConfig.setPrefAsrAuthConnect(false)
                R.id.setting_asr_auth_on -> appConfig.setPrefAsrAuthConnect(true)
            }
        }
        (mAuthSettingGroup!!.getChildAt(if (appConfig.getPrefAsrAuthConnect()) 1 else 0) as RadioButton).isChecked =
            true
        findViewById<View>(R.id.asr_server_connection_edit_setting_btn).setOnClickListener(this)
        findViewById<View>(R.id.asr_server_auth_connection_edit_setting_btn).setOnClickListener(this)
    }

    private fun getModel(): String {
        var text = editTextModel!!.text.toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }


    private fun getAppKey(): String {
        var text = editTextAppKey!!.text.toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    private fun getAppSecret(): String {
        var text: String = editTextAppSecret!!.text.toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    override fun onDestroy() {
        // close any value
        getModel().let { appConfig.setPrefAsrModel(it) }
        getAppKey().let { appConfig.setPrefAppKey(it) }
        getAppSecret().let { appConfig.setPrefAppSecret(it) }
        super.onDestroy()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.asr_server_connection_edit_setting_btn -> startActivity(
                Intent(
                    this,
                    AsrConnectionSettingActivity::class.java
                )
            )
            R.id.asr_server_auth_connection_edit_setting_btn -> startActivity(
                Intent(
                    this,
                    AsrAuthConnectionSettingActivity::class.java
                )
            )
        }
    }
}
