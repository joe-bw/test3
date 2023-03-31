package kr.co.sorizava.asrplayer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import com.sorizava.asrplayer.extension.getSharedPrefs
import org.mozilla.focus.R


class AppConfig(val context: Context){
    private val mSharedPref = context.getSharedPrefs()

    companion object
    {
        // 자막 위치
        const val SUBTITLE_POSITION_TOP = 0
        const val SUBTITLE_POSITION_BOTTOM = 1

        // 자막 폰트 크기
        const val SUBTITLE_FONT_SIZE_VERY_SMALL = 0
        const val SUBTITLE_FONT_SIZE_SMALL = 1
        const val SUBTITLE_FONT_SIZE_MEDIUM = 2
        const val SUBTITLE_FONT_SIZE_LARGE = 3
        const val SUBTITLE_FONT_SIZE_VERY_LARGE = 4

        // 자막 라인 수
        const val SUBTITLE_LINE_2 = 2
        const val SUBTITLE_LINE_3 = 3
        const val SUBTITLE_LINE_4 = 4

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: AppConfig? = null

        /**
         * Config instance
         *
         * @return  instance of this class.
         */
        fun getInstance(context: Context): AppConfig {
            return instance ?: synchronized(this) {
                instance ?: AppConfig(context).also {
                    instance = it
                }
            }
        }
    }

    var prefSubtitleOnOff: Boolean
        get() = mSharedPref.getBoolean("SP_KEY_SZ_SUBTITLE_ON", true)
        set(prefSubtitleOnOff) = mSharedPref.edit().putBoolean("SP_KEY_SZ_SUBTITLE_ON", prefSubtitleOnOff).apply()

    var prefSubtitleSync: Boolean
        get() = mSharedPref.getBoolean("SP_KEY_SZ_SUBTITLE_SYNC", true)
        set(prefSubtitleSync) = mSharedPref.edit().putBoolean("SP_KEY_SZ_SUBTITLE_SYNC", prefSubtitleSync).apply()

    var prefSubtitlePosition: Int
        get() = mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_POSITION", SUBTITLE_POSITION_BOTTOM)
        set(prefSubtitlePosition) = mSharedPref.edit().putInt("SP_KEY_SZ_SUBTITLE_POSITION", prefSubtitlePosition).apply()

    var prefSubtitleLine: Int
        get() = mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_LINE", SUBTITLE_LINE_3)
        set(prefSubtitleLine) = mSharedPref.edit().putInt("SP_KEY_SZ_SUBTITLE_LINE", prefSubtitleLine).apply()

    var prefSubtitleFont: Int
        get() = mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_FONT", 0)
        set(prefSubtitleFont) = mSharedPref.edit().putInt("SP_KEY_SZ_SUBTITLE_FONT", prefSubtitleFont).apply()

    var prefSubtitleFontSize: Int
        get() = mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_FONT_SIZE", 0)
        set(prefSubtitleFontSize) = mSharedPref.edit().putInt("SP_KEY_SZ_SUBTITLE_FONT_SIZE", prefSubtitleFontSize).apply()

    //20220922 cbw 화자분리 on off 저장하는 preference
    var prefSubtitleSpeakerOnOff: Boolean
        get() = mSharedPref.getBoolean("SP_KEY_SZ_SUBTITLE_SPEAKER_ON_OFF", true)
        set(prefSubtitleSpeakerOnOff) = mSharedPref.edit().putBoolean("SP_KEY_SZ_SUBTITLE_SPEAKER_ON_OFF", prefSubtitleSpeakerOnOff).apply()

    // get subtitle foreground color
    fun getPrefSubtitleForegroundColor(): Int {
        return mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_FOREGROUND_COLOR", Color.WHITE)
    }

    // set subtitle foreground color
    fun setPrefSubtitleForegroundColor(color: Int) {
        val edit = mSharedPref.edit()
        edit.putInt("SP_KEY_SZ_SUBTITLE_FOREGROUND_COLOR", color)
        edit.apply()
    }

