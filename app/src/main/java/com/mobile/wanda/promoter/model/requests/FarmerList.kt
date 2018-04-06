package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.responses.Farmer

/**
 * Created by kombo on 28/01/2018.
 */
data class FarmerList(@SerializedName("data") var farmers: ArrayList<Farmer>? = null)