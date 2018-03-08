package com.mobile.wanda.promoter.model.orders

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 08/03/2018.
 */

data class ProductResults(
        @SerializedName("data") val items: ArrayList<Product>
)

data class Product(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String
)

open class Order(
        @SerializedName("farmer_id") val farmerId: Int? = 0,
        @SerializedName("items") val items: RealmList<OrderItem>? = RealmList()
) : RealmObject()

open class OrderItem(
        @SerializedName("variation_id") val variationId: Int? = 0,
        @SerializedName("quantity") val quantity: Int? = 0
) : RealmObject()

open class PendingOrder(
        @Ignore
        @SerializedName("error") val error: Boolean?,
        @SerializedName("message") val message: String?,
        @PrimaryKey
        @SerializedName("order_id") val orderId: Long? = 0,
        @SerializedName("status") val status: String
) : RealmObject()