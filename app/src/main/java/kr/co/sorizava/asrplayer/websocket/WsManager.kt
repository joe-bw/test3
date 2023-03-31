package kr.co.sorizava.asrplayer.websocket

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import com.sorizava.asrplayer.extension.appConfig
import kr.co.sorizava.asrplayer.AppConfig
import kr.co.sorizava.asrplayer.ZerothDefine
import kr.co.sorizava.asrplayer.media.Resampler
import kr.co.sorizava.asrplayer.network.*
import kr.co.sorizava.asrplayer.websocket.listener.WsStatusListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class WsManager : IWsManager {

    private val TAG = "WsManager"
    private val RECONNECT_INTERVAL = 3 * 1000 //Reconnect interval
    private val RECONNECT_MAX_TIME = (50 * 1000).toLong() //Reconnect max interval

    private var mActivity: Context? = null
    private var mWebSocket: WebSocket? = null
    private var mOkHttpClient: OkHttpClient? = null

    private var mCurrentStatus = WsStatus.DISCONNECTED //websocket Connection Status
    private var isNeedReconnect = false //Whether to automatically reconnect after disconnection
    private var isManualClose = false //Whether to manually close the websocket connection

    private var wsStatusListener: WsStatusListener? = null
    private var mLock: Lock? = null
    private val wsMainHandler = Handler(Looper.getMainLooper())
    private var reconnectCount = 0 //Number of reconnections

    /**
     * AccessToken 을 얻기 위한 retrofit Service
     */

    private var mWsServerUrl: String? = null
    private val channelConfig = String.format(ZerothDefine.OPT_16_KHZ, ZerothDefine.ZEROTH_MONO)

    private var mSourceSampleRate = 0 // source sample rate
    private var mSourceChannelCount = 2 // source channel count

    private val mTargetSampleRate = 16000 // target sample rate
    private val mTargetChannelCount = 1 // target channel count : AudioFormat.CHANNEL_IN_MONO;
    private var isStoreAudio = false
    var mResampler = Resampler()

    private val reconnectRunnable = Runnable { buildConnect() }

    constructor(builder : Builder)
    {
        this.isNeedReconnect = builder.needReconnect
        this.mLock = ReentrantLock()
    }

    companion object
    {
        var mWsManager: WsManager? = null
        var mApiService: ApiService? = null

        fun getInstance(): WsManager? {
            if (mWsManager == null) {
                mWsManager = Builder()
                    .needReconnect(true)
                    .build()
                mApiService = ApiManager.createServer()
            }
            return mWsManager
        }

        class Builder {
            internal var needReconnect = true
            fun needReconnect(`val`: Boolean): Builder {
                needReconnect = `val`
                return this
            }

            fun build(): WsManager {
                return WsManager(this)
            }
        }
    }

    private val mWebSocketListener: WebSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            Log.i(TAG, "WebSocket>>onMessage>>onOpen:" + response.code)
            mWebSocket = webSocket
            setCurrentStatus(WsStatus.CONNECTED)
            connected()
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.i(TAG, "WebSocket>>onMessage>>ByteString:");
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (wsStatusListener != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    wsMainHandler.post {
                        wsStatusListener!!.onMessageSubtitle(text)
                    }
                } else {
                    wsStatusListener!!.onMessageSubtitle(text)
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket>>onClosing: $code / $reason")
            tryReconnect()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket>>onClosed : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            val code = response?.code ?: WsStatus.ERROR_SOCKET_FAIL
            Log.e(TAG, "WebSocket>>Fail: " + GsonManager.toJson(ErrorModel(code, t.message, t.localizedMessage)))
            tryReconnect()
        }
    }

    private fun initWebSocket() {
        if (mActivity?.appConfig?.getPrefAsrAuthConnect()!!) {
            getToken(object : OnGetTokenListener {

                override fun onGetToken(token: String?) {

                    //화자 분리 연결 세팅 20220928 cbw 시작
                    //if( mActivity?.appConfig?.prefSubtitleSpeakerOnOff == null ) mActivity?.appConfig?.prefSubtitleSpeakerOnOff = true
                    var num_speaker : String  = "-1"
                    if( mActivity?.appConfig?.prefSubtitleSpeakerOnOff == false) num_speaker = "-1"
                    else num_speaker = "999"
                    Log.d(TAG, "num_speaker:$num_speaker")

                    mWsServerUrl = createAuthWWSUrl_SeparateSpeaker(
                        false,
                        token!!,
                        mActivity?.appConfig?.getPrefAsrModel()!! ,
                        channelConfig ,
                        num_speaker
                    )
                    //화자 분리 연결 세팅 20220928 cbw 끝

                    if (mOkHttpClient == null) {
                        mOkHttpClient = OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .build()
                    }

                    mOkHttpClient!!.dispatcher.cancelAll()
                    try {
                        mLock!!.lockInterruptibly()
                        try {
                            val request = Request.Builder().url(mWsServerUrl!!).build()
                            mOkHttpClient!!.newWebSocket(request, mWebSocketListener)
                        } finally {
                            mLock!!.unlock()
                        }
                    } catch (e: InterruptedException) {
                    }
                }

                override fun onGetToken(oAuthToken: OAuthToken?) {
                }

                override fun onFailed(error: ErrorModel?) {
                }
            })
        } else {

            //화자 분리 연결 세팅 20220928 cbw 시작
            //if( mActivity?.appConfig?.prefSubtitleSpeakerOnOff == null ) mActivity?.appConfig?.prefSubtitleSpeakerOnOff = true
            var num_speaker : String  = "-1"
            if( mActivity?.appConfig?.prefSubtitleSpeakerOnOff == false) num_speaker = "-1"
            else num_speaker = "999"
            Log.d(TAG, "num_speaker:$num_speaker")

            mWsServerUrl = if (mActivity?.appConfig?.getPrefAsrUseParamProject()!!) {
                createWWSUrl_SeparateSpeaker(
                    false,
                    mActivity?.appConfig?.getPrefAppKey()!! ,
                    mActivity?.appConfig?.getPrefAsrModel()!!,
                    channelConfig,
                    num_speaker
                )

            } else {
                //화자 분리 연결 세팅 20220928 cbw
                //if( mActivity?.appConfig?.prefSubtitleSpeakerOnOff == null ) mActivity?.appConfig?.prefSubtitleSpeakerOnOff = true
                var num_speaker: String
                if( mActivity?.appConfig?.prefSubtitleSpeakerOnOff == false) num_speaker = "-1"
                else num_speaker = "999"
                Log.d(TAG, "num_speaker:$num_speaker")

                createWWSUrl_SeparateSpeaker(false, mActivity?.appConfig?.getPrefAsrModel()!!, channelConfig, num_speaker)
            }
            //화자 분리 연결 세팅 20220928 cbw 끝

            if (mOkHttpClient == null) {
                mOkHttpClient = OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .build()
            }

            mOkHttpClient!!.dispatcher.cancelAll()
            try {
                mLock!!.lockInterruptibly()
                try {
                    val request = Request.Builder().url(mWsServerUrl!!).build()
                    mOkHttpClient!!.newWebSocket(request, mWebSocketListener)
                } finally {
                    mLock!!.unlock()
                }
            } catch (e: InterruptedException) {
            }
        }
    }

    override fun getWebSocket(): WebSocket? {
        return mWebSocket
    }

    fun setListener(activity: Activity?, wsStatusListener: WsStatusListener?): WsManager {
        this.mActivity = activity
        this.wsStatusListener = wsStatusListener
        return this
    }

    @Synchronized
    override fun isWsConnected(): Boolean {
        return mCurrentStatus == WsStatus.CONNECTED
    }

    @Synchronized
    override fun getCurrentStatus(): Int {
        return mCurrentStatus
    }

    @Synchronized
    override fun setCurrentStatus(currentStatus: Int) {
        mCurrentStatus = currentStatus
    }

    override fun startConnect(): WsManager {
        Log.d(TAG, "startConnect()")
        isManualClose = false
        if (mCurrentStatus == WsStatus.CONNECTED) {
            Log.d(TAG, "startConnect() already connected")
        } else {
            buildConnect()
        }
        return this
    }

    override fun stopConnect() {
        Log.d(TAG, "stopConnect()")
        isManualClose = true
        disconnect()
    }
    //화자분리 변경을 한후 다시 연결을 맺기위한 disconnect
    fun reconnect() {
        Log.d(TAG, "reconnect()")
        disconnect()
    }

    fun stopEOS() {
        isStoreAudio = true
        sendMessage("\'EOS\'")
    }

    private fun tryReconnect() {
        Log.d(TAG, "tryReconnect()")
        if (!isNeedReconnect or isManualClose) {
            Log.d(TAG, "pass tryReconnect")
            return
        }

        setCurrentStatus(WsStatus.RECONNECT)
        val delay = reconnectCount.toLong() * RECONNECT_INTERVAL
        wsMainHandler
            .postDelayed(reconnectRunnable, Math.min(delay, RECONNECT_MAX_TIME))
        reconnectCount++
    }

    private fun cancelReconnect() {
        Log.d(TAG, "cancelReconnect()")
        wsMainHandler.removeCallbacks(reconnectRunnable)
        reconnectCount = 0
    }

    private fun connected() {
        cancelReconnect()
    }

    private fun disconnect() {
        Log.d(TAG, "disconnect()")
        if (mCurrentStatus == WsStatus.DISCONNECTED) {
            return
        }
        cancelReconnect()
        /** 2021.12.23
         * EOS not request
         * 서버 개발자(?)의 요청에 따라 주석 처리
         */
//        stopEOS();
        if (mOkHttpClient != null) {
            mOkHttpClient!!.dispatcher.cancelAll()
        }
        if (mWebSocket != null) {
            val isClosed = mWebSocket!!.close( CODE.NORMAL_CLOSE, TIP.NORMAL_CLOSE)
            //Close connection abnormally
            if (!isClosed) {
                Log.d(TAG, "disconnect() " + TIP.ABNORMAL_CLOSE)
            }
        }
        setCurrentStatus(WsStatus.DISCONNECTED)
    }

    @Synchronized
    private fun buildConnect() {
        Log.d(TAG, "buildConnect()")
        if (!isNetworkConnected(mActivity)) {
            setCurrentStatus(WsStatus.DISCONNECTED)
            tryReconnect()
            return
        }
        when (getCurrentStatus()) {
            WsStatus.CONNECTED, WsStatus.CONNECTING -> {}
            else -> {
                setCurrentStatus(WsStatus.CONNECTING)
                initWebSocket()
            }
        }
    }

    //Send a message
    override fun sendMessage(msg: String?): Boolean {
        //return false
        return send(msg)
    }

    override fun sendMessage(byteString: ByteString?): Boolean {
        return send(byteString)
    }

    private fun send(msg: Any?): Boolean {
        var isSend = false
        if (mWebSocket != null && mCurrentStatus == WsStatus.CONNECTED) {
            if (msg is String) {
                isSend = mWebSocket!!.send(msg)
            } else if (msg is ByteString) {
                isSend = mWebSocket!!.send(msg)
            }
            //Failed to send message, try to reconnect
            if (!isSend) {
                tryReconnect()
            }
        }
        return isSend
    }

    //Check if the network is connected
    private fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager
                .activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }

    //화자 분리 연결 세팅 20220928 cbw 추가(_SeparateSpeaker)
    //const val API_WWS_PARAM_WITH_PROJECT = "?single=%s&project=%s&model=%s&content-type=%s&num-speaker=999"     //테스트용
    private fun createAuthWWSUrl_SeparateSpeaker(
        single: Boolean,
        accessToken: String,
        model: String,
        contentType: String,
        num_speaker: String
    ): String {
        val zerothUrl = mActivity?.appConfig?.getPrefAsrAuthUrl() + ZerothDefine.API_AUTH_WWS_PARAM_SEPARATESPEAKER
        return String.format(
            zerothUrl, single.toString(),
            accessToken,
            model,
            contentType,
            num_speaker
        )
    }

    private fun createWWSUrl_SeparateSpeaker(
        single: Boolean,
        appKey: String,
        model: String,
        contentType: String,
        num_speaker: String
    ): String {
        val zerothUrl = mActivity?.appConfig?.getPrefAsrUrl() + ZerothDefine.API_WWS_PARAM_WITH_PROJECT_SEPARATESPEAKER
        return String.format(
            zerothUrl, single.toString(),
            appKey,
            model,
            contentType,
            num_speaker
        )
    }
    private fun createWWSUrl_SeparateSpeaker(
        single: Boolean,
        model: String,
        contentType: String,
        num_speaker: String
    ): String {
        val zerothUrl = mActivity?.appConfig?.getPrefAsrUrl() + ZerothDefine.API_WWS_PARAM_SEPARATESPEAKER
        return String.format(
            zerothUrl, single.toString(),
            model,
            contentType,
            num_speaker
        )
    }

    /**
     * Asynchronously send the request
     */
    private fun getToken(onGetTokenListener: OnGetTokenListener) {
        Log.i(TAG, "getToken")

        val headerMap = HashMap<String?, String?>()
        val authorizationCredentials =
            mActivity?.appConfig?.getPrefAppKey() + ":" + mActivity?.appConfig?.getPrefAppSecret()
        val flags = Base64.NO_WRAP or Base64.URL_SAFE
        val encodedString = Base64.encode(authorizationCredentials.toByteArray(), flags)
        val basicAuth = "Basic " + String(encodedString)
        headerMap["Content-Type"] = "application/x-www-form-urlencoded"
        headerMap["Authorization"] = basicAuth

        mApiService!!.getToken(
            mActivity?.appConfig?.getPrefAuthTokenUrl(),
            headerMap,
            ApiService.GRANT_TYPE
        )?.enqueue(object : Callback<OAuthToken?> {

            override fun onResponse(call: Call<OAuthToken?>, response: Response<OAuthToken?>) {
                if (response.isSuccessful) {
                    val accessToken = response.body()!!.access_token
                    onGetTokenListener.onGetToken(accessToken)
                } else {
                    onGetTokenListener.onFailed(
                        ErrorModel(
                            response.code(),
                            response.message(),
                            ""
                        )
                    )
                }
            }

            override fun onFailure(call: Call<OAuthToken?>, t: Throwable) {
                onGetTokenListener.onFailed(
                    ErrorModel(
                        ZerothDefine.ERROR_GET_TOKEN_FAIL,
                        t.message,
                        t.localizedMessage
                    )
                )
                Log.i(TAG, "getToken fail")
            }
        })
    }

    /**
     * method for configuration audio format
     *
     * @param sampleRate   sampling rate
     * @param channelCount channel count
     */
    fun configure(sampleRate: Int, channelCount: Int) {
        mSourceChannelCount = channelCount
        mSourceSampleRate = sampleRate
        Log.d(TAG, "configure sampleRate: $sampleRate channelCount: $channelCount")
    }

    private var asr_mono_buffer = ByteArray(8192) // 8192 max mono buffer size

    private var asr_stored_buffer = ByteArray(8192 * 8 * 4) // 4sec

    private var asr_stored_buffer_offset = 0
    private var asr_stored_buffer_length = 0

    // ex : 44100 * 16 * 2
    // = 1,411,200 bits / sec
    // = 176,400 bytes / sec

    // ex : 44100 * 16 * 2
    // = 1,411,200 bits / sec
    // = 176,400 bytes / sec
    // 16000 * 16 * 1 = 16000
    var zeroPcmBuffer = ByteArray(8192) // 4번이면 32000

    /**
     * callback for audio decoder
     *
     * @param buffer                   pcm buffer
     * @param length                   pcm buffer length
     */
    fun writeBuffer(buffer: ByteArray, length: Int) {

        if (mWebSocket == null || mCurrentStatus != WsStatus.CONNECTED) {
            if (isStoreAudio) {
                asr_stored_buffer_length = length
                if (asr_stored_buffer_offset + length < 262144) {
                    System.arraycopy(buffer, 0, asr_stored_buffer, asr_stored_buffer_offset, length)
                    asr_stored_buffer_offset += length
                }
            }
            return
        } else {
            if (isStoreAudio) {
                sendStoredBuffer()
                isStoreAudio = false
                asr_stored_buffer_offset = 0
            }
        }
        // mono convert
        if (mSourceChannelCount == 2 && mTargetChannelCount == 1) {
            val mono_buffer_length = length / 2
            // mono convert
            var i = 0
            while (i < mono_buffer_length) {
                asr_mono_buffer[i] = buffer[2 * i]
                asr_mono_buffer[i + 1] = buffer[2 * i + 1]
                i += 2
            }

            // resampling : 16bit 다운 셈플링
            val resampledPcmData = mResampler.reSample(
                asr_mono_buffer,
                mono_buffer_length,
                16,
                mSourceSampleRate,
                mTargetSampleRate
            )

            //Log.e("writeBuffer3:", ByteString.of(*resampledPcmData!!).toString())
            try {
                sendMessage(ByteString.of(*resampledPcmData))
            } catch (e: Exception) {
                Log.e(TAG, "sendMessage Responding failed", e)
            }
        }
    }

    fun requsetFinalTranscript() {
        Arrays.fill(zeroPcmBuffer, 0.toByte())
        for (i in 0..2) {
            try {
                sendMessage(ByteString.of(*zeroPcmBuffer))
            } catch (e: Exception) {
                Log.e(TAG, "sendMessage Responding failed", e)
            }
        }
        wsStatusListener?.resetSubtitleView()
    }

    private fun sendStoredBuffer() {
        if (asr_stored_buffer_length == 0 || asr_stored_buffer_offset == 0) return
        val buffer_length = asr_stored_buffer_offset / asr_stored_buffer_length
        val length = asr_stored_buffer_length
        for (j in 0 until buffer_length) {
            if (mSourceChannelCount == 2 && mTargetChannelCount == 1) {
                val mono_buffer_length = length / 2
                // mono convert
                var i = 0
                while (i < mono_buffer_length) {
                    asr_mono_buffer[i] = asr_stored_buffer[asr_stored_buffer_length * j + 2 * i]
                    asr_mono_buffer[i + 1] =
                        asr_stored_buffer[asr_stored_buffer_length * j + 2 * i + 1]
                    i += 2
                }

                // resampling : 16bit 다운 셈플링
                val resampledPcmData : ByteArray? = mResampler.reSample(
                    asr_mono_buffer,
                    mono_buffer_length,
                    16,
                    mSourceSampleRate,
                    mTargetSampleRate
                )
                try {
                    sendMessage(ByteString.of(*resampledPcmData!!))
                } catch (e: Exception) {
                    Log.e(TAG, "sendMessage Responding failed", e)
                }
            }
        }
    }
}
