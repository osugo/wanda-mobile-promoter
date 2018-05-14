package com.mobile.wanda.promoter.model.errors

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 14/05/2018.
 */
data class ErrorData(
        @SerializedName("message")
        var message: String? = null
)