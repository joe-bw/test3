package kr.co.sorizava.asrplayer.network

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //     @GET("/token")
    companion object{
        var GRANT_TYPE = "client_credentials"
    }


    // @FormUrlEncoded
    // @POST("/auth/realms/zeroth/protocol/openid-connect/token")
    // Call<OAuthToken> getToken(@HeaderMap Map<String, String> headers, @Field("grant_type") String grant_type);

    // @FormUrlEncoded
    // @POST("/auth/realms/zeroth/protocol/openid-connect/token")
    // Call<OAuthToken> getToken(@HeaderMap Map<String, String> headers, @Field("grant_type") String grant_type);
    @FormUrlEncoded
    @POST
    fun getToken(
        @Url url: String?,
        @HeaderMap headers: Map<String?, String?>?,
        @Field("grant_type") grant_type: String?
    ): Call<OAuthToken?>?

}