package kr.co.sorizava.asrplayer

object ZerothDefine {

    const val API_AUTH_COMPLETE_URL =
        "http://ailab.sorizava.co.kr:9080/auth/realms/zeroth/protocol/openid-connect/token"
    const val API_AUTH_URL = "http://ailab.sorizava.co.kr:9080/"

    const val API_APP_KEY = "6bbb73c5-ce84-42e5-a5e7-3fcc9e4d050c"
    const val API_APP_SECRET = "ee01365ee13a407593d2"

    /**
     * FCM 구독명
     * 개발 구독명 - devEarzoom
     *
     * 운영 구독명 - earzoom
     */
    //    public static final String FCM_SUBSCRIBE_NAME =  "devEarzoom";
    const val FCM_SUBSCRIBE_NAME = "earzoom"

    /**
     * 웹서버 URL
     * 개발 서버 - http://211.248.153.107:1969/ -----> http://222.99.52.79:1969/
     *
     * 실제 운영서버 - https://api.earzoom.kr:8443/
     * 211021
     */
    const val BASE_URL = "http://222.99.52.79:1969/"
//    public static final String BASE_URL = "https://api.earzoom.kr:8443/";

    //    public static final String BASE_URL = "https://api.earzoom.kr:8443/";
    /**
     * 웹서비스 URL 주소
     * 신규등록
     * jhong
     * 211006
     *
     * addInfo -> privacyAgree 로 변경
     * 211021
     */
    const val API_WEB_URL_ADDINFO = BASE_URL + "privacyAgree/"

    /**
     * 웹서비스 URL 주소
     * Notice
     * jhong
     * 211006
     */
    const val API_WEB_URL_NOTICEVIEW = BASE_URL + "noticeView/"

    /**
     * 웹서비스 URL 주소
     * FAQ
     * jhong
     * 220803
     */
    const val API_WEB_URL_FAQVIEW = BASE_URL + "faqView/"

    /**
     * 웹서비스 URL 주소
     * 이벤트
     * jhong
     * 220803
     */
    const val API_WEB_URL_EVENTVIEW = BASE_URL + "eventView/"

    /**
     * 웹서비스 URL 주소
     * Privacy Policy 개인정보처리
     * jhong
     * 211006
     */
    const val API_WEB_URL_PRIVACY_POLICY = BASE_URL + "privacyPolicy/"


    /** 2021.11.10
     * Banner URL
     * jhong
     */
    const val API_WEB_URL_BANNER = "http://datain.co.kr/reply/survey.html?idx=2453"


    /**
     * jhong
     * 211006
     * model >> KOREAN_16K
     * app key 와 app secret 는 사용하지 않습니다.
     * Use project parameter >> OFF
     * ASR Server EndPoint URL >> ws://45.115.152.123:3179/client/ws/speech
     */
    const val API_WWS_MODEL = "KOREAN_16K"
    //val API_WWS_MODEL = "KOREAN_ONLINE_16K" //20220816 cbw
//    const val API_WWS_MODEL = "KOREAN_ONLINE_16K" //20220913 cbw변경  화자분리 내부서버 테스트


    /**
     * jhong
     * 220526
     * TTS 엔진 서버 관리
     * 소리자바 서버 URL
     *
     * */

    /**
     * jhong
     * 220526
     * TTS 엔진 서버 관리
     * 소리자바 서버 URL
     *
     */
    /** 소리자바 서버 URL  */
    //const val API_WWS_URL = "ws://ailab.sorizava.co.kr:3179/client/ws/speech"
    const val API_WWS_URL = "ws://222.235.220.206:5013/client/ws/speech"

    /** 신규 배포 URL  */ //    public static final String API_WWS_URL = "ws://45.115.152.123:3179/client/ws/speech";
    //const val API_AUTH_WWS_URL = "ws://ailab.sorizava.co.kr:3179/client/ws/trusted"
    const val API_AUTH_WWS_URL = "ws://222.235.220.206:5013/client/ws/trusted"

    const val API_WWS_PARAM_SEPARATESPEAKER = "?single=%s&model=%s&content-type=%s&num-speaker=%s"                              //화자분리용 num-speaker 추가 20220928 cbw
    const val API_WWS_PARAM_WITH_PROJECT_SEPARATESPEAKER = "?single=%s&project=%s&model=%s&content-type=%s&num-speaker=%s"     //화자분리용 num-speaker 추가 20220928 cbw
    const val API_AUTH_WWS_PARAM_SEPARATESPEAKER = "?single=%s&access-token=%s&model=%s&content-type=%s"

    const val OPT_16_KHZ =
        "audio/x-raw,+layout=(string)interleaved,+rate=(int)16000,+format=(string)S16LE,+channels=(int)%d"
    const val OPT_44_KHZ =
        "audio/x-raw,+layout=(string)interleaved,+rate=(int)44100,+format=(string)S16LE,+channels=(int)%d"
    const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val DATE_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"

    const val ZEROTH_LANG_KOR = "kor"
    const val ZEROTH_LANG_ENG = "eng"

    const val ZEROTH_RATE_16 = 16000
    const val ZEROTH_RATE_44 = 44100

    const val ZEROTH_MONO = 1
    const val ZEROTH_STEREO = 2

    const val ERROR_GET_TOKEN_FAIL = 1000
    const val ERROR_SOCKET_FAIL = 1001

    const val REQUEST_PERMISSIONS_RECORD_AUDIO = 1
}
