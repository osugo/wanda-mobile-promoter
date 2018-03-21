package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 29/11/2017.
 */
data class FarmerVoucherTopUpRequest(
        @SerializedName("user_id")
        val user_id: Long,
        @SerializedName("payment_option")
        val paymentOption: String,
        @SerializedName("amount")
        val amount: String
)