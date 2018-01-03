package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 27/11/2017.
 */
data class LoginCredentials(
        @SerializedName("grant_type")
        val grantType: String,
        @SerializedName("client_id")
        val clientId: String,
        @SerializedName("client_secret")
        val clientSecret: String,
        @SerializedName("username")
        val username: String,
        @SerializedName("password")
        val password: String
)