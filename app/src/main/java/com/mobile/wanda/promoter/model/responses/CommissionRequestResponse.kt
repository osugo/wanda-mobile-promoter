package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 29/11/2017.
 */
data class CommissionRequestResponse(
        @SerializedName("success")
        val success: Boolean?,
        @SerializedName("error")
        val error: Boolean?,
        @SerializedName("message")
        val message: String?
)