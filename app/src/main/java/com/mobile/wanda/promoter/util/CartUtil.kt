package com.mobile.wanda.promoter.util

import com.mobile.wanda.promoter.model.CartItem
import com.mobile.wanda.promoter.model.orders.Product

/**
 * Created by kombo on 25/03/2018.
 */
object CartUtil {

    /**
     * convert product to cart item
     */
    fun getCartItem(product: Product, quantity: Int): CartItem {
        val cartItem = CartItem()

        with(cartItem){
            id = product.id
            name = product.name
            this.quantity = quantity
        }

        return cartItem
    }
}