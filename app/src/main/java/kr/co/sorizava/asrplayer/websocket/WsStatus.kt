package kr.co.sorizava.asrplayer.websocket

class WsStatus {
    companion object
    {
        val CONNECTED = 1
        val CONNECTING = 0
        val RECONNECT = 2
        val DISCONNECTED = -1

        val ERROR_SOCKET_FAIL = -1

        class CODE {
            val NORMAL_CLOSE = 1000
            val ABNORMAL_CLOSE = 1001
        }

        class TIP {
            val NORMAL_CLOSE = "normal close"
            val ABNORMAL_CLOSE = "abnormal close"
        }
    }

}

class CODE {
    companion object {
        const val NORMAL_CLOSE = 1000
        const val ABNORMAL_CLOSE = 1001
    }
}

class TIP {
    companion object {
        val NORMAL_CLOSE = "normal close"
        val ABNORMAL_CLOSE = "abnormal close"
    }
}