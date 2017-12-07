package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 27/11/2017.
 */
data class Order(
        @SerializedName("farmer_id")
        val farmerId: Int,
        @SerializedName("items")
        val items: ArrayList<OrderItem>
)