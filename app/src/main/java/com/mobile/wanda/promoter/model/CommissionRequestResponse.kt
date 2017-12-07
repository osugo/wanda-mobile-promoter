package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 29/11/2017.
 */
data class CommissionRequestResponse(
        @SerializedName("success")
        val success: Boolean?,
        @SerializedName("error")
        val error: String?,
        @SerializedName("message")
        val message: String?
)