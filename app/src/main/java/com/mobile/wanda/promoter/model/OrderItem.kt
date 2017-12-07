package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 27/11/2017.
 */
data class OrderItem(
        @SerializedName("variation_id")
        val variationId: Int,
        @SerializedName("quantity")
        val quantity: Int
)