    // get subtitle background transparency
    fun getPrefSubtitleTransparency(): Int {
        return mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_TRANSPARENCY", 50) // 0.5f (0-100:0~1)
    }

    // set subtitle background transparency
    fun setPrefSubtitleTransparency(i: Int) {
        val edit = mSharedPref.edit()
        edit.putInt("SP_KEY_SZ_SUBTITLE_TRANSPARENCY", i)
        edit.apply()
    }

    // get asr auth connection
    fun getPrefAsrAuthConnect(): Boolean {
        return mSharedPref.getBoolean("SP_KEY_ASR_AUTH_CONNECT", false)
    }

    // set asr auth connection
    fun setPrefAsrAuthConnect(z: Boolean) {
        val edit = mSharedPref.edit()
        edit.putBoolean("SP_KEY_ASR_AUTH_CONNECT", z)
        edit.apply()
    }

    // get asr connection use param project
    fun getPrefAsrUseParamProject(): Boolean {
        return mSharedPref.getBoolean("SP_KEY_ASR_USE_PARAM_PROJECT", true)
    }

    // set asr connection use param project
    fun setPrefAsrUseParamProject(z: Boolean) {
        val edit = mSharedPref.edit()
        edit.putBoolean("SP_KEY_ASR_USE_PARAM_PROJECT", z)
        edit.apply()
    }

    // get ASR server model
    fun getPrefAsrModel(): String {
        return mSharedPref.getString("SP_KEY_ASR_SERVER_MODEL", ZerothDefine.API_WWS_MODEL)!!
    }

    // set ASR server model
    fun setPrefAsrModel(name: String) {
        val edit = mSharedPref.edit()
        edit.putString("SP_KEY_ASR_SERVER_MODEL", name)
        edit.apply()
    }

    // get app key
    fun getPrefAppKey(): String {
        return mSharedPref.getString("SP_KEY_ASR_APP_KEY", ZerothDefine.API_APP_KEY)!!
    }

    // set app key
    fun setPrefAppKey(id: String) {
        val edit = mSharedPref.edit()
        edit.putString("SP_KEY_ASR_APP_KEY", id)
        edit.apply()
    }

    // get app secret
    fun getPrefAppSecret(): String {
        return mSharedPref.getString("SP_KEY_ASR_APP_SECRET", ZerothDefine.API_APP_SECRET)!!
    }

    // set app secret
    fun setPrefAppSecret(key: String) {
        val edit = mSharedPref.edit()
        edit.putString("SP_KEY_ASR_APP_SECRET", key)
        edit.apply()
    }

    // get ASR server url
    fun getPrefAsrUrl(): String {
        return mSharedPref.getString("SP_KEY_ASR_URL", ZerothDefine.API_WWS_URL)!!
    }

    // set ASR server url
    fun setPrefAsrUrl(url: String) {
        val edit = mSharedPref.edit()
        edit.putString("SP_KEY_ASR_URL", url)
        edit.apply()
    }

    // get ASR auth server url
    fun getPrefAsrAuthUrl(): String? {
        return mSharedPref.getString("SP_KEY_ASR_AUTH_URL", ZerothDefine.API_AUTH_WWS_URL)
    }

    // set ASR auth server url
    fun setPrefAsrAuthUrl(url: String?) {
        val edit = mSharedPref.edit()
        edit.putString("SP_KEY_ASR_AUTH_URL", url)
        edit.apply()
    }

    // get Auth token generate url
    fun getPrefAuthTokenUrl(): String? {
        return mSharedPref.getString("SP_KEY_AUTH_TOKEN_URL", ZerothDefine.API_AUTH_COMPLETE_URL)
    }

    // set Auth token generate url
    fun setPrefAuthTokenUrl(url: String?) {
        val edit = mSharedPref.edit()
        edit.putString("SP_KEY_AUTH_TOKEN_URL", url)
        edit.apply()
    }

