package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class Farm(

        @field:SerializedName("ward_name")
        val wardName: String? = null,

        @field:SerializedName("size")
        val size: Int? = null,

        @field:SerializedName("user_id")
        val userId: Int? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("farmer")
        val farmer: String? = null,

        @field:SerializedName("location")
        val location: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("ward")
        val ward: Ward? = null,

        @field:SerializedName("county_name")
        val countyName: String? = null,

        @field:SerializedName("ward_id")
        val wardId: Int? = null
)