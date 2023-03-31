package kr.co.sorizava.asrplayer.websocket

import okhttp3.WebSocket
import okio.ByteString

interface IWsManager {

    fun getWebSocket(): WebSocket?

    fun startConnect(): WsManager

    fun stopConnect()

    fun isWsConnected(): Boolean

    fun getCurrentStatus(): Int

    fun setCurrentStatus(currentStatus: Int)

    fun sendMessage(msg: String?): Boolean

    fun sendMessage(byteString: ByteString?): Boolean
}