package com.mobile.wanda.promoter.model.errors

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 05/03/2018.
 */
data class FarmAuditErrors(
        @SerializedName("user_id") val userId: ArrayList<String>?,
        @SerializedName("ward_id") val wardId: ArrayList<String>?,
        @SerializedName("size") val size: ArrayList<String>?,
        @SerializedName("description") val description: ArrayList<String>?,
        @SerializedName("location") val location: ArrayList<String>?
)