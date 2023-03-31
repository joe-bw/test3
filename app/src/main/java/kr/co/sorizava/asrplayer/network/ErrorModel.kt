package kr.co.sorizava.asrplayer.network

class ErrorModel (code: Int, message: String?, reason: String?) {

    var code = 0
    var message: String? = null
    var reason: String? = null

    init{
        this.code = code
        this.message = message
        this.reason = reason
    }
}