package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 02/05/2018.
 */
class FarmList(
        @SerializedName("data")
        var farms: ArrayList<UserFarm>? = null
)

class UserFarm(
        @SerializedName("id")
        var id: Int? = null,
        @SerializedName("name")
        var name: String? = null
)