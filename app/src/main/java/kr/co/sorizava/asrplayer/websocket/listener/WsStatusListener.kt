package kr.co.sorizava.asrplayer.websocket.listener

interface WsStatusListener {
    fun onConnectionStatusChanged(status: Int)
    fun onMessageSubtitle(text: String?, isFinal: Boolean)
    fun onMessageSubtitle(text: String?)
    fun resetSubtitleView()
}