package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class User(

	@SerializedName("updated_at")
	val updatedAt: String? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("created_at")
	val createdAt: String? = null,

	@SerializedName("current_team_id")
	val currentTeamId: Any? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("mobile_number")
	val mobileNumber: String? = null,

	@SerializedName("type")
	val type: String? = null,

	@SerializedName("email")
	val email: String? = null,

	@SerializedName("status")
	val status: String? = null
)