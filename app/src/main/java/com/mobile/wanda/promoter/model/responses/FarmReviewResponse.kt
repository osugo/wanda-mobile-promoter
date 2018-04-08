package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.errors.FarmReviewError

/**
 * Created by kombo on 08/04/2018.
 */
data class FarmReviewResponse(
        @SerializedName("message")
        var message: String? = null,
        @SerializedName("error")
        var error: Boolean? = null,
        @SerializedName("error_data")
        var farmReviewError: FarmReviewError? = null
)