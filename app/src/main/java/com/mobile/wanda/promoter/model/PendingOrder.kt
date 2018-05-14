package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class PendingOrder(

        @field:SerializedName("data")
        val details: OrderDetails? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @SerializedName("error")
        val error: Boolean? = null,

        @SerializedName("error_data")
        var errorData: ErrorData? = null
)

data class ErrorData(
        @SerializedName("message")
        var message: String? = null
)