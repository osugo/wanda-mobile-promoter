package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class OrderDetails(
        @SerializedName("total_cost")
        val totalCost: Double? = null,

        @SerializedName("collection_center_id")
        val collectionCenterId: String? = null,

        @SerializedName("items_cost")
        val itemsCost: Any? = null,

        @SerializedName("estimate_delivery_cost")
        val estimateDeliveryCost: Double? = null,

        @SerializedName("reference")
        val reference: String? = null,

        @SerializedName("total")
        val total: Double? = null,

        @SerializedName("delivery_cost")
        val deliveryCost: Double? = null,

        @SerializedName("user_id")
        val userId: Int? = null,

        @SerializedName("payment_id")
        val paymentId: Int? = null,

        @SerializedName("distribution_center_id")
        val distributionCenterId: Int? = null,

        @SerializedName("currency")
        val currency: String? = null,

        @SerializedName("id")
        val id: Long? = null,

        @SerializedName("items")
        val items: List<Any?>? = null,

        @SerializedName("user")
        val user: User? = null,

        @SerializedName("status")
        val status: String? = null
)