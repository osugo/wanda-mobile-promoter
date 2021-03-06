package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.errors.FarmAuditErrors

/**
 * Created by kombo on 05/03/2018.
 */
data class FarmCreationResponse(
        @SerializedName("error")
        val error: Boolean?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("errorData")
        val farmAuditErrors: FarmAuditErrors?
)