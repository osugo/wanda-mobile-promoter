package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class PendingOrder(
        @SerializedName("orderDetails")
        val orderDetails: OrderDetails? = null,

        @SerializedName("message")
        val message: String? = null
)