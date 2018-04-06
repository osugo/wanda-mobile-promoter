package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class PendingOrder(

        @field:SerializedName("details")
        val details: OrderDetails? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @SerializedName("error")
        val error: Boolean? = null
)