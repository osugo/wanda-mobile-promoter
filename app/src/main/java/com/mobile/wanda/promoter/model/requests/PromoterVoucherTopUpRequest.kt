package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 21/03/2018.
 */
data class PromoterVoucherTopUpRequest(@SerializedName("payment_option") val paymentOption: String, @SerializedName("amount") val amount: String)