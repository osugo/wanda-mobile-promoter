package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.errors.ErrorData

/**
 * Created by kombo on 14/05/2018.
 */
class PendingPaymentResponse {

    @SerializedName("error")
    var error: Boolean? = null

    @SerializedName("error_data")
    var errorData: ErrorData? = null
}