package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 14/05/2018.
 */
class PendingPaymentsList {
    @SerializedName("data")
    var pendingPayments: ArrayList<PendingPayment>? = null
}