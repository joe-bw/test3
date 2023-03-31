package kr.co.sorizava.asrplayer.network

interface OnGetTokenListener {
    fun onGetToken(token: String?)
    fun onGetToken(oAuthToken: OAuthToken?)
    fun onFailed(error: ErrorModel?)
}