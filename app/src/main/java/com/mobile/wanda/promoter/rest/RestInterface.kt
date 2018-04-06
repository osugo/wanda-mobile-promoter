package com.mobile.wanda.promoter.rest

import com.mobile.wanda.promoter.model.OrderPlacementRequest
import com.mobile.wanda.promoter.model.PendingOrder
import com.mobile.wanda.promoter.model.orders.Order
import com.mobile.wanda.promoter.model.orders.ProductResults
import com.mobile.wanda.promoter.model.requests.*
import com.mobile.wanda.promoter.model.responses.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by kombo on 23/11/2017.
 */

interface RestInterface {

    @GET("vouchers/balance")
    fun voucherBalance(): Observable<VoucherBalance>

    @POST("/wanda-mobile/oauth/token")
    fun login(@Body loginCredentials: LoginCredentials): Observable<AuthCredentials>

    @POST("farmers/register")
    fun registerFarmer(@Body registrationDetails: FarmerRegistrationDetails): Observable<FarmerRegistrationResponse>

    @POST("farmers/add-farm")
    fun addFarm(@Body auditDetails: FarmAuditDetails): Observable<FarmCreationResponse>

    @GET("products/categories?")
    fun getProductCategories(): Observable<ProductResults>

    @GET("products/types")
    fun searchProducts(@Query("category_id") categoryId: Long): Observable<ProductResults>

    @GET("products/products")
    fun getProductVariations(@Query("type_id") typeId: Long): Observable<ProductResults>

    @POST("farmers/orders/create")
    fun createOrder(@Body order: Order): Observable<PendingOrder>

    @POST("farmers/orders/place")
    fun placeOrder(@Body request: OrderPlacementRequest): Observable<PendingOrder>

    @POST("payments/pay-for-order")
    fun payOrder(@Body orderPayment: OrderPayment): Observable<PaymentResponse>

    @POST("payments/top-up-farmer-voucher")
    fun farmerVoucherTopUp(@Body farmerVoucherTopUpRequest: FarmerVoucherTopUpRequest): Observable<VoucherTopupResponse>

    @POST("payments/top-up-promoter-voucher")
    fun promoterVoucherTopUp(@Body promoterVoucherTopUpRequest: PromoterVoucherTopUpRequest): Observable<VoucherTopupResponse>

    @GET("commissions/check")
    fun checkCommission(): Observable<Commission>

    @POST("commissions/request-payment")
    fun requestCommission(): Observable<CommissionRequestResponse>

    @GET("lookup/farmer?search")
    fun getFarmers(): Observable<FarmerList>

    @GET("lookup/ward?search")
    fun getWards(): Observable<WardList>
}
