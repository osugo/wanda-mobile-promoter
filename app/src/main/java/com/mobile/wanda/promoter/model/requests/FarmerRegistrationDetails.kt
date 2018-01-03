package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 27/11/2017.
 */
data class FarmerRegistrationDetails(
        @SerializedName("name")
        val name: String,
        @SerializedName("mobile_number")
        val number: String,
        @SerializedName("farmer-collection-center")
        val collectionCenter: String,
        @SerializedName("farmer-ward")
        val farmerWard: String
)