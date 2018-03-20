package com.mobile.wanda.promoter.model.errors

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 19/03/2018.
 */
data class LoginError(@SerializedName("error") var error: String?, @SerializedName("message") var message: String?)