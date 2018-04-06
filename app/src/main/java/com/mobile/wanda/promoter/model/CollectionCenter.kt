package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class CollectionCenter(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)