/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.data.vo

import com.google.gson.annotations.SerializedName

/**
 * 프로토콜 변경
 * jhong
 * 2022.09.26
 *
 * {
"status": 200,
"data": {
"member": {
"id": "1980051501089461505",
"deviceToken": null,
"name": "홍진우",
"birth": "19800515",
"sex": "남성",
"addr": "서울특별시",
"phone": "010-8946-1505",
"memAuth": 0,
"status": "비장애인",
"statusLevel": null,
"organ": "",
"recomName": null,
"kind": null,
"roleName": null,
"snsFlag": 0,
"privacyAgree": 1,
"regDt": "2021-11-23 17:02:37",
"adFlag": null
}
},
"code": "정상"
}
 *
 */
data class LoginMemberVO (

    @JvmField
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("deviceToken")
    var deviceToken: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("birth")
    var birth: String? = null,

    @SerializedName("sex")
    var sex: String? = null,

    @SerializedName("addr")
    var addr: String? = null,

    @SerializedName("phone")
    var phone: String? = null,

    @SerializedName("memAuth")
    var memAuth: Int? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("statusLevel")
    var statusLevel: Int? = null,

    @SerializedName("organ")
    var organ: String? = null,

    @SerializedName("recomName")
    var recomName: String? = null,

    @SerializedName("kind")
    var kind: String? = null,

    @SerializedName("roleName")
    var roleName: String? = null,

    @SerializedName("snsFlag")
    var snsFlag: Int? = null,

    @SerializedName("privacyAgree")
    var privacyAgree: Int? = null,

    @SerializedName("regDt")
    var regDt: String? = null,

    @SerializedName("adFlag")
    var adFlag: String? = null,
)