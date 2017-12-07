package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 29/11/2017.
 */
data class VoucherTopUpRequest(
        @SerializedName("user_id")
        val user_id: Int,
        @SerializedName("payment_option")
        val paymentOption: String,
        @SerializedName("amount")
        val amount: Int
)