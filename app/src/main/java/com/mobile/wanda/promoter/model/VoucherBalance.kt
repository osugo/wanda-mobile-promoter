package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 29/11/2017.
 */
data class VoucherBalance(
        @SerializedName("balance")
        val balance: Long
)