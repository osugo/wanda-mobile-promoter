package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.errors.FarmerRegistrationErrors

/**
 * Created by kombo on 03/01/2018.
 */
data class FarmerRegistrationResponse(
        @SerializedName("error")
        val error: Boolean,
        @SerializedName("message")
        val registrationErrors: FarmerRegistrationErrors?
)