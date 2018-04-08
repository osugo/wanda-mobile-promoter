package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 08/04/2018.
 */
class FarmReview(
        @SerializedName("farm_id")
        var farmId: Int? = null,
        @SerializedName("location")
        var location: String? = null,
        @SerializedName("comments")
        var comments: String? = null
)