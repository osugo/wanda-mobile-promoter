package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.responses.Farmer

data class OrderDetails(

	@SerializedName("total_cost")
	val totalCost: Int? = null,

	@SerializedName("distribution-center-name")
	val distributionCenterName: String? = null,

	@SerializedName("can_cancel")
	val canCancel: Boolean? = null,

	@SerializedName("collection_center_id")
	val collectionCenterId: Int? = null,

	@SerializedName("created_at")
	val createdAt: CreatedAt? = null,

	@SerializedName("items_cost")
	val itemsCost: Int? = null,

	@SerializedName("collection-center")
	val collectionCenter: CollectionCenter? = null,

	@SerializedName("collection-center-name")
	val collectionCenterName: String? = null,

	@SerializedName("estimate_delivery_cost")
	val estimateDeliveryCost: Int? = null,

	@SerializedName("reference")
	val reference: String? = null,

	@SerializedName("total")
	val total: Int? = null,

	@SerializedName("delivery_cost")
	val deliveryCost: Int? = null,

	@SerializedName("distribution-center")
	val distributionCenter: DistributionCenter? = null,

	@SerializedName("user_id")
	val userId: Int? = null,

	@SerializedName("payment_id")
	val paymentId: Any? = null,

	@SerializedName("distribution_center_id")
	val distributionCenterId: Int? = null,

	@SerializedName("farmer")
	val farmer: Farmer? = null,

	@SerializedName("currency")
	val currency: String? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("items")
	val items: List<ProductOrder>? = null,

	@SerializedName("can_update")
	val canUpdate: Boolean? = null,

	@SerializedName("status")
	val status: String? = null,

	@SerializedName("farmer_name")
	val farmerName: String? = null
)