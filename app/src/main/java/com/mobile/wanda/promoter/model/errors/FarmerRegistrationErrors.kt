package com.mobile.wanda.promoter.model.errors

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 03/01/2018.
 */
data class FarmerRegistrationErrors(
        @SerializedName("name")
        val name: ArrayList<String>?,
        @SerializedName("mobile_number")
        val phone: ArrayList<String>?,
        @SerializedName("farmer-collection-center")
        val collectionCenter: ArrayList<String>?,
        @SerializedName("farmer-ward")
        val ward: ArrayList<String>?
)