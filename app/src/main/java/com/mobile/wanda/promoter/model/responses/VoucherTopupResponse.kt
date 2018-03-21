package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.errors.VoucherTopupErrors

/**
 * Created by kombo on 07/03/2018.
 */
data class VoucherTopupResponse(
        @SerializedName("error")
        val error: Boolean?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("data")
        val voucherTopupErrors: VoucherTopupErrors?
)