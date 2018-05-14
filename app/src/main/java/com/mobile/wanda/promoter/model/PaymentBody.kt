package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

class PaymentBody(
        @field:SerializedName("promoter_payment_id")
        var promoterPaymentId: Int? = null
)