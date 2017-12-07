package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 27/11/2017.
 */
data class FarmerRegistrationDetails(
        @SerializedName("name")
        val name: String,
        @SerializedName("mobile_number")
        val number: String,
        @SerializedName("farmer_collection_center")
        val collectionCenter: String,
        @SerializedName("farmer_ward")
        val farmerWard: String
)