package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 03/01/2018.
 */
data class FarmerRegistrationResponse(
        @SerializedName("error")
        var error: Boolean?,
        @SerializedName("message")
        var message: String?
//        @SerializedName("data")
//        var registrationErrors: FarmerRegistrationErrors?
)