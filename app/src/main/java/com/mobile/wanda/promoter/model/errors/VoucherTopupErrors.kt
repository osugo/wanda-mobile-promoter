package com.mobile.wanda.promoter.model.errors

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 07/03/2018.
 */
data class VoucherTopupErrors(
        @SerializedName("user_id") val userId: String?,
        @SerializedName("payment_option") val paymentOption: String?,
        @SerializedName("amount") val amount: String?
)