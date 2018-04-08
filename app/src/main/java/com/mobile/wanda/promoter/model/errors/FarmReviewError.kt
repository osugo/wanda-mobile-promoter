package com.mobile.wanda.promoter.model.errors

import com.google.gson.annotations.SerializedName

data class FarmReviewError(

	@field:SerializedName("farm_id")
	var farmId: List<String>? = null,

    @SerializedName("location")
    var location: List<String>? = null,

    @SerializedName("comments")
    var comments: List<String>? = null
)