package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 05/03/2018.
 */
data class FarmAuditDetails(
        @SerializedName("user_id") val userId: Long,
        @SerializedName("ward_id") val wardId: Long,
        @SerializedName("size") val size: String,
        @SerializedName("description") val description: String,
        @SerializedName("location") val location: String
)