    // convert  preference index value -> subtitle font size
    fun convertPrefSubtitleFontSize(i: Int): Int {
        return when (i) {
            SUBTITLE_FONT_SIZE_VERY_LARGE -> 28
            SUBTITLE_FONT_SIZE_LARGE -> 26
            SUBTITLE_FONT_SIZE_MEDIUM -> 24
            SUBTITLE_FONT_SIZE_SMALL -> 22
            SUBTITLE_FONT_SIZE_VERY_SMALL -> 20
            else -> 24
        }
    }

    // convert  preference index value -> subtitle line
    fun convertPrefSubtitleLine(i: Int): Int {
        return when (i) {
            SUBTITLE_LINE_2 -> 2
            SUBTITLE_LINE_3 -> 3
            SUBTITLE_LINE_4 -> 4
            else -> 3
        }
    }

    // convert  preference index value -> subtitle font path
    fun convertPrefSubtitleFontPath(context: Context, i: Int): String? {
        val fontPaths = context.resources.getStringArray(R.array.subtitle_font_list)
        when (i) {
            0 -> return ""
            1 -> return fontPaths[0]
            2 -> return fontPaths[1]
            3 -> return fontPaths[2]
            4 -> return fontPaths[3]
        }
        return null
    }

    // convert  preference index value -> subtitle font display name
    fun convertPrefSubtitleFontName(context: Context, i: Int): String? {
        val fontPaths = context.resources.getStringArray(R.array.subtitle_font_list)
        when (i) {
            0 -> return context.resources.getString(R.string.subtitle_font_0)
            1 -> return context.resources.getString(R.string.subtitle_font_1)
            2 -> return context.resources.getString(R.string.subtitle_font_2)
            3 -> return context.resources.getString(R.string.subtitle_font_3)
            4 -> return context.resources.getString(R.string.subtitle_font_4)
        }
        return null
    }

    // convert  preference index value -> subtitle font Typeface
    fun convertPrefSubtitleFontTypeface(context: Context, i: Int): Typeface? {
        val fontPath = convertPrefSubtitleFontPath(context, i)
        return if (fontPath != null && fontPath != "") {
            Typeface.createFromAsset(context.assets, fontPath)
        } else {
            Typeface.DEFAULT
        }
    }

    // convert  preference index value -> subtitle background transparency
    fun convertPrefSubtitleTransparency(i: Int): Float {
        //return if (i in 101 downTo -1) 0.5f else (100 - i).toFloat() / 100f
        return (100 - i).toFloat() / 100f
    }

    // Tutorial 체크
    var prefTutorialCheck: Boolean
        get() = mSharedPref.getBoolean("SP_KEY_TUTORIAL_CHECK", false)
        set(prefTutorialCheck) = mSharedPref.edit().putBoolean("SP_KEY_TUTORIAL_CHECK", prefTutorialCheck).apply()

    var prefShowTutorialDoubleCheck: Boolean
        get() = mSharedPref.getBoolean("SP_KEY_TUTORIAL_DOUBLE_CHECK", false)
        set(prefShowTutorialDoubleCheck) = mSharedPref.edit().putBoolean("SP_KEY_TUTORIAL_DOUBLE_CHECK", prefShowTutorialDoubleCheck).apply()

    // 통계 API pref 추가
    // jhong
    // since 210824
    // ################################################################################################
    private val PREF_KEY_START_TIME_SEQ = "PREF_KEY_START_TIME_SEQ"
    private val PREF_KEY_INIT_START_TIME_SEQ = "PREF_KEY_INIT_START_TIME_SEQ"
    private val PREF_KEY_START_URL = "PREF_KEY_START_URL"

    private val PREF_WEB_ADDINFO_URL = "PREF_WEB_ADDINFO_URL"
    private val PREF_WEB_NOTICEVIEW_URL = "PREF_WEB_NOTICEVIEW_URL"
    private val PREF_WEB_FAQVIEW_URL = "PREF_WEB_FAQVIEW_URL"
    private val PREF_WEB_EVENTVIEW_URL = "PREF_WEB_EVENTVIEW_URL"
    private val PREF_WEB_PRIVACY_URL = "PREF_WEB_PRIVACY_URL"

