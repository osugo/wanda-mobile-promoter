package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 23/11/2017.
 */
data class AuthCredentials(
        @SerializedName("token_type")
        val tokenType: String?,
        @SerializedName("expires_in")
        val expiresIn: Long?,
        @SerializedName("access_token")
        val accessToken: String?,
        @SerializedName("refresh_token")
        val refreshToken: String?)