    fun clearPrefstatistics() {
        clearPrefStartTimeSeq()
        clearPrefStartURL()
        clearPrefInitStartTimeSeq()
        prefShowTutorialDoubleCheck = false
    }

    fun setPrefInitStartTimeSeq(seq: String?) {
        val edit = mSharedPref.edit()
        edit.putString(PREF_KEY_INIT_START_TIME_SEQ, seq)
        edit.apply()
    }

    fun getPrefInitStartTimeSeq(): String? {
        return mSharedPref.getString(PREF_KEY_INIT_START_TIME_SEQ, "")
    }

    private fun clearPrefInitStartTimeSeq() {
        val edit = mSharedPref.edit()
        edit.remove(PREF_KEY_INIT_START_TIME_SEQ)
        edit.apply()
    }

    fun setPrefStartTimeSeq(seq: String?) {
        val edit = mSharedPref.edit()
        edit.putString(PREF_KEY_START_TIME_SEQ, seq)
        edit.apply()
    }

    private fun clearPrefStartTimeSeq() {
        val edit = mSharedPref.edit()
        edit.remove(PREF_KEY_START_TIME_SEQ)
        edit.apply()
    }

    fun getPrefStartTimeSeq(): String? {
        return mSharedPref.getString(PREF_KEY_START_TIME_SEQ, "")
    }

    fun setPrefStartURL(url: String?) {
        val edit = mSharedPref.edit()
        edit.putString(PREF_KEY_START_URL, url)
        edit.apply()
    }

    fun getPrefStartURL(): String? {
        return mSharedPref.getString(PREF_KEY_START_URL, "")
    }

    private fun clearPrefStartURL() {
        val edit = mSharedPref.edit()
        edit.remove(PREF_KEY_START_URL)
        edit.apply()
    }

    /////////////////////////////// WEB INFO ///////////////////////////////
    // get Web server add info url
    fun getPrefWebAddInfoUrl(): String? {
        return mSharedPref.getString(PREF_WEB_ADDINFO_URL, ZerothDefine.API_WEB_URL_ADDINFO)
    }

    // set Web server add info url
    fun setPrefWebAddInfoUrl(url: String?) {
        val edit = mSharedPref.edit()
        edit.putString(PREF_WEB_ADDINFO_URL, url)
        edit.apply()
    }

    // get Web server add info url
    fun getPrefWebNoticeViewUrl(): String? {
        return mSharedPref.getString(PREF_WEB_NOTICEVIEW_URL, ZerothDefine.API_WEB_URL_NOTICEVIEW)
    }

    // get Web server faq view url
    fun getPrefWebFaqViewUrl(): String? {
        return mSharedPref.getString(PREF_WEB_FAQVIEW_URL, ZerothDefine.API_WEB_URL_FAQVIEW)
    }

    // get Web server event view url
    fun getPrefWebEventViewUrl(): String? {
        return mSharedPref.getString(PREF_WEB_EVENTVIEW_URL, ZerothDefine.API_WEB_URL_EVENTVIEW)
    }

    // set Web server add info url
    fun setPrefWebNoticeViewUrl(url: String?) {
        val edit = mSharedPref.edit()
        edit.putString(PREF_WEB_NOTICEVIEW_URL, url)
        edit.apply()
    }

    // get Web server privacy policy url
    fun getPrefWebViewPrivacyPolicyUrl(): String? {
        return mSharedPref.getString(PREF_WEB_PRIVACY_URL,
            ZerothDefine.API_WEB_URL_PRIVACY_POLICY)
    }

    // set Web server privacy policy url
    fun setPrefWebViewPrivacyPolicyUrl(url: String?) {
        val edit = mSharedPref.edit()
        edit.putString(PREF_WEB_PRIVACY_URL, url)
        edit.apply()
    }
}

